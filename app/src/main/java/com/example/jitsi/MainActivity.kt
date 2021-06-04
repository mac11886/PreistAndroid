package com.example.jitsi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import net.mrbin99.laravelechoandroid.Echo

import net.mrbin99.laravelechoandroid.EchoCallback
import net.mrbin99.laravelechoandroid.EchoOptions
import org.jitsi.meet.sdk.*
import java.io.*

class MainActivity : BaseActivity() {
    lateinit var button: Button
    lateinit var editText: EditText
    lateinit var setText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        connectToSocket()
        initLiveDataListener()
        button.setOnClickListener {
            buttonToProfile()
        }
    }

    private fun initLiveDataListener() {
        receivedEvent.observe(this, Observer {
            displayEventData(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectFromSocket()
    }

    private fun displayEventData(event: Any) {
        if (event is MessageCreated) {
            setText.apply {
                val newText = event.message + "\n" + this.text.toString()
                text = newText
            }
        }
    }

    fun buttonToProfile() {
        Intent(this, ProfileAcitivity::class.java).also { intent -> startActivity(intent) }
    }

    fun init() {
        button = findViewById(R.id.insertKeyBtn)
        editText = findViewById(R.id.insertKeyText)
        setText = findViewById(R.id.setText)
    }


}