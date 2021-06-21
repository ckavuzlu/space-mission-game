package com.example.spacemission.model

sealed class RepoResponse<out R : Any, out E : ErrorModel> {
    class Data<out R : Any>(val data: R) : RepoResponse<R, Nothing>()
    class Error<out E : ErrorModel>(val error: E) : RepoResponse<Nothing, E>()
}