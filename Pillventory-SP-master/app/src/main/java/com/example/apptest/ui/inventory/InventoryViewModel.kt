package com.example.apptest.ui.inventory

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

// Data class for storing the data relevant to a specific record
data class PillRecord(
    val pillId: String,
    val count: String,
    val date: String,
    val description: String,
    val imageRef: String,
    val tags: String
)

// Background View model for the inventory screen
@HiltViewModel
class InventoryViewModel
@Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {


    /**
     * Variables
     */

    var tagsList by mutableStateOf(listOf<String>())
        private set

    var selectedTags = mutableStateOf(setOf<String>())

    fun toggleTagSelection(tag: String) {
        selectedTags.value = selectedTags.value.toMutableSet().apply {
            if (contains(tag)) remove(tag) else add(tag)
        }
    }

    // Keep track of Inventory layout view toggle to change layout to either Card lists or Image Grid
    private val _isCardLayout = MutableStateFlow(true) // Default value to Card view
    val isCardLayout = _isCardLayout

    private val _isGridLayout = MutableStateFlow(false)
    val isGridLayout = _isGridLayout

    private val _isGuest = MutableStateFlow(false)
    val isGuest = _isGuest

    private val _userID = MutableStateFlow("")
    private val userID = _userID

    var pillRecords by mutableStateOf(emptyList<PillRecord>())
        private set

    /**
     * Related functions
     */

    // Update the state of the inventory layout
    fun changeLayoutToCard(){ // Change to cards
        isCardLayout.value = true
        isGridLayout.value = false
    }

    fun changeLayoutToGrid(){ // Change to grid
        isCardLayout.value = false
        isGridLayout.value = true
    }

    fun parseTagString(tagString: String): List<String> {
        val tagsList = tagString.split(",").map { it.trim() }
        return tagsList
    }

    // Sort pillRecords by date in ascending order
    @RequiresApi(Build.VERSION_CODES.O)
    fun sortRecordsByDateAscending() {
        //pillRecords = pillRecords.sortedBy { it.date }
        pillRecords = pillRecords.sortedWith(compareBy { record ->
            val (month, day, year) = record.date.split("/")
            LocalDate.of(year.toInt(), month.toInt(), day.toInt())
        })
    }

    // Sort pillRecords by date in descending order
    @RequiresApi(Build.VERSION_CODES.O)
    fun sortRecordsByDateDescending(){
        //pillRecords = pillRecords.sortedByDescending { it.date }
        pillRecords = pillRecords.sortedWith(compareByDescending { record ->
            val (month, day, year) = record.date.split("/")
            LocalDate.of(year.toInt(), month.toInt(), day.toInt())
        })
    }
    // Sort by count ascending
    fun sortRecordsByCountAscending(){
        pillRecords = pillRecords.sortedBy{ it.count.toInt() }
    }
    // Sort by count descending
    fun sortRecordsByCountDescending(){
        pillRecords = pillRecords.sortedByDescending{ it.count.toInt() }
    }
    // Filter by Tag
    fun sortRecordsByTag(tag: String) {
       pillRecords = pillRecords.filter { record ->
            record.tags.contains(tag)
        }
    }

    // Function to determine if user is guest with coroutine
    private fun promptIsUserGuest() = viewModelScope.launch {
        _isGuest.value = repository.isUserAnonymous()
    }

    // Function to be used in screen composable for isGuest variable
    fun isUserGuest() : Boolean {
        promptIsUserGuest()
        return isGuest.value
    }

    // Function to prompt system to retrieve UID
    private fun promptUserUID() = viewModelScope.launch {
        _userID.value = repository.getUID().toString()
    }

    // Function to be used in viewmodel for saving items with UID
    private fun getUserID() : String {
        promptUserUID()
        return userID.value
    }

    fun fetchTags() {
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

    // Function to fetch the user's saved images by their id's
    fun fetchPillIds() {

        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val userPillIdsRef = database.child("users").child(userId)//.child("pills")

        userPillIdsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = mutableListOf<PillRecord>() // Initialize list of Pill records to store all records of user

                snapshot.children.forEach { pillSnapshot ->
                    val pillId = pillSnapshot.child("pillId").value.toString()
                    val count = pillSnapshot.child("count").value.toString()
                    val date = pillSnapshot.child("date").value.toString()
                    val description = pillSnapshot.child("description").value.toString()
                    val imageRef = pillSnapshot.child("imageRef").value.toString()
                    val tags = pillSnapshot.child("tags").value.toString()

                    // Create pill record object
                    val pillRecord = PillRecord(pillId, count, date, description, imageRef, tags)
                    records.add(pillRecord)
                }
                //sortRecordsByDateAscending(records)
                //sortRecordsByCountAscending(records)
                records.forEachIndexed { index, pillRecord ->
                    Log.d("JoeJoe", "Entry $index: $pillRecord")
                }
                pillRecords = records


            }


            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })


    }
}