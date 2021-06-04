package com.example.jitsi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import net.mrbin99.laravelechoandroid.Echo
import net.mrbin99.laravelechoandroid.EchoCallback
import net.mrbin99.laravelechoandroid.EchoOptions
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL

const val SERVER_URL = "http://147.50.152.134:6001"
const val CHANNEL_MESSAGES = "api_database_calling"
var EVENT_MESSAGE  = ".calling.device.1"
const val TAG = "msg"
open class BaseActivity :AppCompatActivity(){
    private var _receivedEvent = MutableLiveData<Any>()
    var receivedEvent = _receivedEvent
    lateinit var eventRoom : String
    lateinit var eventFrom : String
    lateinit var eventTo : String
    lateinit var eventStatus : String

    private var echo: Echo? = null

    protected fun connectToSocket() {
        val options = EchoOptions()
        options.host = SERVER_URL

        echo = Echo(options)
        echo?.connect(object : EchoCallback {
            override fun call(vararg args: Any?) {
                log("successful connect")
                listenForEvents()
            }
        }, object : EchoCallback {
            override fun call(vararg args: Any?) {
                log("error while connecting: " + args.toString())
            }
        })
    }

    protected fun listenForEvents() {
        echo?.let {
            it.channel(CHANNEL_MESSAGES)
                .listen(EVENT_MESSAGE) {
                    Log.i("JAY",it.toString())
                    val newEvent = MessageCreated.parseFrom(it)
                    eventFrom = newEvent?.from.toString()
                    eventRoom = newEvent?.room.toString()
                    eventStatus = newEvent?.status.toString()
                    eventTo = newEvent?.to.toString()
                    if (newEvent != null){
                        launchRoom()
                    }

                    Log.i("JAY",newEvent?.from.toString())
                    Log.i("JAY",newEvent?.to.toString())
                    Log.i("JAY",newEvent?.room.toString())
                    Log.i("JAY",newEvent?.status.toString())
//                    launchRoom()
//                    EVENT_MESSAGE
//                    displayNewEvent(newEvent)
                }
        }
    }

    protected fun disconnectFromSocket() {
        echo?.disconnect()
    }

    private fun log(message: String) {
        Log.i(TAG, message)
    }
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }
    private fun displayNewEvent(event: MessageCreated?) {
//        Log.i("new event ", event?.message.toString())
        _receivedEvent.postValue(event)
    }
    fun launchRoom(){
        val serverURL: URL
        serverURL = try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            URL("https://zoom.kisra.co.th")
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
        var text = eventRoom
        if (text.length > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(text)
                // Settings for audio and video
                //.setAudioMuted(true)
                //.setVideoMuted(true)
//                .setFeatureFlag("kick-out.enabled", false).setFeatureFlag("chat.enabled", false)
//                .setFeatureFlag("invite.enabled", false)
//                .setFeatureFlag("raise-hand.enabled", false)
//                .setFeatureFlag("video-shared.enabled", false).setFeatureFlag("pip.enabled", false)
//                .setFeatureFlag("overflow-menu.enabled", false)
//                .setFeatureFlag("toolbox.more", false)
//                .setFeatureFlag("security-options.enabled", false)
//                .setFeatureFlag("title-view.enabled", false)
//                .setFeatureFlag("close-captions.enabled", false)
//                .setFeatureFlag("filmstrip.enabled", false)
                .build()
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            //lauch room

            JitsiMeetActivity.launch(this, options)
        }
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
}