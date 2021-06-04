package com.example.jitsi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.lifecycle.Observer

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

            EVENT_MESSAGE += editText.text
            Toast.makeText(this, EVENT_MESSAGE,Toast.LENGTH_LONG).show()
//            listenForEvents()
//            EVENT_MESSAGE = ".calling.device."
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
                val newText = event.room + "\n" + this.text.toString()
                text = newText
            }
        }
    }

    fun buttonToProfile() {
        Intent(this, ProfileAcitivity::class.java).also { intent ->
            intent.putExtra("device", EVENT_MESSAGE)
            startActivity(intent) }
    }

    fun init() {
        button = findViewById(R.id.insertKeyBtn)
        editText = findViewById(R.id.insertKeyText)
        setText = findViewById(R.id.setText)
    }


}