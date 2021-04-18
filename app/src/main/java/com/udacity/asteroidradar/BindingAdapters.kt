package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidAdapter
import com.udacity.asteroidradar.api.NasaApiStatus

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidAdapter
    adapter.submitList(data)
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
        imageView.contentDescription = imageView.context.getString(R.string.potentially_hazardous_asteroid)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
        imageView.contentDescription = imageView.context.getString(R.string.not_hazardous_asteroid)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription = imageView.context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = imageView.context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("picOfTheDay")
fun bindImageViewToPicOfTheDay(imageView: ImageView, picOfTheDay: PictureOfDay?) {
    Picasso.with(imageView.context)
            .load(picOfTheDay?.url)
            .fit()
            .centerInside()
            .placeholder(R.drawable.ic_astronaut)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    imageView.contentDescription = picOfTheDay?.title ?: imageView.context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
}

@BindingAdapter("picOfTheDayTitle")
fun bindTextViewToPicOfTheDayTitle(textView: TextView, picOfTheDay: PictureOfDay?) {
    if (null != picOfTheDay && picOfTheDay.mediaType == "image") {
        textView.text = picOfTheDay.title
    } else {
        textView.text = textView.context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
    }
}

@BindingAdapter("isContentLoading")
fun bindProgressBarToStatus(swipeRefreshLayout: SwipeRefreshLayout, status: NasaApiStatus) {
    when(status) {
        NasaApiStatus.LOADING -> swipeRefreshLayout.isRefreshing = true
        else -> swipeRefreshLayout.isRefreshing = false
    }
}