package com.example.apptest.ui.landing.tagmanagement

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserTagsViewModel @Inject constructor(

    val repository: AuthRepository // Assuming AuthRepository helps manage user auth state
) : ViewModel() {

    private val database = FirebaseDatabase.getInstance().reference

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags = _tags.asStateFlow()


    var customTagsList by mutableStateOf(listOf<String>())
        private set

    private val _userID = MutableStateFlow("")
    private val userID = _userID

    private fun promptUserUID() = viewModelScope.launch {
        _userID.value = repository.getUID().toString()
    }

    // Function to be used in viewmodel for saving items with UID
    private fun getUserID(): String {
        promptUserUID()
        return userID.value
    }

    init {
        getUserID()
        fetchUserCustomTags()
    }


//    private fun fetchUserCustomTags() {
//        viewModelScope.launch {
//            val database = FirebaseDatabase.getInstance().reference
//            val userId = getUserID()
//            val usersRef = database.child("custom_tags").child("users").child(userId)
//
//            usersRef.get().addOnSuccessListener { dataSnapshot ->
//                val tempCustomTagsList = mutableListOf<String>()
//
//                dataSnapshot.children.forEach { userSnapshot ->
//                    val customTags = userSnapshot.child("user_tags")
//                    customTags.children.forEach { tagSnapshot ->
//                        val tag = tagSnapshot.value ?: ""
//                        tempCustomTagsList.add(tag.toString())
//                    }
//                }
//                customTagsList = tempCustomTagsList
//            }
//        }
//    }

    fun fetchUserCustomTags() {
        viewModelScope.launch {
            val database = FirebaseDatabase.getInstance().reference
            val usersRef = database.child("custom_tags").child("users").child(userID.value).child("user_tags")

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val tempCustomTagsList = mutableListOf<String>()

                        for (tagSnapshot in dataSnapshot.children) {
                            val tag = tagSnapshot.value as? String ?: ""
                            tempCustomTagsList.add(tag)
                        }

                        customTagsList = tempCustomTagsList
                    } else {
                        Log.d("Firebase", "No custom tags found for user: $userID")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Database error: ${databaseError.message}")
                }
            })
        }
    }



    fun addUserCustomTagUsingPillId(userId: String, newTag: String) {
        viewModelScope.launch {
            val database = FirebaseDatabase.getInstance().reference
            val userRef = database.child("custom_tags").child("users").child(userId).child("user_tags")

            userRef.get().addOnSuccessListener {
                val pillTagsRef = database.child("custom_tags").child("users").child(userId).child("user_tags")

                val newTagRef = pillTagsRef.push()
                newTagRef.setValue(newTag).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        customTagsList = customTagsList.toMutableList().apply {
                            add(newTag)
                        }
                    } else {
                        Log.e("Firebase", "Failed to add new tag: ${task.exception?.message}")
                    }
                }
            }.addOnFailureListener {
                Log.e("Firebase", "Failed to fetch user data: ${it.message}")
            }
        }
    }

    fun removeUserCustomTagUsingPillId(userId: String, tagName: String) {
        viewModelScope.launch {
            val database = FirebaseDatabase.getInstance().reference
            val userTagsRef = database.child("custom_tags").child("users").child(userId).child("user_tags")

            userTagsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var tagFound = false
                        for (tagSnapshot in dataSnapshot.children) {
                            if (tagSnapshot.value == tagName) {
                                tagFound = true
                                tagSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("Firebase", "Successfully removed tag: $tagName")
                                        customTagsList = customTagsList.toMutableList().apply {
                                            removeIf { it == tagName }
                                        }
                                    } else {
                                        Log.e("Firebase", "Failed to remove tag: ${task.exception?.message}")
                                    }
                                }
                                break
                            }
                        }
                        if (!tagFound) {
                            Log.d("Firebase", "Tag $tagName not found")
                        }
                    } else {
                        Log.d("Firebase", "No custom tags found for user: $userId")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Database error: ${databaseError.message}")
                }
            })
        }
    }





//    fun removeUserCustomTagUsingPillId(userId: String, tagKey: String) {
//        viewModelScope.launch {
//            val database = FirebaseDatabase.getInstance().reference
//            val userRef = database.child("users").child(userId)
//
//            userRef.get().addOnSuccessListener { dataSnapshot ->
////                val pillID = dataSnapshot.child("pillID").value.toString()
//                println("this dumb thing with tagKey string itself {$tagKey}")
//                val tagKeyRef = database.child("users").child(userId).child("custom_tags").child(tagKey).getKey()
//                println("this dumb bullshit {$tagKeyRef}")
//                val tagRef =
//                    tagKeyRef?.let {
//                        database.child("users").child(userId).child("custom_tags")
//                    }
//
////                val key = tagKey.getKey()
//                println("this dumb bullshit {$tagRef}")
//                if (tagRef != null) {
//                    tagRef.removeValue().addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            customTagsList = customTagsList.toMutableList().apply {
//                                removeIf { it == tagKey } // Remove by key
//                            }
//                        } else {
//                            Log.e("Firebase", "Failed to remove tag: ${task.exception?.message}")
//                        }
//                    }
//                }
//            }.addOnFailureListener {
//                Log.e("Firebase", "Failed to fetch user data: ${it.message}")
//            }
//        }
//    }
}
