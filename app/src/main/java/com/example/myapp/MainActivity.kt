package com.example.myapp

//import android.content.ContentValues.TAG
//import android.content.Context
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
//import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
//import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val TOPIC = "/topics/myTopic2"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            findViewById<EditText>(R.id.etToken).setText(it)
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        findViewById<Button>(R.id.btnSend).setOnClickListener {
            val title = findViewById<EditText>(R.id.etTitle).text.toString()
            val message = findViewById<EditText>(R.id.etMessage).text.toString()
            val recipientToken = findViewById<EditText>(R.id.etToken).text.toString()
            if(title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, message),
                    recipientToken
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}