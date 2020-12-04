package com.utng.discoverw

import java.io.Serializable

class Post(
        val title: String,
        var image: String,
        val description: String,
        val lat: String,
        val long: String
) : Serializable