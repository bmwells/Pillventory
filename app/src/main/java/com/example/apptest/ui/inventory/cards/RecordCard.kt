package com.example.apptest.ui.inventory.cards


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.apptest.ui.inventory.InventoryViewModel

// Create the record card composable to display extra information pertaining to the pill records
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordCard(
    pillId: String,
    pillCount: String,
    date: String,
    description: String,
    tags: String,
    imageRef: String,
    navigateRecord: (String) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    //val tagList = viewModel.parseTags(tags)

    Card( // Card Composable for grouping
        onClick = { navigateRecord(pillId) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .height(110.dp)
            .fillMaxWidth()
    ) {
        Row {

            // Load in the image using the given reference
            PillImage(imageRef)

            Column (
                modifier = Modifier
                    .padding(16.dp)
            ) {

                // Display the pill count for record
                Text(text = "Pill Count: $pillCount", fontSize = 18.sp)

                // Display the date for record
                Text(text = date, fontSize = 13.sp)

                // Spacer for UI readability
                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    // Iterate through each given tag in list and create a chip on the UI
                    viewModel.parseTagString(tags).forEach {tagName ->
                        AssistChip(
                            onClick = { Log.d("Tag name", "click deactivated") },
                            label = { Text(text = tagName) }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(13.dp))
}

// Retrieve the image from the image URL -> display image preview to individual record card
@Composable
fun PillImage(imageUrl: String) {
    val painter: AsyncImagePainter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = imageUrl).apply(block = fun ImageRequest.Builder.() {
        }).build()
    )
    // Load in the image with Image composable
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp)
            .aspectRatio(15F/15F, false)
            .clip(RoundedCornerShape(5.dp)),
        contentScale = ContentScale.Crop
    )
}

// Preview card elements
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecordCardPreview() {
    RecordCard( // Testing with hard coded values first
        pillId = "",
        pillCount = "82",
        date = "2/28/2024",
        description = "",
        tags = "Prescription, Ibuprofen", //mutableListOf("PRESCRIPTION", "IBUPROFEN"),
        imageRef = "",
        navigateRecord = {}
    )
}