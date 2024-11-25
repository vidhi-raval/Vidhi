package com.example.apicallingdemo.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefHelper {

    private const val PREF_NAME = "MyAppPreferences"
    private lateinit var preferences: SharedPreferences
    var dataRangeKey = "selected_data_range_pos"

    // Initialize the SharedPreferences instance
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save a string value
    fun saveString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    // Retrieve a string value
    fun getString(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    // Save a boolean value
    fun saveBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    // Retrieve a boolean value
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    // Save an integer value
    fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    // Retrieve an integer value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    // Remove a specific key
    fun removeKey(key: String) {
        preferences.edit().remove(key).apply()
    }

    // Clear all data
    fun clearAll() {
        preferences.edit().clear().apply()
    }
}
