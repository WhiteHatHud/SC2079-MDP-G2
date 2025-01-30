package com.application.controller.bluetooth

import android.R
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.controller.MainActivity
import com.google.android.material.snackbar.Snackbar
import java.util.Timer
import java.util.TimerTask

class CommunicationFragment : Fragment() {
    /**
    private var communicationViewModel: CommunicationViewModel? = null

    private var timer: Timer? = null
    private var updateReceivedDataTimerTask: UpdateReceivedDataTimerTask? = null
    private var textViewPersistentCommunicationString1: TextView? = null
    private var textViewPersistentCommunicationString2: TextView? = null
    private var textViewVolatileCommunicationString: TextView? = null
    private var textViewReceivedStrings: TextView? = null
    private var persistentStringSendButton1: Button? = null
    private var persistentStringSendButton2: Button? = null
    private var volatileStringSendButton: Button? = null
    private var receivedDataClearButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        communicationViewModel =
            ViewModelProvider(this).get(CommunicationViewModel::class.java)

        // Update receive data
        timer = Timer()
        updateReceivedDataTimerTask = UpdateReceivedDataTimerTask()

        val root: View = inflater.inflate(R.layout.fragment_communication, container, false)
        textViewPersistentCommunicationString1 =
            root.findViewById<TextView>(R.id.editTextCommunicationString1)
        textViewPersistentCommunicationString2 =
            root.findViewById<TextView>(R.id.editTextCommunicationString2)
        textViewVolatileCommunicationString =
            root.findViewById<TextView>(R.id.editTextCommunicationString)
        textViewReceivedStrings = root.findViewById<TextView>(R.id.textViewReceivedStrings)
        persistentStringSendButton1 = root.findViewById<Button>(R.id.stringSendButton1)
        persistentStringSendButton2 = root.findViewById<Button>(R.id.stringSendButton2)
        volatileStringSendButton = root.findViewById<Button>(R.id.stringSendButton)
        receivedDataClearButton = root.findViewById<Button>(R.id.receivedDataClearButton)

        textViewReceivedStrings.setMovementMethod(ScrollingMovementMethod())
        textViewReceivedStrings.setText(RECEIVED_DATA_PLACEHOLDER)

        persistentStringSendButton1.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(
                view,
                SENT_PERSISTENT_STRING + textViewPersistentCommunicationString1.getText()
                    .toString(),
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
            MainActivity.sendCommunicationMessage(
                textViewPersistentCommunicationString1.getText().toString()
            )
        })
        persistentStringSendButton2.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(
                view,
                SENT_PERSISTENT_STRING + textViewPersistentCommunicationString2.getText()
                    .toString(),
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
            MainActivity.sendCommunicationMessage(
                textViewPersistentCommunicationString2.getText().toString()
            )
        })

        volatileStringSendButton.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(
                view,
                SENT_VOLATILE_STRING + textViewVolatileCommunicationString.getText().toString(),
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
            MainActivity.sendCommunicationMessage(
                textViewVolatileCommunicationString.getText().toString()
            )
        })

        receivedDataClearButton.setOnClickListener(View.OnClickListener {
            MainActivity.resetReceivedTextStrings()
            textViewReceivedStrings.setText(RECEIVED_DATA_PLACEHOLDER)
            Log.d(
                COMMUNICATION_FRAGMENT_TAG,
                "Reset received data: " + textViewReceivedStrings.getText()
            )
        })

        timer!!.schedule(
            updateReceivedDataTimerTask,
            RECEIVE_DATA_UPDATE_DELAY.toLong(),
            RECEIVE_DATA_UPDATE_INTERVAL.toLong()
        )

        return root
    }

    override fun onPause() {
        super.onPause()

        // Write persistent strings in SharedPreferences
        // https://stackoverflow.com/questions/21720089/how-do-i-use-shared-preferences-in-a-fragment-on-android
        val sharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(
            PERSISTENT_STRING_KEY_1,
            "" + textViewPersistentCommunicationString1!!.text
        )
        editor.putString(
            PERSISTENT_STRING_KEY_2,
            "" + textViewPersistentCommunicationString2!!.text
        )
        editor.apply()
    }

    override fun onResume() {
        super.onResume()

        // Get persistent strings from SharedPreferences
        // https://stackoverflow.com/questions/21720089/how-do-i-use-shared-preferences-in-a-fragment-on-android
        val sharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        val communicationStringValue1 =
            sharedPreferences.getString(PERSISTENT_STRING_KEY_1, PERSISTENT_STRING_DEFAULT_1)
        val communicationStringValue2 =
            sharedPreferences.getString(PERSISTENT_STRING_KEY_2, PERSISTENT_STRING_DEFAULT_2)
        textViewPersistentCommunicationString1!!.text = communicationStringValue1
        textViewPersistentCommunicationString2!!.text = communicationStringValue2
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////           Update Received Data           ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    internal inner class UpdateReceivedDataTimerTask : TimerTask() {
        override fun run() {
            try {
                activity!!.runOnUiThread {
                    if (!MainActivity.getReceivedTextStrings().isEmpty()) {
                        textViewReceivedStrings.setText(MainActivity.getReceivedTextStrings())
                    }
                }
            } catch (ignored: NullPointerException) {
            } catch (exception: Exception) {
                Log.d(COMMUNICATION_FRAGMENT_TAG, exception.localizedMessage)
            }
        }
    }

    companion object {
        private const val COMMUNICATION_FRAGMENT_TAG = "CommunicationFragment"

        // Snackbar messages
        private const val SENT_PERSISTENT_STRING = "Sent persistent string: "
        private const val SENT_VOLATILE_STRING = "Sent volatile string: "

        private const val PERSISTENT_STRING_KEY_1 = "persistent_string_1"
        private const val PERSISTENT_STRING_KEY_2 = "persistent_string_2"
        private const val PERSISTENT_STRING_DEFAULT_1 = "This is persistent text string 1"
        private const val PERSISTENT_STRING_DEFAULT_2 = "This is persistent text string 2"
        private const val RECEIVED_DATA_PLACEHOLDER = "Your received text strings will appear here"

        private const val RECEIVE_DATA_UPDATE_DELAY = 0
        private const val RECEIVE_DATA_UPDATE_INTERVAL = 500
    }
    **/
}