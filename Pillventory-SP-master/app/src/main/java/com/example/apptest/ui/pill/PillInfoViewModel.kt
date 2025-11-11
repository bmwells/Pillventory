package com.example.apptest.ui.pill

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.apptest.ReadAndWrite
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow

class PillInfoViewModel : ViewModel() {
    private val _pillName = MutableStateFlow("")
    val pillName = _pillName

    private val _pillCount = MutableStateFlow(0)
    val pillCount = _pillCount

    private val _pillDescription = MutableStateFlow("")
    val pillDescription = _pillDescription

    fun onPillNameChange(newPillName: String) {
        _pillName.value = newPillName
    }

    fun readPill(){
        val database = FirebaseDatabase.getInstance().reference
        val pillReference = database.child("pills").orderByChild("name").equalTo(_pillName.value)
        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    _pillCount.value = ds.child("count").getValue(Int::class.java)!!
                    _pillDescription.value = ds.child("description").getValue(String::class.java)!!
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.message)
            }
        }
        pillReference.addListenerForSingleValueEvent(valueEventListener)
    }

}