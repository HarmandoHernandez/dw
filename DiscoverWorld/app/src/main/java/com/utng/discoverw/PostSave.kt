package com.utng.discoverw

import java.io.Serializable

class PostSave(
        val title: String,
        val image: String,
        val description: String
) : Serializable