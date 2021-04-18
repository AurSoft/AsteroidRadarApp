package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*

fun getTodayDateAsString() : String {
    val calendar = Calendar.getInstance()
    val today = calendar.time
    return SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault()).format(today)
}