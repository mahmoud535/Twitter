package com.example.twitter.firestore.models

class Ticket {
    var tweetID:String?=null
    var tweetText:String?=null
    var tweetImageURL:String?=null
    var tweetDate:String?=null
    var personName:String?=null
    var personImage:String?=null
    var personID:String?=null

    constructor(tweetID: String?, tweetText: String?, tweetImageURL: String?, personID: String?) {
        this.tweetID = tweetID
        this.tweetText = tweetText
        this.tweetImageURL = tweetImageURL
        this.personID = personID
    }



//    var tweetID:String?=null
//    var tweetText:String?=null
//    var tweetImageURL:String?=null
//    var tweetPersonUID:String?=null
//    constructor(tweetID:String,tweetText:String,tweetImageURL:String,tweetPersonUID:String){
//        this.tweetID=tweetID
//        this.tweetText=tweetText
//        this.tweetImageURL=tweetImageURL
//        this.tweetPersonUID=tweetPersonUID
//    }


}