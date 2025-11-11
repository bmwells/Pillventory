package com.example.apptest.ui.navigation

interface NavigationDestination {
    // Name to define the path for a composable
    val route: String

    // Resource ID containing title displayed on screen
    val titleRes: Int
}
