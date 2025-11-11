package com.example.apptest.ui.settings.camerasettings

import android.util.Log
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
class CameraSettingsViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    // Variables

    private val _userID = MutableStateFlow("")
    private val userID = _userID
    private val _isGuest = MutableStateFlow(false)
    private val isGuest = _isGuest

    private val _redValue = MutableStateFlow(0)
    private val redValue = _redValue
    private val _greenValue = MutableStateFlow(0)
    private val greenValue = _greenValue
    private val _blueValue = MutableStateFlow(0)
    private val blueValue = _blueValue
    private val _check = MutableStateFlow(false)
    private val check = _check

    // Functions
    fun getRed() : Int {
        return redValue.value
    }
    fun getGreen() : Int {
        return greenValue.value
    }
    fun getBlue() : Int {
        return blueValue.value
    }
    fun getCheck() : Boolean {
        return check.value
    }




    /**
     * Database related
     */

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


    fun fetchUserPref() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val userPrefReference = database.child("camera_settings").child(userId).orderByChild("userID").equalTo(userId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val red = ds.child("red").getValue(Int::class.java)
                    val green = ds.child("green").getValue(Int::class.java)
                    val blue = ds.child("blue").getValue(Int::class.java)
                    val check = ds.child("check").getValue(Boolean::class.java)
                    val uid = ds.child("userID").getValue(String::class.java)

                    // Check for null values before assigning
                    if (red != null) {
                        _redValue.value = red
                    } else {
                        _redValue.value = 0 // Default value if red is null
                    }
                    if (green != null) {
                        _greenValue.value = green
                    } else {
                        _greenValue.value = 111 // Default value if green is null
                    }
                    if (blue != null) {
                        _blueValue.value = blue
                    } else {
                        _blueValue.value = 255 // Default value if blue is null
                    }
                    if (check != null) {
                        _check.value = check
                    } else {
                        _check.value = false // Default value if check is null
                    }
                    userID.value = uid ?: ""
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("fetchUserPref", "Error fetching preferences: ${error.message}")
            }
        }
        userPrefReference.addListenerForSingleValueEvent(valueEventListener)
    }






    // Function that will update the RGB values and check boolean.
    fun updateUserPref(redValue: Int, greenValue: Int, blueValue: Int, check: Boolean) {
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val pref = UserPreferences(
            redValue = redValue,
            greenValue = greenValue,
            blueValue = blueValue,
            check = check,
            userID = userID.value
        )
        database.child("camera_settings").child(userId).child("preference").setValue(pref)
            .addOnSuccessListener {
                Log.d("update pref", "Update Success")
            }
            .addOnFailureListener{
                Log.d("update pref", "Update Failure")
            }
    }




    // Function that will reset the RGB values and check boolean to default.
    fun resetUserPref() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val pref = UserPreferences(
            redValue = 0,
            greenValue = 111,
            blueValue = 255,
            check = false,
            userID = userID.value
        )
        database.child("camera_settings").child(userId).child("preference").setValue(pref)
            .addOnSuccessListener {
                Log.d("update pref", "Update Success")
            }
            .addOnFailureListener{
                Log.d("update pref", "Update Failure")
            }
    }




}

data class UserPreferences(
    val redValue: Int,
    val greenValue: Int,
    val blueValue: Int,
    val check: Boolean,
    val userID: String
)



