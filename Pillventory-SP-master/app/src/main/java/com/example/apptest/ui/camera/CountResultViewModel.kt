package com.example.pillsv.camera

import android.graphics.Bitmap
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.ui.camera.CameraDestination
import com.example.apptest.ui.camera.CountResultDestination
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import javax.inject.Inject


// Background view model for count results screen and modal
@HiltViewModel
class CountResultViewModel@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AuthRepository
) : ViewModel() {

    /**
     * Variables store state
     */
    private val _description = MutableStateFlow("")
    val description = _description

    private val _isGuest = MutableStateFlow(false)
    val isGuest = _isGuest

    private val _userID = MutableStateFlow("")
    private val userID = _userID

    private val _isOverMaxLength = MutableStateFlow(false)
    val isOverMaxLength = _isOverMaxLength

    private val _imageFromProc = MutableStateFlow<Bitmap?>(null)
    val imageFromProc = _imageFromProc

    private val _redAmt = MutableStateFlow(255); // setting default to red
    val redAmt = _redAmt

    private val _greenAmt = MutableStateFlow(0);
    val greenAmt = _greenAmt

    private val _blueAmt = MutableStateFlow(0);
    val blueAmt = _blueAmt

    private val _isNumberCounting = MutableStateFlow(false)
    val isNumberCounting = _isNumberCounting

    private val _ignoreId = MutableStateFlow("")
    val ignoreId = _ignoreId

   // private val count: Int = checkNotNull(savedStateHandle[CountResultDestination.countArg])
   // private var _count = MutableStateFlow("")
    //private var count = _count


    fun setImageBitmap(bitmap: Bitmap) {
        _imageFromProc.value = bitmap
    }

    fun getImageBitmap(): Bitmap? {
        //Log.d("CountResultViewModel", "Setting bitmap")
        return imageFromProc.value
    }
    /**
     * Related functions
     */
    fun onDescriptionChange(editDesc: String) {
        _description.value = editDesc
    }

    // Function to activate warning that description too long
    fun onDescOverLength(){
        isOverMaxLength.value = true
    }

    // Function to deactivate warning that description too long
    fun onDescUnderLength(){
        isOverMaxLength.value = false
    }

    // sets count
    //joe
//   fun setCount(count: Int) {
//        _count.value = count.toString()
//    }
    // gets the count found in the image
    fun getCount(): String {
        // Placeholder function for now
        return "0"
        //
    }

    // Gets the current date
    fun getDate(): String {
        val calendar = Calendar.getInstance().time
        return DateFormat.getDateInstance(DateFormat.SHORT).format(calendar)
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
    fun getUserID() : String {
        promptUserUID()
        return userID.value
    }

    fun retrieveCameraSettings() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID() // Make sure this returns the correct user ID
        println("AAAAAAAAAAAAA $userId")

        val cameraSettingsRef = database
            .child("camera_settings")
            .child(userId)
            //.child("preference")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    println("Child key: ${ds.key}, Value: ${ds.value}")
                    _blueAmt.value = ds.child("blueValue").getValue(Int::class.java) ?: 0

                    _isNumberCounting.value = ds.child("check").getValue(Boolean::class.java) ?: false
                    _greenAmt.value = ds.child("greenValue").getValue(Int::class.java) ?: 0
                    _redAmt.value = ds.child("redValue").getValue(Int::class.java) ?: 0
                    _ignoreId.value = ds.child("userID").getValue(String::class.java) ?: ""

                    println("HELLO BLUE ${_blueAmt.value}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        cameraSettingsRef.addListenerForSingleValueEvent(valueEventListener)
    }

}