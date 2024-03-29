package com.example.coroutinekotlin

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    val JOB_TIMEOUT = 1900L
    private val Result_1 = "Result #1"
    private val Result_2 = "Result #2"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            CoroutineScope(IO).launch {
                fakeApiRequest()
            }

        }
    }


    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun fakeApiRequest() {

        withContext(IO) {
            val job = withTimeoutOrNull(JOB_TIMEOUT) {
                val result1 = getResult1FromApi() // wait
                println("debug: $result1")
                setTextOnMainThread(result1)
                val result2 = getResult2FromApi() // wait
                setTextOnMainThread(result2)
                println("debug: result #1: $result1")
            } // wait
            if (job == null) {
                val cancelMessage = "Cancelling job... Job took longer than $JOB_TIMEOUT ms"
                println("debug: $cancelMessage")
                setTextOnMainThread(cancelMessage)
            }
        }
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResultFromApi")
        delay(1000)
        Thread.sleep(1000)
        return Result_1
    }

    private suspend fun getResult2FromApi(): String {
        logThread("getResult2FromApi")
        delay(1000)
        return Result_2
    }

    private fun logThread(methodName: String) {
        println("debug: $methodName:${Thread.currentThread().name}")
    }
}
