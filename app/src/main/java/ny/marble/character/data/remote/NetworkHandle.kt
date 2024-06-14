package ny.marble.character.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException

fun <T : Any> handleFlowApi(
    execute: suspend () -> T,
): Flow<ApiResult<T>> = flow {
    emit(ApiResult.Loading) //값 갱신전 로딩을 emit
//    delay(1000) // (1초대기)
    try {
        emit(ApiResult.Success(execute())) // execute 성공시 해당값을 Success에 담아서 반환
    } catch (e: HttpException) {
        emit(
            ApiResult.Fail.Error(
                code = e.code(),
                message = e.message()
            )
        ) // 네트워크 오류시 code와 메세지를 반환
    } catch (e: Exception) {
        emit(ApiResult.Fail.Exception(e = e)) // 예외 발생시 해당 에러를 반환
    }
}.flowOn(Dispatchers.Default)

suspend fun <T : Any> handleApi(
    execute: suspend () -> T,
): ApiResult<T> {

    return try {
        ApiResult.Success(execute())
    } catch (e: HttpException) {

        ApiResult.Fail.Error(
            code = e.code(),
            message = e.message()
        )

    } catch (e: Exception) {
        return ApiResult.Fail.Exception(e = e)
    }
}