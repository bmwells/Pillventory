package com.example.apptest.ui.inventory.cards

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Composable for individual image preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalGridCard(
    pillId: String,
    pillCount: String,
    date: String,
    description :String,
    tags: String,
    imageRef: String,
    navigateRecord: (String)-> Unit
) {
    // Card is necessary to implement the clickable aspect of each image preview
    Card(
        onClick = { navigateRecord(pillId) },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Box (
            modifier=Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            PillImage(imageRef) // Reuse pill image loader written in the RecordCard file

            // Want to layer the count over the image
            Text(
                text = pillCount,
                color = Color.White,
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
