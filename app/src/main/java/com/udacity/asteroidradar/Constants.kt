package com.udacity.asteroidradar

object Constants {
    const val API_QUERY_DATE_FORMAT = "yyyy-MM-dd" //Changed after having searched for lint suggestion: yyyy is calendar, while YYYY is week year. This can cause bugs at the end of a year date
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = ""
}