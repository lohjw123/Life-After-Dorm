package com.example.lifeafterdorm.data

class Rating {
    var userId: String = ""
    var roomId: String = ""
    var ratingValue: String = ""
    var ratingTime: String = ""

    constructor(userId: String, roomId: String, ratingValue: String, ratingTime: String) {
        this.userId = userId
        this.roomId = roomId
        this.ratingValue = ratingValue
        this.ratingTime = ratingTime
    }

    constructor() {
    }
}