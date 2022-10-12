package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient

    lateinit var rvTweets: RecyclerView

    lateinit var adapter: TweetsAdapter


    lateinit var swipeContainer: SwipeRefreshLayout

    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getRestClient(this)

        swipeContainer = findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i("ROB", "REFRESHING TWEETS!!!")
            populateTimeline()
        }
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)

        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter

        populateTimeline()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.compose){
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, 10)

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode == 10){
            val tweet = data?.getParcelableExtra("tweet") as Tweet

            tweets.add(0,tweet)
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun populateTimeline(){
        client.getHomeTimeline(object : JsonHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i("ROB", "populateTimeline SUC $json")

                val jsonArray = json.jsonArray

                try {
                    adapter.clear()
                    val listOfNewTweetsRetried = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(listOfNewTweetsRetried)
                    adapter.notifyDataSetChanged()
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException){
                    Log.e("ROB", "JSON ERROR SOMETHING WENT WRONG $e")
                }
            }
            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                Log.i("ROB", "populateTimeline FAIL")
            }

        })
    }
}