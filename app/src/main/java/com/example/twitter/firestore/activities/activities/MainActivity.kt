package com.example.twitter.firestore.activities.activities
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mypet.utils.Constants
import com.example.mypet.utils.GlideLoader

import com.example.twitter.R
import com.example.twitter.firestore.Operations
import com.example.twitter.firestore.models.PostInfo
import com.example.twitter.firestore.SaveSettings
import com.example.twitter.firestore.models.Ticket
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.add_ticket.*
import kotlinx.android.synthetic.main.add_ticket.view.*
import kotlinx.android.synthetic.main.tweets_ticket.*
import kotlinx.android.synthetic.main.tweets_ticket.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity() {

    private var database=FirebaseDatabase.getInstance()
    private var myRef=database.reference

    var ListTweets=ArrayList<Ticket>()
    var adapter:MyTweetAdpater?=null
    var myemail:String?=null
    var UserUID:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var b: Bundle? =intent.extras
        if (b != null) {
            myemail=b.getString("email")
            UserUID=b.getString("uid")
        }
        //الخاص بلون الواجهه اللي فوق في الDashboardActivity
//        supportActionBar!!.setBackgroundDrawable(
//            ContextCompat.getDrawable(
//                this@MainActivity,
//                R.drawable.app_gradient_color_background_1
//            )
//        )
        //////////////////////////////////////////
        val saveSettings=SaveSettings(this)
        saveSettings.loadSettings()


        SearchInDatabase("%",0)

        ListTweets.add(Ticket("0","him","url","add"))
//        ListTweets.add(Ticket("0","him","url","mahmoud"))
//        ListTweets.add(Ticket("0","him","url","Aly"))
//        ListTweets.add(Ticket("0","him","url","yousef"))
//        ListTweets.add(Ticket("0","him","url","waleed"))
//        ListTweets.add(Ticket("0","him","url","atamer"))


        //set adapterc
        adapter=MyTweetAdpater(this,ListTweets)
        lvTweets.adapter=adapter
//        LoadPost()
    }
    //الخاص بوضع الايقونه
    //لكي نظهر الزر الموجود في ال menu يجب ان نستدعي 2 functions (onCreate Options Menu and onOptionsItemSelected(
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.dashboard_menu, menu)
        //الخاص بايقونه ال سيرش للبحث
        val sv: SearchView =menu.findItem(R.id.app_bar_search).actionView as SearchView

        val sm=getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                if (query != null) {
                    SearchInDatabase(query,0)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_settings->{
                //Got to add paage
                var intent= Intent(this,SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.homePage->{
                // TODO: go to home
                // CALL http
                SearchInDatabase("%",0)

            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun  SearchInDatabase(SearchText:String, startFrom:Int){
        val SearchText=URLEncoder.encode(SearchText,"utf-8")
        DownloadURL=URLEncoder.encode(DownloadURL,"utf-8")
        val url="http://10.0.2.2/~hussienalrubaye/TwitterAndroidServer/TweetList.php?op=3&query=" + SearchText + "&StartFrom=" + startFrom
        MyAsyncTask().execute(url)
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //dapter loader
    inner class MyTweetAdpater:BaseAdapter{
        var listNotesAdpater=ArrayList<Ticket>()
        var context:Context?=null
        constructor(context:Context, listNotesAdpater: java.util.ArrayList<Ticket>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var mytweet=listNotesAdpater[position]
            if (mytweet.personID.equals("add")){//tweetDate
                var myView=layoutInflater.inflate(R.layout.add_ticket,null)
                myView.iv_attach.setOnClickListener {
                    loadImage()
                }
                myView.iv_post.setOnClickListener {

                    myRef.child("posts").push().setValue(
                            PostInfo(UserUID,
                                    myView.etPost.text.toString(),DownloadURL))

                    //upload server

                    ListTweets.add(0, Ticket("0", "him", "url", "loading"))
                    adapter!!.notifyDataSetChanged()

                    //call http
                    val postText = URLEncoder.encode(myView.etPost.text.toString(), "utf-8")
                    DownloadURL = URLEncoder.encode(DownloadURL, "utf-8")
                    val url =
                            "http://10.0.2.2/~hussienalrubaye/TwitterAndroidServer/TweetAdd.php?user_id=" + SaveSettings.userID + "&tweet_text=" + postText + "&tweet_picture=" + DownloadURL
                    MyAsyncTask().execute(url)

                    myView.etPost.setText("")
                }
                return myView
            }
//            else if (mytweet.tweetDate.equals("loading")){
//                var myView=layoutInflater.inflate(R.layout.loading_ticket,null)
//                return  myView
//            }
            else{
                var myView=layoutInflater.inflate(R.layout.tweets_ticket,null)
                myView.txt_tweet.text=mytweet.tweetText
                myView.txtUserName.text=mytweet.tweetID
                //myView.tweet_picture.setImageURI(mytweet.tweetImageURL)
                Picasso.get().load(mytweet.tweetImageURL).into(myView.tweet_picture)
//
//                Picasso.get().load(mytweet.personImage).into(myView.picture_path)
//                myView.txtUserName.text = mytweet.personName
//                myView.txtUserName.setOnClickListener {
//                    //
//                    val url="http://10.0.2.2/~hussienalrubaye/TwitterAndroidServer/TweetList.php?op=2&user_id=" + mytweet.personID +"&StartFrom=0"
//                    MyAsyncTask().execute(url)
//                }
                return myView
            }
        }

        override fun getItem(position: Int): Any {
            return listNotesAdpater[position]
        }

        override fun getItemId(position: Int): Long {
            return  position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdpater.size
        }

    }

    //Load image
    //طلب رخصه لتحميل الصوره
    val PICK_IMAGE_CODE=123
    fun loadImage(){
        var intent=Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==PICK_IMAGE_CODE&& data!=null && resultCode == RESULT_OK){

            val selectedImage=data.data
            val filePathColum= arrayOf(MediaStore.Images.Media.DATA)
            val cursor=contentResolver.query(selectedImage!!,filePathColum,null,null,null)
            cursor!!.moveToFirst()
            val coulomIndex=cursor!!.getColumnIndex(filePathColum[0])
            val picturePath=cursor!!.getString(coulomIndex)
            cursor!!.close()
            UploadImage(BitmapFactory.decodeFile(picturePath))
        }
    }
    var DownloadURL:String?="noImage"
    fun UploadImage(bitmap: Bitmap){
        ListTweets.add(0, Ticket("0","him","url","loading"))
        adapter!!.notifyDataSetChanged()

        val storage= FirebaseStorage.getInstance()
        val storgaRef=storage.getReferenceFromUrl("gs://twitterwebservice-b75b6.appspot.com")
        val df= SimpleDateFormat("ddMMyyHHmmss")
        val dataobj= Date()
        val imagePath= SaveSettings.userID + "."+ df.format(dataobj)+ ".jpg"
        //val imagePath=SplitString(myemail!!)+ "."+ df.format(dataobj)+ ".jpg"
        val ImageRef=storgaRef.child("imagePost/"+imagePath )
        val baos= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data= baos.toByteArray()
        val uploadTask=ImageRef.putBytes(data)
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"fail to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->

            DownloadURL= taskSnapshot.storage.downloadUrl.toString()
            ListTweets.removeAt(0)
            adapter!!.notifyDataSetChanged()

        }

    }
    fun SplitString(email:String):String{
        val split=email.split("@")
        return split[0]
    }

    //    fun LoadPost(){
//        myRef.child("posts")
//                .addValueEventListener(object :ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        try {
//                            ListTweets.clear()
//                            ListTweets.add(Ticket("0","him","url","add"))
//
//                            var td=snapshot.value as HashMap<String,Any>
//                            for (key in td.keys){
//                                var post=td[key] as HashMap<String,Any>
//
//                                ListTweets.add(Ticket(key,
//                                        post["text"]as String,
//                                        post["postImage"]as String,
//                                        post["userUID"]as String))
//
//                            }
//
//                            adapter!!.notifyDataSetChanged()
//                        }catch (ex:Exception){}
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//                })
//    }
    //call HTTP
    inner class MyAsyncTask: AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            //Before task started
        }
        override fun doInBackground(vararg p0: String?): String {
            try {

                val url= URL(p0[0])

                val urlConnect=url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout=7000

                val op= Operations()

                var inString= op.ConvertStreamToString(urlConnect.inputStream)
                //Cannot access to ui
                publishProgress(inString)
            }catch (ex:Exception){}


            return " "

        }

        override fun onProgressUpdate(vararg values: String?) {
            try{
                var json= JSONObject(values[0])
                Toast.makeText(applicationContext,json.getString("msg"),Toast.LENGTH_LONG).show()


                if (json.getString("msg")== "tweet is added"){
                    DownloadURL="noImage"
                    ListTweets.removeAt(0)
                    adapter!!.notifyDataSetChanged()
                }else if ( json.getString("msg")=="has tweet"){
                    ListTweets.clear()
                    ListTweets.add(Ticket("0","him","url","add"))


                    // get tweets
                    val tweets = JSONArray(json.getString("info"))
                    for (i in 0..tweets.length()-1){
                        val singleTweet= tweets.getJSONObject(i)
                        ListTweets.add(Ticket(singleTweet.getString("tweet_id"),singleTweet.getString("tweet_text"),
                                singleTweet.getString("tweet_picture"),singleTweet.getString("tweet_date")
                        ))

                    }
                }else if ( json.getString("msg")=="no tweets"){
                    ListTweets.clear()
                    ListTweets.add(Ticket("0","him","url","add"))

                }



                adapter!!.notifyDataSetChanged()

            }catch (ex:Exception){}
        }

        override fun onPostExecute(result: String?) {

            //after task done
        }


    }
    override fun onBackPressed() {///للرجوع الي الخلف
        doubleBackToExit()
    }
}