package com.wilco375.roosternotification.online

import com.wilco375.roosternotification.`object`.items
import com.wilco375.roosternotification.exception.InvalidCodeException
import com.wilco375.roosternotification.exception.InvalidWebsiteException
import com.wilco375.roosternotification.exception.StatusException
import com.wilco375.roosternotification.exception.UnknownAuthenticationException
import cz.msebera.android.httpclient.NameValuePair
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import cz.msebera.android.httpclient.message.BasicNameValuePair
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LiveApi(private val website: String) : Api {

    override fun getSchedule(token: String, username: String, start: Long, end: Long): JSONArray? {
        try {
            val response = doGetRequest("https://$website/api/v2/appointments?user=$username&start=$start&end=$end&valid=true&fields=appointmentInstance,subjects,cancelled,locations,startTimeSlot,start,end,groups,type,branchOfSchool,teachers&access_token=$token")
            return JSONObject(response).getJSONObject("response").getJSONArray("data")
        } catch (e: Exception) {
            return null
        }
    }

    override fun getNames(token: String, schools: Set<String>): Map<String, String> {
        val types = arrayOf("isEmployee", "isStudent")

        val names = HashMap<String, String>()

        for (type in types) {
            var status: Int
            val fields = arrayListOf("firstName", "prefix", "lastName")
            do {
                try {
                    val response = doGetRequest("https://$website/api/v2/users?archived=false&$type=true&schoolInSchoolYear=${schools.joinToString(",")}&fields=${fields.joinToString(",")},code&access_token=$token")
                    val data = JSONObject(response).getJSONObject("response").getJSONArray("data")
                    for (item in data.items()) {
                        if (item is JSONObject) {
                            val code = item.optString("code", "")
                            if (code.isNotEmpty()) {
                                val name = fields // Combine first name, prefix and last name
                                        .map { field -> item.optString(field, "") }
                                        .filter { it.isNotBlank() && it != "null" }
                                        .joinToString(" ")
                                        .capitalize(Locale.getDefault())

                                names[code] = name
                            }
                        }
                    }
                    status = 200
                } catch (e: Exception) {
                    if (e is StatusException) {
                        status = e.status
                        fields.removeAt(0)
                    } else {
                        status = 500
                        e.printStackTrace()
                    }
                }
            } while (status == 403 && fields.size > 0) // Try again with only last name, that may be allowed
        }

        return names
    }

    @Throws(UnknownAuthenticationException::class, InvalidWebsiteException::class, InvalidCodeException::class)
    override fun getToken(code: String): String? {
        try {
            val parameters: HashMap<String, String> = HashMap()
            parameters["grant_type"] = "authorization_code"
            parameters["code"] = code
            val response = doPostRequest("https://$website/api/v2/oauth/token?", parameters)

            val tokenJson = JSONObject(response)
            return tokenJson.getString("access_token")
        } catch (e: StatusException) {
            if (e.status == 404) {
                throw InvalidWebsiteException()
            } else {
                throw InvalidCodeException()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            throw UnknownAuthenticationException("Error getting API token: JSON is invalid: " + e.message)
        } catch (e: IOException) {
            e.printStackTrace()
            throw UnknownAuthenticationException("Error getting API token: IOException: " + e.message)
        }
    }


    @Throws(IOException::class, StatusException::class)
    private fun doGetRequest(url: String): String {
        val client = HttpClientBuilder.create().build()

        val get = HttpGet(url)
        val response = client.execute(get)
        val statusCode = response.statusLine.statusCode

        if (statusCode == 200) {
            val br = BufferedReader(InputStreamReader(response.entity.content))
            val text = br.readText()
            br.close()
            return text
        } else {
            throw StatusException(statusCode)
        }
    }

    @Throws(IOException::class, StatusException::class)
    private fun doPostRequest(url: String, variables: Map<String, String>): String {
        val client = HttpClientBuilder.create().build()

        val post = HttpPost(url)

        val nameValuePair = ArrayList<NameValuePair>(variables.size)
        variables.entries.forEach { nameValuePair.add(BasicNameValuePair(it.key, it.value)) }

        post.entity = UrlEncodedFormEntity(nameValuePair)
        val response = client.execute(post)
        val statusCode = response.statusLine.statusCode

        if (statusCode == 200) {
            val br = BufferedReader(InputStreamReader(response.entity.content))
            val text = br.readText()
            br.close()
            return text
        } else {
            throw StatusException(statusCode)
        }
    }
}