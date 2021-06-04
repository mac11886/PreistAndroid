package com.example.jitsi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import org.jitsi.meet.sdk.*
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ProfileAcitivity : BaseActivity() {

    lateinit var dateText: TextView
    lateinit var timeText: TextView
    lateinit var callBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_acitivity)

        listenForEvents()

        init()
        dateAndTime()
//        onButtonClick()
        callBtn.setOnClickListener {
            onButtonClick()
        }
        val android_id = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
//        Toast.makeText(this, "Id:" + android_id, Toast.LENGTH_LONG).show()
        // Initialize default options for Jitsi Meet conferences.
        val serverURL: URL
        serverURL = try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            URL("https://priest-core.kisra.co.th/calling/")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            // When using JaaS, set the obtained JWT here
            //.setToken("MyJWT")
            // Different features flags can be set
            //.setFeatureFlag("toolbox.enabled", false)
            //.setFeatureFlag("filmstrip.enabled", false)
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        registerForBroadcastMessages()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }

    // Example for handling different JitsiMeetSDK events
    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.getType()) {
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i(
                    "Conference Joined with url%s",
                    event.getData().get("url")
                )
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i(
                    "Participant joined%s",
                    event.getData().get("name")
                )
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.action);
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.action);
                ... other events
         */
        for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    // Example for sending actions to JitsiMeetSDK
    private fun hangUp() {
        val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(org.webrtc.ContextUtils.getApplicationContext())
            .sendBroadcast(hangupBroadcastIntent)
    }

    fun onButtonClick() {

//        val editText = findViewById<EditText>(R.id.conferenceName)
        //should set keyId this
//        if ()
        var device = intent.getStringExtra("device").toString()
        var ward = "/ward-1"
        var text = "$device" +"$ward"
        Log.i("link", text)
        if (text.length > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(text)
                // Settings for audio and video
                //.setAudioMuted(true)
                //.setVideoMuted(true)
                .setFeatureFlag("kick-out.enabled", false).setFeatureFlag("chat.enabled", false).setFeatureFlag("invite.enabled",false)
                .setFeatureFlag("raise-hand.enabled",false).setFeatureFlag("video-shared.enabled",false).setFeatureFlag("pip.enabled",false)
                .setFeatureFlag("overflow-menu.enabled",false).setFeatureFlag("toolbox.more",false).setFeatureFlag("security-options.enabled",false)
                .setFeatureFlag("title-view.enabled",false).setFeatureFlag("close-captions.enabled",false).setFeatureFlag("filmstrip.enabled",false)
                .build()
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            //lauch room

            JitsiMeetActivity.launch(this, options)
        }
    }

    fun init() {
        dateText = findViewById(R.id.dateText)
        timeText = findViewById(R.id.timeText)
        callBtn = findViewById(R.id.button4)
    }

    fun dateAndTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date()).also { dateText.text = it }
        }

        val someHandler =
            Handler(mainLooper)
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                timeText.setText(java.text.SimpleDateFormat("HH:mm:ss", Locale.US).format(Date()))
                someHandler.postDelayed(this, 1000)
            }
        }, 10)
    }
}