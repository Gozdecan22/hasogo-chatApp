package com.buildapp.hasogo.util

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber

object FirebaseUtils {
    const val CHANNEL_ID = "channel_id"
    const val CHANNEL_NAME = "channel_name"

    fun sendNotification(context: Context, name: String, message: String, token: String) {

        try {
            val queue: RequestQueue = Volley.newRequestQueue(context)
            val url = "https://fcm.googleapis.com/fcm/send"
            val data = JSONObject()
            data.put("title", name)
            data.put("body", message)
            val notificationData = JSONObject()
            notificationData.put("notification", data)
            notificationData.put("to", token)

            Timber.e("Notification: ${Gson().toJson(notificationData)}")

            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, url, notificationData,
                Response.Listener<JSONObject?> {
                    //
                }, Response.ErrorListener {
                    Timber.e("Notification: ${it.message}")
                }) {


                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val key = "Key=${com.buildapp.hasogo.util.Constants.SERVER_CLOUD_MESSAGING_KEY}"
                    val params = hashMapOf<String, String>()
                    params["Content-Type"] = "application/json";
                    params["Authorization"] = key;
                    return params
                }
            }
            queue.add(request)
        } catch (ex: Exception) {
        }
    }


}


