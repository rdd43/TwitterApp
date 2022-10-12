package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.w3c.dom.Text


class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient

    lateinit var cLim: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        cLim = findViewById(R.id.CharLimit)

        etCompose = findViewById(R.id.etTweetCompose)
        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
                Log.i("ROB","Text Length: ${etCompose.length()}")
                cLim.setText((280-etCompose.length()).toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
            }
        })


        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener{
            val tweetContent = etCompose.text.toString()
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Empty Tweet Is Bad!", Toast.LENGTH_SHORT).show()
            }
            if(tweetContent.length > 280){
                Toast.makeText(this, "Tweet Is TOO LONG!", Toast.LENGTH_SHORT).show()
            } else{
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler(){
                    override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
                    ) {
                        Log.e("ROB", "Failed to published tweet", throwable)
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i("ROB", "Published tweet")
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                })
            }
        }
    }
}