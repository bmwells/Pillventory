package com.example.apptest.ui.inventory.calculator

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {


    /**
     * Variables
     */
    private val _userID = MutableStateFlow("")
    private val userID = _userID

    var shortenedPillRecords by mutableStateOf(emptyList<ShortenedPillRecord>())
        private set

    private val _runningTotal = mutableStateOf(0)
    val runningTotal: State<Int> = _runningTotal

    private val _multFactor = MutableStateFlow(0)
    val multFactor = _multFactor

    private val _divFactor = MutableStateFlow(1)
    val divFactor = _divFactor

    private val _product = MutableStateFlow(0)
    val product = _product

    private val _quotient = MutableStateFlow(0)
    val quotient = _quotient

    private val _subtractFrom = MutableStateFlow(0)
    val subtractFrom = _subtractFrom

    private val _runningTotalForSubtr = mutableStateOf(0)
    val runningTotalForSubtr: State<Int> = _runningTotalForSubtr

    private val _upID = MutableStateFlow("")
    val upID = _upID

    /**
     * Functions
     */
    fun toggleItemSelection(itemId: String) {
        val item = shortenedPillRecords.find { it.pillId == itemId }
        item?.checked = item?.checked?.not() ?: false

        _runningTotal.value = getSummedItems()
    }

    fun getSummedItems() : Int {
        var sum = 0

        for (item in shortenedPillRecords) {
            if( item.checked ) {
                sum += item.count.toInt()
            }
        }
        return sum
    }

    fun onMultFactorChange(editMult: String) {
        _multFactor.value = editMult.toInt()

    }

    fun getProduct(itemId: String) {
        val item = shortenedPillRecords.find { it.pillId == itemId }

        _product.value = item?.count?.toInt()?.times(multFactor.value) ?: 0
    }

    fun onSubtractFromChange(editSubtr: String) {
        _subtractFrom.value = editSubtr.toInt()
    }

    fun toggleItemSelectionForSubtr(itemId: String) {
        val item = shortenedPillRecords.find { it.pillId == itemId }
        item?.checked = item?.checked?.not() ?: false

        _runningTotalForSubtr.value = getSummedItems()
    }

    fun getDifference() : String {
        return (subtractFrom.value - runningTotalForSubtr.value).toString()
    }

    fun onDivChange(editDiv: String) {
        _divFactor.value = editDiv.toInt()
    }

    fun getQuotient(itemId:String) {
        val item = shortenedPillRecords.find { it.pillId == itemId }

        _quotient.value = item?.count?.toInt()?.div(divFactor.value) ?: 0
    }

    fun setID(itemId: String) {
        _upID.value = itemId
    }
    /**
     * Database related:
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

    fun fetchShortenedPills() {

        val database = FirebaseDatabase.getInstance().reference
        val userId = getUserID()

        val userPillIdsRef = database.child("users").child(userId)//.child("pills")

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
}

data class ShortenedPillRecord(
    val pillId: String,
    val count: String,
    val date: String,
    val imageRef: String,
    var checked: Boolean = false
)