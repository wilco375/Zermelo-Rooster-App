package com.wilco375.roosternotification.exception

class StatusException : Exception {
    val status: Int

    constructor(status: Int) : super() {
        this.status = status
    }

    constructor(status: Int, message: String?) : super(message) {
        this.status = status
    }
}