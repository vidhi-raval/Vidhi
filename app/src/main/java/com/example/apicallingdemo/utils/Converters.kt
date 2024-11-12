package com.example.apicallingdemo.utils

import androidx.room.TypeConverter
import com.example.apicallingdemo.model.Owner
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromOwner(owner: Owner): String {
        return Gson().toJson(owner) // Convert Owner object to a JSON string
    }

    @TypeConverter
    fun toOwner(ownerString: String): Owner {
        return Gson().fromJson(ownerString, Owner::class.java) // Convert JSON string back to Owner object
    }
}
