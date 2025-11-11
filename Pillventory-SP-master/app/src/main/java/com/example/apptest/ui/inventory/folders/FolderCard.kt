package com.example.apptest.ui.inventory.folders

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apptest.R

// Folder card used for personal folder display - navigate to folder contents onClick
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderCard(
    folderId: String,
    folderName: String,
    folderDescription: String,
    folderQuantity: String,
    folderRecords: String,
    navigateFolder: (String) -> Unit,
    viewModel: FolderViewModel = hiltViewModel()
) {

    Card(
        onClick = { navigateFolder(folderId) } , // Needs to be implemented
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
           // .padding(16.dp)
    ) {
        Row (modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.baseline_folder_open_24),
                contentDescription = "Folder icon"
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(text = folderName, fontSize = 18.sp)

            Spacer(modifier = Modifier.weight(1f))

            Text(text = folderQuantity, fontSize = 17.sp, color = Color.DarkGray)
        }

    }
    Spacer(modifier = Modifier.height(13.dp))
}


// Preview the folder card elements
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun FolderCardPreview() {
    FolderCard(
        folderId = "",
        folderName = "Test Record" ,
        folderDescription = "Sampling the inventory",
        folderQuantity = "3",
        folderRecords = "",
        navigateFolder = {}
    )
}