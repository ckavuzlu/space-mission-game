package com.example.spacemission.model

import java.lang.Exception

open class ErrorModel(
    val message: String? = null,
    val code: Int? = null,
    val exception : Exception? = null
)