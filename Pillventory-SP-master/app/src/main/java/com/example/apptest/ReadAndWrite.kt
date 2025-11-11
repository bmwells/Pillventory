package com.example.apptest

import android.content.ContentValues.TAG
import android.util.Log
import com.example.apptest.ui.pill.Pill
import com.example.apptest.ui.pill.PillInfoViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

//abstract
class ReadAndWrite {
    private lateinit var database: DatabaseReference

    fun initializeDbRef() {
        database = Firebase.database.reference
    }

    fun writeNewPillWithTaskListeners(pillId: String, pillName: String, count: Int, description: String) {
        database = Firebase.database.reference
        val pill = Pill(pillName, count, description)

        database.child("pills").child(pillId).setValue(pill)
            .addOnSuccessListener {
                // Write was successful!
                // ...
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }
    }

    // Unused -- Reading moved to PillInfoViewModel updated ver
    fun pillEventListener(dbReference: DatabaseReference){
        val pillListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val pill = dataSnapshot.getValue<Pill>()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        dbReference.addValueEventListener(pillListener)
    }

}
