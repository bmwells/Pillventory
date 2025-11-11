package com.example.apptest.ui.inventory


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.data.PillInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


// Background View model for the individual pill record screen
@HiltViewModel
class PillRecordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AuthRepository
) : ViewModel() {

    /**
     * Variables:
     */
    private val itemRecordId: String = checkNotNull(savedStateHandle[RecordDestination.itemArg]) // Keep track of the current Id passed to the screen

    val dataToPass = MutableLiveData<String>()
    private val _itemRecordId = itemRecordId

    private val _userID = MutableStateFlow("")
    private val userID = _userID

    private val _pillCount = MutableStateFlow("")
    val pillCount = _pillCount

    private val _date = MutableStateFlow("")
    val date = _date

    private val _description = MutableStateFlow("")
    val description = _description

    private val _imageRef = MutableStateFlow("")
    val imageRef = _imageRef

    private val _tags = MutableStateFlow("")
    val tags = _tags

    private val _tagsList = MutableStateFlow<List<String>>(emptyList())
    val tagsList = _tagsList

    // Track the original values of the record before they are altered on the Edit screen
    private val originalCount = MutableStateFlow("")
    private val originalDescription = MutableStateFlow("")

    /**
     * Functions
     */

    fun getPillId() : String {
        return itemRecordId
    }

    fun getPillCount() : String {
        return pillCount.value
    }

    fun getDate() : String {
        return date.value
    }

    fun getDescription() : String {
        return description.value
    }

    fun getImageRef() : String {
        return imageRef.value
    }

    private fun getTagString() : String {
        return tags.value
    }

    private fun parseTagString() { // Function will turn string of tags into a list to iterate through later
        val tagsList = getTagString().split(",").map { it.trim() }
        _tagsList.value = tagsList
    }

    fun getTagsList(): List<String> { // Function will return the list of tags
        parseTagString()
        return tagsList.value
    }

    fun passItemRecordIdToRepository() {
        repository.setItemRecordId(itemRecordId)
    }



    /**
     * Data editing
     */

    fun onPillCountChange(editCount: String) {
        _pillCount.value = editCount
    }

    fun onDescriptionChange(editDescription: String){
        _description.value = editDescription
    }


    fun getOriginalPillCount() :String {
        return originalCount.value
    }

    fun getOriginalDescription(): String {
        return originalDescription.value
    }

    /**
     * Database related
     */

    // Function to prompt system to retrieve UID
    private fun promptUserUID() = viewModelScope.launch {
        _userID.value = repository.getUID().toString()
    }

    // Function to be used in viewmodel for saving items with UID
    private fun getUserID() : String {
        promptUserUID()
        return userID.value
    }

    // Function to fetch the individual pill record
    fun fetchIndividualPillRecord() {

        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()
        val pillRecordId = itemRecordId

        // Define pathway
        val pillRecordReference = database.child("users").child(userId).orderByChild("pillId").equalTo(pillRecordId)
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for( ds in snapshot.children) {
                    _pillCount.value = ds.child("count").getValue(String::class.java)!!
                    _date.value = ds.child("date").getValue(String::class.java)!!
                    _description.value = ds.child("description").getValue(String::class.java)!!
                    _imageRef.value = ds.child("imageRef").getValue(String::class.java)!!
                    _tags.value = ds.child("tags").getValue(String::class.java)!!

                    originalCount.value = ds.child("count").getValue(String::class.java)!!
                    originalDescription.value = ds.child("description").getValue(String::class.java)!!
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        pillRecordReference.addListenerForSingleValueEvent(valueEventListener)
    }


    // Function that will update the pill information based on the current Pill ID and changed information
    fun updatePillRecord() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val pill = PillInfo(
            pillId = itemRecordId,
            count = pillCount.value,
            date = date.value,
            description = description.value,
            tags = tags.value,
            imageRef = imageRef.value
        )

        database.child("users").child(userId).child(itemRecordId).setValue(pill)
            .addOnSuccessListener {
                Log.d("update record", "Update Success")
            }
            .addOnFailureListener{
                Log.d("update record", "Update Failure")
            }
    }

    // Function to delete the current pill record based on the current pill ID
    fun deletePillRecord() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val pill = PillInfo(
            pillId = itemRecordId,
            count = pillCount.value,
            date = date.value,
            description = description.value,
            tags = tags.value,
            imageRef = imageRef.value
        )

        database.child("users").child(userId).child(itemRecordId).removeValue()
            .addOnSuccessListener {
                Log.d("remove record", "Removed record Success")
            }
            .addOnFailureListener{
                Log.d("remove record", "Removed record Failure")
            }
    }

    // Function to update the Record UI on screen
    fun updateRecordUI() {
        updatePillRecord()
        fetchIndividualPillRecord()
    }

    // Function to export the record to other apps like GMAIL using Android studio INTENTs
    fun ShareRecord(context: Context) {
        val storage = FirebaseStorage.getInstance()
        val httpsReference = storage.getReferenceFromUrl(
            imageRef.value
        )

        val file = File.createTempFile("image", ".jpg")
        httpsReference.getFile(file).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Grant read permissions to the file
                val photoURI = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                // Create an Intent for sharing the image and notes
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, photoURI)
                    putExtra(Intent.EXTRA_SUBJECT, "Pillventory Record - " + date.value)
                    putExtra(Intent.EXTRA_TEXT, "Pill Count: " + pillCount.value + "\r\n" + description.value)
                    // Add flag to grant read permissions to receiving app
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // Start the activity
                context.startActivity(intent)
            } else {
                // Handle error
            }
        }
    }
}


// Rebuild the pill record with full information
data class PillRecordUiState(
    val pillId: String,
    val count: String,
    val date: String,
    val description: String,
    val imageRef: String,
    val tags: String
)