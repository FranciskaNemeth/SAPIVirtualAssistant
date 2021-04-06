package com.example.sapivirtualassistant.model

class User(userType : Int, profilePicture: String?, userName: String, emailAddress: String, phoneNumber: String?, birthDay: String?) {

    var userType : Int = userType  // 0 -> oktato, 1 -> hallgato
        set(value) {
            if (value == 0 || value == 1) {
                field = value
            }
            else {
                throw Exception("Wrong userType!")
            }
        }

    var profilePicture : String? = profilePicture
        set(value) {
            if (value == null || value.isNotEmpty()) {
                field = value
            }
            else {
                throw Exception("Wrong profilePicture!")
            }
        }

    var userName : String = userName
        set(value) {
            if (value.isNotEmpty()) {
                field = value
            }
            else {
                throw Exception("Wrong userName!")
            }
        }

    var emailAddress : String = emailAddress
        set(value) {
            if (value.isNotEmpty() && isValidEmail(value)) {
                field = value
            }
            else {
                throw Exception("Wrong emailAddress!")
            }
        }

    var phoneNumber : String? = phoneNumber
        set(value) {
            if (value == null || value.isNotEmpty() && isValidPhoneNumber(value)) {
                field = value
            }
            else {
                throw Exception("Wrong phoneNumber!")
            }
        }

    var birthDay : String? = birthDay
        set(value) {
            if (value == null || value.isNotEmpty()) {
                field = value
            }
            else {
                throw Exception("Wrong birthDay!")
            }
        }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val regex = "^(\\+4|)?(07[0-8]{1}[0-9]{1}|02[0-9]{2}|03[0-9]{2}){1}?(\\s|\\.|\\-)?([0-9]{3}(\\s|\\.|\\-|)){2}\$".toRegex()
        return regex.matches(phone)
    }

    override fun toString(): String {
        return "$userType $userName $emailAddress $phoneNumber $birthDay"
    }

    fun userToHashMapOf() : HashMap<String, Any?> {
        return hashMapOf(
            "userType" to this.userType,
            "userName" to this.userName,
            "emailAddress" to this.emailAddress,
            "phoneNumber" to this.phoneNumber,
            "birthDay" to this.birthDay,
            "profilePicture" to this.profilePicture
        )
    }

}