package com.example.apptest.ui.inventory.folders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.ui.inventory.PillRecord
import com.example.apptest.ui.inventory.calculator.ShortenedPillRecord
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class FolderRecordsViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AuthRepository
) : ViewModel() {

    /**
     * Variables passed from the navGraph (between screens)
     */
    private val folderId: String = checkNotNull(savedStateHandle[FolderRecordsDestination.folderArg]) // Keep track of the current Id passed to the screen

    private val ownerId: String = checkNotNull(savedStateHandle[FolderRecordsDestination.userArg])

    /**
     * Track variables:
     */
    private val _userID = MutableStateFlow("")
    private val userID = _userID

    private val _folderName = MutableStateFlow("")
    val folderName = _folderName

    private val _folderDescription = MutableStateFlow("")
    val folderDescription = _folderDescription

    private val _folderQuantity = MutableStateFlow("")
    val folderQuantity = _folderQuantity

    private val _folderRecordIDs = MutableStateFlow("")
    private val folderRecordIDs = _folderRecordIDs

    private val _parsedListRecordIDs = MutableStateFlow(emptyList<String>())
    val parsedListRecordIDs = _parsedListRecordIDs

    var folderPillRecords by mutableStateOf(emptyList<PillRecord>())
        private set

    var shortenedPillRecords by mutableStateOf(emptyList<ShortenedPillRecord>())
        private set

    private val _folderRecordIDsAdded = MutableStateFlow("")
    private val folderRecordIDsAdded = _folderRecordIDsAdded

    private val _selectedPillIds = mutableSetOf<String>()
    val selectedPillIds: Set<String>
        get() = _selectedPillIds

    private val _shareWithEmail = MutableStateFlow("")
    val shareWithEmail = _shareWithEmail

    private val _shareWithEmailUUID = MutableStateFlow("")
    val shareWithEmailUUID = _shareWithEmailUUID

    private val _userIdShared = MutableStateFlow<String?>(null)
    val userIdShared: StateFlow<String?> = _userIdShared


    /**
     * view model functions:
     */

    fun onEmailChange(editEmail: String) { // track when textbox email is changed -> update variable
        _shareWithEmail.value = editEmail
    }

    // getters for folder information:
    fun getFolderName(): String {
        return folderName.value
    }

    fun getFolderDescription(): String {
        return folderDescription.value
    }

    fun getFolderRecordIds(): String {
        return folderRecordIDs.value
    }

    // Function to prompt system to retrieve UID
    private fun promptUserUID() = viewModelScope.launch {
        _userID.value = repository.getUID().toString()
    }

    // Function to be used in viewmodel for saving items with UID
    private fun getUserID(): String {
        promptUserUID()
        return userID.value
    }

    fun fetchFolderInfo() { // temp
        val database = Firebase.database.reference
        val userId = getUserID()
        val folderId = folderId

        println("What is the owner ID? {$ownerId}")

        // Refe path to the folder information
        val folderReference = database.child("folder sys").child(ownerId).child("folders").orderByChild("folderId").equalTo(folderId)

        // for async
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        folderReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //val pillRecords = mutableListOf<PillRecord>() // List to store the pill records

                // List of deferred jobs for fetching PillRecords
                val deferredJobs = mutableListOf<Deferred<PillRecord>>()

                for (ds in snapshot.children) {
                    // Read and parse ids from folderRecords in database
                    val folderRecords = ds.child("folderRecords").getValue(String::class.java)
                    folderRecords?.let {
                        val pillIds = it.split(",").map { id -> id.trim() }.filter { id -> id.isNotEmpty() } // split on ","
                        _parsedListRecordIDs.value = pillIds

                        // get pill records for each ID in loop
                        for (pillId in pillIds) {
                            val job = coroutineScope.async {
                                // Log.d("BEFORE FIX THIS", pillId)
                                println("BEFORE FIX THIS {$pillId}")
                                fetchPillGivenId(pillId)
                            }
                            deferredJobs.add(job)
                        }
                    }

                    // update state variables
                    _folderName.value = ds.child("folderName").getValue(String::class.java)!!
                    _folderQuantity.value = ds.child("folderQuantity").getValue(String::class.java)!!
                    _folderDescription.value = ds.child("folderDescription").getValue(String::class.java)!!
                   // _folderRecordIDs.value = ds.child("folderRecordIDs").getValue(String::class.java)!!
                }

                // After all fetch operations have completed
                coroutineScope.launch {
                    // Wait for all deferred jobs to complete and collect the results
                    val results = deferredJobs.awaitAll()

                    // Update the state variable with the list of pill records on the main thread
                    withContext(Dispatchers.Main) {
                        folderPillRecords = results
                        println("Updated folderPillRecords: $folderPillRecords")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // fetch the individual pill information given an id
    private suspend fun fetchPillGivenId(folderPillId: String): PillRecord { // temp
        println("Id given to Fetch $folderPillId")
        val database = Firebase.database.reference
        val userId = getUserID()

        // path to the user's pills find with matching pill id
        val userPillIdsRef = database.child("users").child(ownerId).orderByChild("pillId").equalTo(folderPillId)

        println("Pill Reference Path {$userPillIdsRef}")

        // suspend function for fetching
        return suspendCoroutine { continuation ->
            userPillIdsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val pillSnapshot = snapshot.children.firstOrNull()

                    println("Snapshot value: {$pillSnapshot}")

                    if (pillSnapshot != null) {
                        val count = pillSnapshot.child("count").value.toString()
                        val date = pillSnapshot.child("date").value.toString()
                        val description = pillSnapshot.child("description").value.toString()
                        val imageRef = pillSnapshot.child("imageRef").value.toString()
                        val tags = pillSnapshot.child("tags").value.toString()

                        // create pill Record object from information gathered
                        val pillRecord = PillRecord(
                            folderPillId,
                            count,
                            date,
                            description,
                            imageRef,
                            tags
                        )

                        println("PASS THIS BACK! {$pillRecord}")
                        continuation.resume(pillRecord)
                    } else {
                        continuation.resumeWithException(Exception("Pill not found"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(Exception(error.message))
                }
            })
        }
    }

    // gives a simplified list of the user's pills - excludes the tags and descriptions
    fun fetchShortenedPills() {

        val database = Firebase.database.reference
        val userId = getUserID()

        // pathway to the user's pills
        val userPillIdsRef = database.child("users").child(userId)

        userPillIdsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = mutableListOf<ShortenedPillRecord>() // Initialize list of Pill records to store all records of user

                snapshot.children.forEach { pillSnapshot ->
                    val pillId = pillSnapshot.child("pillId").value.toString()
                    val count = pillSnapshot.child("count").value.toString()
                    val date = pillSnapshot.child("date").value.toString()
                    val imageRef = pillSnapshot.child("imageRef").value.toString()

                    // Create pill record object
                    val pillRecord = ShortenedPillRecord(pillId, count, date, imageRef)
                    records.add(pillRecord)
                }
                shortenedPillRecords = records
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Function for controlling the toggle from UI check boxes for pill selection
    fun toggleItemSelection(pillId: String) {
        // Add any existing tags to the selected Pills
        for(id in parsedListRecordIDs.value) {
            if(id !in selectedPillIds) {
                _selectedPillIds.add(id)
            }
        }

        // Add and removed any pill when the check box is toggled
        if (_selectedPillIds.contains(pillId)) {
            _selectedPillIds.remove(pillId)
            println("Remove pill: {$pillId}")
        } else {
            _selectedPillIds.add(pillId)
            println("Selected pill: {$pillId}")
        }

        // Update the folderRecordIDsAdded
        _folderRecordIDsAdded.value = _selectedPillIds.joinToString(",")
    }

    // Function to show in the UI screen when there are pre-existing pills in the folder (reaccessed)
    fun isPillInFolder(pillId: String): Boolean {
        return _parsedListRecordIDs.value.contains(pillId)
    }

    // Update the database with the selected pill IDs
    fun updateFolderWithSelectedPillIds() {
        val userId = getUserID()
        val database = Firebase.database.reference
        val folderId = folderId

        // pathway
        val folderReference = database.child("folder sys").child(userId).child("folders").child(folderId)

        // Update folderRecords
        folderReference.child("folderRecords").setValue(folderRecordIDsAdded.value)

        // Update folder quantity when changed from parsing and getting length of ids added
        val tempNumRecordsList = folderRecordIDsAdded.value.split(",")
        folderReference.child("folderQuantity").setValue(tempNumRecordsList.size.toString())
    }


    private fun removePeriods(email: String): String {
        // replaced . with "" blank in string
        return email.replace(".", "")
    }

    fun fetchUserId(email: String) {
        val database = Firebase.database.reference
        val emailWithoutPeriod = removePeriods(email)

        // pathway
        val emailPathRef = database.child("user info").child(emailWithoutPeriod)

        emailPathRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // continue if snapshot exists (or if pathway is not null)
                if (snapshot.exists()) {
                    val userId = snapshot.child("userId").getValue(String::class.java)

                    _userIdShared.value = userId
                    println("Here!")
                } else {
                    println("snapshot error")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("error: ${error.message}")
            }
        })
    }

    fun shareFolderContentLocal() {
        val sharedFolderId = UUID.randomUUID().toString()
        val folderId = folderId
        val sharedWithUserId = userIdShared.value
        val database = Firebase.database.reference

        val userId = getUserID()

        val sharedFolder = SharedFolderInfo(
            folderId = folderId,
            ownerUserId = userId
        )

        database.child("folder sys").child(userId).child("shared folders").child(folderId).setValue(sharedFolder)
            .addOnSuccessListener {
                //
            }
            .addOnFailureListener{
                //
            }
    }


    fun shareFolderContentOtherUser() {
        val sharedFolderId = UUID.randomUUID().toString()
        val folderId = folderId
        val sharedWithUserId = userIdShared.value
        val database = Firebase.database.reference

        val userId = getUserID()

        val sharedFolder = SharedFolderInfo(
            folderId = folderId,
            ownerUserId = userId
        )

        if (sharedWithUserId != null) {
            database.child("folder sys").child(sharedWithUserId).child("shared folders").child(folderId).setValue(sharedFolder)
                .addOnSuccessListener {
                    //
                }
                .addOnFailureListener{
                    //
                }
        }
    }


}

// data class
data class SharedFolderInfo(
    val folderId: String,
    val ownerUserId: String
)