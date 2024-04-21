package com.ahmadhassan.i210403

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object UserInstance {
    private var currentUser: User? = null
    private const val baseUrl = "http://${DatabaseIP.IP}"

    fun getInstance(): User? {
        return currentUser
    }

    fun fetchUser(context: Context, userId: String, callback: (User?) -> Unit) {
        if (currentUser != null) {
            Log.d("UserInstance", "Using already stored User")
            callback(getInstance())
            return
        }
        if(userId == ""){
            callback(null)
            return
        }

        val url = "$baseUrl/getuser.php"
        val jsonObject = JSONObject().apply {
            put("userId", userId)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url,jsonObject,
            { response ->
                try {
                    val userJson = response.getJSONObject("user")
                    val user = User(
                        userJson.getString("userId"),
                        userJson.getString("email"),
                        userJson.getString("fullName"),
                        userJson.getString("city"),
                        userJson.getString("country"),
                        userJson.getString("profilePic"),
                        userJson.getString("bannerPic"),
                        userJson.getString("fcmToken")
                    )
                    currentUser = user
                    callback(user)
                    Log.d("UserInstance", "User: $user")
                } catch (e: JSONException) {
                    Log.d("UserInstance", "Error parsing JSON: ${e.message}")
                    callback(null)
                }
            },
            { error ->
                Log.d("UserInstance", "Error: ${error.message}")
                callback(null)
            }
        )

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    fun updateUser(context: Context, updatedUser: User, callback: (Boolean) -> Unit) {
        val url = "$baseUrl/updateuser.php"

        val jsonObject = JSONObject().apply {
            put("userId", updatedUser.userId)
            put("email", updatedUser.email)
            put("city", updatedUser.city)
            put("country", updatedUser.country)
            put("fullName", updatedUser.fullName)
            put("profilePic", updatedUser.profilePic)
            put("bannerPic", updatedUser.bannerPic)
            put("fcmToken", updatedUser.fcmToken)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        currentUser = updatedUser
                        callback(true)
                    } else {
                        callback(false)
                    }
                } catch (e: JSONException) {
                    Log.d("UserInstance", "Error parsing JSON: ${e.message}")
                    callback(false)
                }
            },
            { error ->
                Log.d("UserInstance", "Error: ${error.message}")
                callback(false)
            }
        )

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    fun setFCMToken(context: Context, token: String, callback: (Boolean) -> Unit) {
        if (currentUser == null) {
            callback(false)
            return
        }

        val url = "$baseUrl/setfcmtoken.php"

        val jsonObject = JSONObject().apply {
            put("userId", currentUser!!.userId)
            put("fcmToken", token)
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        currentUser!!.fcmToken = token
                        callback(true)
                    } else {
                        callback(false)
                    }
                } catch (e: JSONException) {
                    Log.d("UserInstance", "Error parsing JSON: ${e.message}")
                    callback(false)
                }
            },
            { error ->
                Log.d("UserInstance", "Error: ${error.message}")
                callback(false)
            }
        )

        Volley.newRequestQueue(context).add(jsonObjectRequest)
    }

    fun clearInstance() {
        currentUser = null
    }
}
