package com.example.apptest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.apptest.ui.camera.CameraDestination
import com.example.apptest.ui.camera.CameraScreen
import com.example.apptest.ui.camera.CountResultDestination
import com.example.apptest.ui.camera.CountResultDestination.countArg
import com.example.apptest.ui.camera.CountResultsScreen
import com.example.apptest.ui.inventory.FilterBody
import com.example.apptest.ui.inventory.InventoryDestination
import com.example.apptest.ui.inventory.InventoryScreen
import com.example.apptest.ui.inventory.PillRecordScreen
import com.example.apptest.ui.inventory.RecordDestination
import com.example.apptest.ui.inventory.RecordDestination.itemArg
import com.example.apptest.ui.inventory.alarm.AlarmScreenDestination
import com.example.apptest.ui.inventory.alarm.SetAlarmScreen
import com.example.apptest.ui.inventory.calculator.CalculatorDestination
import com.example.apptest.ui.inventory.calculator.CalculatorScreen
import com.example.apptest.ui.inventory.folders.FolderDestination
import com.example.apptest.ui.inventory.folders.FolderRecordsDestination
import com.example.apptest.ui.inventory.folders.FolderRecordsDestination.folderArg
import com.example.apptest.ui.inventory.folders.FolderRecordsDestination.userArg
import com.example.apptest.ui.inventory.folders.FolderRecordsScreen
import com.example.apptest.ui.inventory.folders.FolderScreen
import com.example.apptest.ui.inventory.metrics.MetricsScreen
import com.example.apptest.ui.inventory.metrics.MetricsScreenDestination
import com.example.apptest.ui.inventory.tags.AddTagDestination
import com.example.apptest.ui.inventory.tags.AddTagScreen
import com.example.apptest.ui.landing.LandingDestination
import com.example.apptest.ui.landing.LandingScreen
import com.example.apptest.ui.landing.forgotpassword.ForgotPasswordDestination
import com.example.apptest.ui.landing.forgotpassword.ForgotPasswordScreen
import com.example.apptest.ui.landing.login.LoginDestination
import com.example.apptest.ui.landing.login.LoginScreen
import com.example.apptest.ui.landing.signup.SignUpDestination
import com.example.apptest.ui.landing.signup.SignUpScreen
import com.example.apptest.ui.landing.signup.VerifyScreen
import com.example.apptest.ui.landing.signup.VerifyScreenDestination
import com.example.apptest.ui.landing.tagmanagement.TagManagementScreen
import com.example.apptest.ui.landing.tagmanagement.TagScreeDestination
import com.example.apptest.ui.settings.SettingsDestination
import com.example.apptest.ui.settings.SettingsScreen
import com.example.apptest.ui.settings.camerasettings.CameraSettingsDestination
import com.example.apptest.ui.settings.camerasettings.CameraSettingsScreen
import com.example.apptest.ui.settings.contact.BugReportDestination
import com.example.apptest.ui.settings.contact.BugReportScreen
import com.example.apptest.ui.settings.contact.ContactDestination
import com.example.apptest.ui.settings.contact.ContactScreen
import com.example.apptest.ui.settings.help.HelpDestination
import com.example.apptest.ui.settings.help.HelpScreen
import com.example.apptest.ui.settings.help.PatchNotesDestination
import com.example.apptest.ui.settings.help.PatchNotesScreen
import com.example.apptest.ui.settings.profilesettings.DeleteAccountDestination
import com.example.apptest.ui.settings.profilesettings.DeleteAccountScreen
import com.example.apptest.ui.settings.profilesettings.ProfileSettingsDestination
import com.example.apptest.ui.settings.profilesettings.ProfileSettingsScreen
import com.example.apptest.ui.settings.profilesettings.ResetPasswordDestination
import com.example.apptest.ui.settings.profilesettings.ResetPasswordScreen


