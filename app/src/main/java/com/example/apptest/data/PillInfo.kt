package com.example.apptest.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PillInfo(
    val pillId: String? = null,
    val count: String? = null,
    val date: String? = null,
    val description: String? = null,
    val tags: String? = null, //List<String?>?
    val imageRef: String? = null
)
// Null default values create a no-argument default constructor, which is needed
// for deserialization from a DataSnapshot.