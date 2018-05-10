package com.wilco375.roosternotification.exception

class UnknownAuthenticationException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
}