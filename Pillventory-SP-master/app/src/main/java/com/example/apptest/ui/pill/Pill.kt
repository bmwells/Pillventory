package com.example.apptest.ui.pill

import com.google.firebase.database.IgnoreExtraProperties
@IgnoreExtraProperties
data class Pill(val name: String? = null, val count: Int? = null, val description: String? = null) {
// Null default values create a no-argument default constructor, which is needed
// for deserialization from a DataSnapshot.
}