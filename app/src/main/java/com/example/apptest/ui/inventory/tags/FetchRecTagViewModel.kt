package com.example.apptest.ui.inventory.tags

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchRecTagViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    var tagsList by mutableStateOf(listOf<String>())
        private set

    var customTagsList by mutableStateOf(listOf<String>())
        private set

    private val _userID = MutableStateFlow("")
    private val userID = _userID

    private val itemRecordId = repository.getItemRecordId()

    // Inside FetchTagsViewModel
    var selectedTag = mutableStateOf<String?>(null)


    fun toggleTagSelection(tag: String) {
        selectedTag.value = if (selectedTag.value == tag) null else tag
    }

    private fun promptUserUID() = viewModelScope.launch {
        _userID.value = repository.getUID().toString()
    }

    // Function to be used in viewmodel for saving items with UID
    private fun getUserID() : String {
        promptUserUID()
        return userID.value
    }


    fun addTag(newTag: String) {
        viewModelScope.launch {
            val userId = getUserID()
            val pillId = itemRecordId
            val database = FirebaseDatabase.getInstance().reference
            val userRef = pillId?.let { database.child("users").child(userId).child(it) }

            Log.d("pillId", "$pillId")

            userRef?.child("tags")?.setValue(newTag)?.addOnSuccessListener {
                Log.d("success", "yay")
            }
        }
    }


    private fun fetchTags() {
        viewModelScope.launch {
            val database = FirebaseDatabase.getInstance().reference
            val tagsRef = database.child("rec_tags")

            tagsRef.get().addOnSuccessListener { dataSnapshot ->
                val tempList = mutableListOf<String>()

                dataSnapshot.children.forEach { snapshot ->
                    val tag = snapshot.value ?: ""
                    tempList.add(tag.toString())

                }
                tagsList = tempList
            }
        }
    }

//    private fun fetchUserCustomTags() {
//        viewModelScope.launch {
//            val database = FirebaseDatabase.getInstance().reference
//            val userId = getUserID()
//            val usersRef = database.child("custom_tags")//.child("users").child(userId)
//
//            usersRef.get().addOnSuccessListener { dataSnapshot ->
//                val tempCustomTagsList = mutableListOf<String>()
//
//                dataSnapshot.children.forEach { userSnapshot ->
//                    val customTags = userSnapshot.child("users").child(userId).child("user_tags")
//                    customTags.children.forEach { tagSnapshot ->
//
//                        val tag = tagSnapshot.value ?: ""
//                        tempCustomTagsList.add(tag.toString())
//                    }
//                    customTagsList = tempCustomTagsList
//                }
//            }
//        }
//    }

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

    private fun fetchUserCustomTags() {
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




    init {
        getUserID()
        fetchTags()
        fetchUserCustomTags()
    }
}
