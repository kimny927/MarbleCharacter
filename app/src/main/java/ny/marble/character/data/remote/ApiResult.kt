package ny.marble.character.data.remote

sealed class ApiResult<out T> {
    data object Loading : ApiResult<Nothing>()

    data class Success<out T>(val data: T) : ApiResult<T>()

    sealed class Fail : ApiResult<Nothing>() {
        data class Error(val code: Int, val message: String?) : Fail()
        data class Exception(val e: Throwable) : Fail()
    }
}

fun <T> ApiResult<T>.onLoading(execute: () -> Unit): ApiResult<T> {
    if (this is ApiResult.Loading) {
        execute()
    }
    return this
}

fun <T> ApiResult<T>.onSuccess(execute: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) {
        execute(this.data)
    }
    return this
}

fun <T> ApiResult<T>.onFail(execute: (code: Int?, message: String?, e: Throwable?) -> Unit): ApiResult<T> {
    val code = if (this is ApiResult.Fail.Error) this.code else null
    val message = if (this is ApiResult.Fail.Error) this.message else null
    val e = if (this is ApiResult.Fail.Exception) this.e else null
    execute(code, message, e)
    return this
}

fun <T> ApiResult<T>.onError(execute: (code: Int, message: String?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Fail.Error) {
        execute(code, message)
    }
    return this
}

fun <T> ApiResult<T>.onException(execute: (e: Throwable) -> Unit): ApiResult<T> {
    if (this is ApiResult.Fail.Exception) {
        execute(e)
    }
    return this
}


