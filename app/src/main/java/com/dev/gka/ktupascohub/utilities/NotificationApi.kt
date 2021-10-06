package com.dev.gka.ktupascohub.utilities

import com.dev.gka.ktupascohub.models.PushNotification
import com.dev.gka.ktupascohub.utilities.Constants.CONTENT_TYPE
import com.dev.gka.ktupascohub.utilities.Constants.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification):
            Response<ResponseBody>
}