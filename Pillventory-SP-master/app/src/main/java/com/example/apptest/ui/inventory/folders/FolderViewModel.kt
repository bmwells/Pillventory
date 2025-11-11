package com.example.apptest.ui.inventory.folders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.ui.inventory.PillRecord
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


// data class for storing data relevant to the folder
data class FolderInfo(
    val folderId: String,
    val folderName : String,
    val folderDescription: String,
    val folderQuantity: String,
    val folderRecords: String
)

@HiltViewModel
class FolderViewModel
@Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {


    /**
     * Track variables
     */

    val _folderName = MutableStateFlow("") // Stores the entered folder name
    val folderName = _folderName

    val _folderDescription = MutableStateFlow("") // Stores the entered folder description
    val folderDescription = _folderDescription

    private val _userID = MutableStateFlow("")
    val userID = _userID

    var pillFolders by mutableStateOf(emptyList<FolderInfo>())
        private set

    var pillFoldersShared by mutableStateOf(emptyList<FolderInfo>())
        private set

    var sharedFolderInfo by mutableStateOf(emptyList<SharedFolderInfo>())
        private set

    /**
     * Functions for folder viewmodel
     */
    fun onNameChange(editName: String) {
        _folderName.value = editName
    }

    fun onDescriptionChange(editDescription: String) {
        _folderDescription.value = editDescription
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

    // Given a folder return who owns the original folder
    fun getOwnerIdByFolderId(folderId: String) : String? {
        val ownerId = sharedFolderInfo.firstOrNull() {it.folderId == folderId}

        return ownerId?.ownerUserId ?: getUserID()
    }

    // Allow user to create folder given name and optional description
    fun createFolder(name: String, description: String = "") {
        val folderId = UUID.randomUUID().toString()

        val database = Firebase.database.reference

        // create object from FolderInfo data class
        val folder = FolderInfo(
            folderId = folderId,
            folderName = name,
            folderDescription = description,
            folderQuantity = "0", // When folder is created it should be empty
            folderRecords = ""
        )

        val userId = getUserID()

        // defined pathway
        database.child("folder sys").child(userId).child("folders").child(folderId).setValue(folder)
            .addOnSuccessListener {
                println("Created folder success")
            }
            .addOnFailureListener{
                println("Created folder FAILURE")
            }
    }

    // retrieve folder information that was shared with the user - create list of sharedFolderInfo objects
    fun fetchSharedFolderInfo() {
        println("Call to fetch the shared folders")
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val userFolderIdsRef = database.child("folder sys").child(userId).child("shared folders")

        userFolderIdsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sharedFolders = mutableListOf<SharedFolderInfo>()
                println("Snapshot visible? {$snapshot}")
                snapshot.children.forEach { folderSnapshot ->
                    val folderId = folderSnapshot.child("folderId").value.toString()
                    val ownerUserId = folderSnapshot.child("ownerUserId").value.toString()


                    // Create shared pill record object
                    val sharedFolderInfo = SharedFolderInfo(
                        folderId,
                        ownerUserId,
                    )
                    sharedFolders.add(sharedFolderInfo)
                }

                sharedFolderInfo = sharedFolders
                println("Shared Folder Info (Should be Ids): {$sharedFolders}")
                iterateSharedFolderInfo()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Iterate through list of SharedFolderInfo objects
    fun iterateSharedFolderInfo() {
        println("Call to iterate through shared folder info (ids)")
        val sharedFolders = mutableListOf<FolderInfo>()
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val deferredJobs = mutableListOf<Deferred<FolderInfo>>()

        for(folder in sharedFolderInfo) {
            val job = coroutineScope.async {
                fetchSharedFolderGivenIds(folder.folderId, folder.ownerUserId)
            }

            deferredJobs.add(job)
        }

        coroutineScope.launch {
            val folderResults = deferredJobs.awaitAll()
            pillFoldersShared = folderResults
            println("convert shared info to folder info: {$folderResults}")
        }
//        pillFoldersShared = sharedFolders
//        println("convert shared info to folder info: {$sharedFolders}")
    }

    // find the individual folder contents given folder id and original owner id
    private suspend fun fetchSharedFolderGivenIds(sharedFolderId: String, ownerUserId: String): FolderInfo {
        println("From iterate called fetchSharedFolderGivenIds")
        val database = FirebaseDatabase.getInstance().reference

        val sharedFolderRef = database.child("folder sys").child(ownerUserId).child("folders").orderByChild("folderId").equalTo(sharedFolderId)

        return suspendCoroutine { continuation ->
            sharedFolderRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val sharedFolderSnapshot = snapshot.children.firstOrNull()

                    if(sharedFolderSnapshot != null) {
                        val folderId = sharedFolderSnapshot.child("folderId").value.toString()
                        val folderName = sharedFolderSnapshot.child("folderName").value.toString()
                        val folderDescription = sharedFolderSnapshot.child("folderDescription").value.toString()
                        val folderQuantity = sharedFolderSnapshot.child("folderQuantity").value.toString()
                        val folderRecords = sharedFolderSnapshot.child("folderRecords").value.toString()

                        // Create pill record object
                        val folderInfo = FolderInfo(
                            folderId,
                            folderName,
                            folderDescription,
                            folderQuantity,
                            folderRecords
                        )
                        println("Send this back!!! {$folderInfo}")
                        continuation.resume(folderInfo)
                    }

                }
                override fun onCancelled(error: DatabaseError) {
                    println("Database error from fetchSharedFolderGivenIds")
                }

            })
        }

    }


    // Fetch the user's folders
    fun fetchFolders() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val userFolderIdsRef = database.child("folder sys").child(userId).child("folders")

        userFolderIdsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val folders = mutableListOf<FolderInfo>() // Initialize list of Pill records to store all records of user

                snapshot.children.forEach { folderSnapshot ->
                    val folderId = folderSnapshot.child("folderId").value.toString()
                    val folderName = folderSnapshot.child("folderName").value.toString()
                    val folderDescription = folderSnapshot.child("folderDescription").value.toString()
                    val folderQuantity = folderSnapshot.child("folderQuantity").value.toString()
                    val folderRecords = folderSnapshot.child("folderRecords").value.toString()


                    // Create pill record object
                    val folderInfo = FolderInfo(
                        folderId,
                        folderName,
                        folderDescription,
                        folderQuantity,
                        folderRecords
                    )
                    folders.add(folderInfo)
                }

                pillFolders = folders
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database error onCancelled in fetchFolders")
            }
        })
    }
}
