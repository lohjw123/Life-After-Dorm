package com.example.lifeafterdorm.data

class RentalRoom {
    var roomId :String=""
    var userId :String=""
    var location : String=""
    var roomType1 :String=""
//    var roomType2 :String=""
    var contactNum :String=""
    var monthlyRental :String=""
    var maxPerson :String=""
    var address :String=""
    var title :String=""
    var status :String=""
    var race :String=""
    var leaseTerm :String=""
    var checkedValues :String=""
    var editTextTextMultiLineDesc :String=""
    var imageUri : String=""
    var roomRating : String=""
    var numOfRating : String=""
    var postDateTime : String=""


    constructor(roomId :String, userId :String, location : String, roomType1 :String, contactNum :String, monthlyRental :String, maxPerson :String, address :String, title :String, status :String, race :String, leaseTerm :String, checkedValues :String, editTextTextMultiLineDesc :String, imageUri : String, roomRating :String, numOfRating :String, postDateTime :String) {
        this.roomId = roomId
        this.userId = userId
        this.location = location
        this.roomType1 = roomType1
//        this.roomType2 = roomType2
        this.contactNum = contactNum
        this.monthlyRental = monthlyRental
        this.maxPerson = maxPerson
        this.address = address
        this.title = title
        this.status = status
        this.race = race
        this.leaseTerm = leaseTerm
        this.checkedValues = checkedValues
        this.editTextTextMultiLineDesc = editTextTextMultiLineDesc
        this.imageUri = imageUri
        this.roomRating = roomRating
        this.numOfRating = numOfRating
        this.postDateTime = postDateTime
    }

    constructor() {

    }
}

//data class RentalRoom(var roomId :String="", var userId :String="", var location : String="", var roomType1 :String="", var roomType2 :String="", var contactNum :String, var monthlyRental :String="", var maxPerson :String, var address :String="", var title :String="", var status :String="", var race :String="", var leaseTerm :String="", var checkedValues :String="", var editTextTextMultiLineDesc :String="", var imageUri : String="")
