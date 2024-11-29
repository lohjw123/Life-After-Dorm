package com.example.lifeafterdorm.data

class CommunityPost {
    var postId :String=""
    var userId :String=""
    var roomId :String=""
    var postDateTime :String=""
    var postContent :String=""

    constructor(postId :String, userId :String, roomId :String, postDateTime :String, postContent :String) {
        this.postId = postId
        this.userId = userId
        this.roomId = roomId
        this.postDateTime = postDateTime
        this.postContent = postContent
    }

    constructor(){}

}