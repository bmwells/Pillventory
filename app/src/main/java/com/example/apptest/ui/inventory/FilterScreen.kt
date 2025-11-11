package com.example.apptest.ui.inventory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import java.util.Date

// Joe
// Navigation to get to filter screen
object SearchFilterDestination: NavigationDestination {
    override val route = "filter"
    override val titleRes = R.string.inventory_title // Added to XML because this is type Int
}
// Joe
@Composable
fun FilterBody(){
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pill Count Range Section
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Pill Count Range",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = { /* No action needed */ },
                label = { Text("Start Count") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = "",
                onValueChange = { /* No action needed */ },
                label = { Text("End Count") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))

        // Date Range Section
        Text(
            text = "Date Range",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = startDate?.toString() ?: "",
                onValueChange = { /* No action needed */ },
                label = { Text("Start Date") },
                modifier = Modifier.weight(1f),
                readOnly = true
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = endDate?.toString() ?: "",
                onValueChange = { /* No action needed */ },
                label = { Text("End Date") },
                modifier = Modifier.weight(1f),
                readOnly = true
            )
        }

        Spacer(modifier = Modifier.height(301.dp))

        // Filter Button
        Button(
            onClick = { /* Handle filter click here */ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Filter")
        }
    }
}