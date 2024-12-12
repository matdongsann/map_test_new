package com.example

import com.naver.maps.geometry.LatLng

data class Course(
    val name: String,
    val points: List<LatLng>,
    val distance: Int,
    val time: Int
)