@Composable
fun PillNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LandingDestination.route,
        modifier= modifier
    ) {
        composable(route = LandingDestination.route) {
            LandingScreen(
                navigateToSignUp = {
                    navController.navigate(SignUpDestination.route)
                },
                navigateToLogin = {
                    navController.navigate(LoginDestination.route)
                },
                navigateGuest = {
                    navController.navigate(CameraDestination.route)
                }
            )
        }

        composable(route = LoginDestination.route) {
            LoginScreen(
                onNavigateUp = {
                    navController.navigate(LandingDestination.route)
                },
                navigateForgotPass = {
                    navController.navigate(ForgotPasswordDestination.route) // Implement Forgot Password Later ....   (want to test if button works though)
                },
                navigateCamera = {
                    navController.navigate(CameraDestination.route) // Probably change when authentication is in place
                }
            )
        }

        composable(route = ForgotPasswordDestination.route) {
            ForgotPasswordScreen(
                onNavigateUp = {
                    navController.navigate(LoginDestination.route)
                },
                navigateLogin = {
                    navController.navigate(LoginDestination.route)
                }
            )
        }

        composable(route = ResetPasswordDestination.route) {
            ResetPasswordScreen(
                navController = navController,
                onNavigateUp = {
                    navController.popBackStack()
                },
                navigateToProfileSettings = {
                    navController.navigate(ProfileSettingsDestination.route)
                }
            )
        }

        composable(route = VerifyScreenDestination.route) {
            VerifyScreen(
                onNavigateUp = {
                    navController.navigate(LoginDestination.route)
                },
                navigateLogin = {
                    navController.navigate(LoginDestination.route)
                } // hope this works
            )
        }

        composable(route = SignUpDestination.route) {
            // Sign Up / Create account page
            SignUpScreen(
                onNavigateUp = {
                    navController.navigate(LandingDestination.route)
                },

                onEnterClick = {
                    navController.navigate(CameraDestination.route)
                },
                navigateVerify = {
                    navController.navigate(VerifyScreenDestination.route)
                }
            )
        }

        composable(route = CameraDestination.route) {
            // Guest
            CameraScreen(
                navController = navController,
                navPillResults = {
                    countArg -> navController.navigate("${CountResultDestination.route}/$countArg")
                }
            )
        }

        composable(
            route = CountResultDestination.routeWithArgs,
            arguments = listOf(
                navArgument(countArg){
                    type = NavType.IntType
                },
            )
            ) {
            CountResultsScreen(
                navigateCamera = {
                    navController.navigate(CameraDestination.route)
                },
                navigateAddTags = {} // Not created yet
            )
        }
        composable(route = InventoryDestination.route) {
            InventoryScreen(
                navController = navController,
                navigateSignIn = {
                    navController.navigate(SignUpDestination.route)
                },
                navigateLogin = {
                    navController.navigate(LoginDestination.route)
                },

                navigateRecord = { itemArg: String ->
                    navController.navigate("${RecordDestination.route}/$itemArg") // send pill id
                }
            )
        }
//        composable(route = SearchFilterDestination.route){
//            FilterBody(
//
//            )
//        }
        // Settings Nav
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                navController = navController,
                navigateToProfileSettings = {
                    navController.navigate(ProfileSettingsDestination.route)
                },
                navigateToCameraSettings = {
                    navController.navigate(CameraSettingsDestination.route)
                },
                navigateToHelp = {
                    navController.navigate(HelpDestination.route)
                },
                navigateToContact = {
                    navController.navigate(ContactDestination.route)
                },
                navigateToBugReport = {
                    navController.navigate(BugReportDestination.route)
                }
            )
        }
        composable(route = ProfileSettingsDestination.route) {
            ProfileSettingsScreen(
                navController = navController,
                navigateToDeleteAccount = {
                    navController.navigate(DeleteAccountDestination.route)
                },
                navigateToLandingPage = {
                    navController.navigate(LandingDestination.route)
                },
                navigateResetPass = {
                    navController.navigate(ResetPasswordDestination.route)
                }
            )

        }
        composable(route = CameraSettingsDestination.route) {
            CameraSettingsScreen(
                navController = navController,
            )
        }

        composable(route = HelpDestination.route) {
            HelpScreen(
                navController = navController,
                navigateToPatchNotes = {
                    navController.navigate(PatchNotesDestination.route)
                },
            )
        }
        composable(route = PatchNotesDestination.route) {
            PatchNotesScreen(
                navController = navController,
            )
        }
        composable(route = ContactDestination.route) {
            ContactScreen(
                navController = navController,
            )
        }
        composable(route = DeleteAccountDestination.route) {
            DeleteAccountScreen(
                navController = navController,
                navigateToLandingPage = {
                    navController.navigate(LandingDestination.route)
                },
            )
        }
        composable(route = BugReportDestination.route) {
            BugReportScreen(
                navController = navController,
            )
        }



        composable(route = CountResultDestination.route) {
            CountResultsScreen(
                navigateCamera = {
                    navController.navigate(CameraDestination.route)
                },
                navigateAddTags = {} // Not created yet
            )
        }


        composable(
            route = RecordDestination.routeWithArgs,
            // Receives
            arguments = listOf(navArgument(itemArg) {
                type = NavType.StringType
            })
        ) {
            PillRecordScreen (
                onNavigateUp = {
                    navController.navigate(InventoryDestination.route)
                },
                navigateAddTagScreen = {
                    navController.navigate(AddTagDestination.route)
                }
            )
        }


        composable(route = CalculatorDestination.route) {
            CalculatorScreen(navController = navController)
        }

        composable(route = FolderDestination.route) {
            FolderScreen(
                navController = navController,
                navigateFolder = { folderArg, userArg ->
                    navController.navigate("${FolderRecordsDestination.route}/$folderArg/$userArg") // send folder id
                }
            )
        }

        composable(
            route = FolderRecordsDestination.routeWithArgs,
            arguments = listOf(
                navArgument(folderArg){
                    type = NavType.StringType
                },
                navArgument(userArg) {
                    type = NavType.StringType
                }
            )
        ) {
            FolderRecordsScreen (
                onNavigateUp = {
                    navController.navigate(FolderDestination.route)
                    //navController.popBackStack()
                }
            )
        }
        
        composable(route = TagScreeDestination.route) {
            TagManagementScreen(navController = navController)
        }

        composable(route = AddTagDestination.route){
            AddTagScreen(navController = navController)
        }
        
        composable(route = AlarmScreenDestination.route){
            SetAlarmScreen(navController = navController)
        }
        composable(route = MetricsScreenDestination.route){
            MetricsScreen(navController = navController)

            }
        }



    }


