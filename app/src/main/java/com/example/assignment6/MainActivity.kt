/* Assignment 6: Mobile Treasure Hunt
    Guru Updesh Singh / singguru@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assignment6.data.ClueDataSource
import com.example.assignment6.model.Geo
import com.example.assignment6.ui.ClueViewModel
import com.example.assignment6.ui.TimerViewModel
import com.example.assignment6.ui.clue.ClueScreen
import com.example.assignment6.ui.complete.CompleteScreen
import com.example.assignment6.ui.permissions.PermissionsScreen
import com.example.assignment6.ui.solved.SolvedScreen
import com.example.assignment6.ui.start.StartScreen
import com.example.assignment6.ui.theme.Assignment6Theme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    TreasureHuntApp()
                }
            }
        }
    }
}

enum class TreasureHuntScreen(@StringRes val title: Int) {
    Permission(title = R.string.permissions_page), Start(title = R.string.start_page), Clue(title = R.string.clue_page), Solved(
        title = R.string.clue_solved_page
    ),
    Complete(title = R.string.treasure_hunt_completed_page)
}

fun getTimerString(elapsedSeconds: Int): String {
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreasureHuntAppBar(
    title: String,
    canNavigateBack: Boolean,
    quit: () -> Unit,
    elapsedSeconds: Int,
    modifier: Modifier = Modifier,
) {
    val timeString = getTimerString(elapsedSeconds)
    val showTimer = elapsedSeconds > 0

    TopAppBar(title = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (canNavigateBack) {
                Button(
                    onClick = { quit() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Text(
                        text = stringResource(R.string.quit_button),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = title, style = MaterialTheme.typography.bodyLarge
            )
            if (showTimer) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = timeString, style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }, modifier = modifier.border(width = 1.dp, color = Color.Gray), navigationIcon = {

    })
}

@Composable
fun TreasureHuntApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // get the title of the current screen
    var title = navController.currentBackStackEntryAsState().value?.destination?.route
    if (title == null) {
        title = ""
    }

    // determine the start route based off permission state
    var startDestination = TreasureHuntScreen.Start.name

    if (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        startDestination = TreasureHuntScreen.Permission.name
    }

    // timer view model
    val timerViewModel: TimerViewModel = viewModel()
    val elapsedSeconds by timerViewModel.seconds.collectAsState(initial = 0)
    fun handleHuntStart() {
        navController.navigate(TreasureHuntScreen.Clue.name)
        timerViewModel.toggleTimer()
    }

    // clue view model
    val clueViewModel: ClueViewModel = viewModel()
    val hintVisible by clueViewModel.hintVisible.collectAsState()
    val currentClueIndex by clueViewModel.currentClueIndex.collectAsState()
    val currentClue = ClueDataSource.clues[currentClueIndex]

    fun quit() {
        clueViewModel.resetHunt()
        navController.navigate(TreasureHuntScreen.Start.name)
        timerViewModel.resetTimer()
    }

    // location information
    val scope = rememberCoroutineScope()
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var locationInfo by remember {
        mutableStateOf("")
    }

    // handle when the clue gets found
    fun foundCallback() {
        if (currentClueIndex == ClueDataSource.clues.size - 1) {
            navController.navigate(TreasureHuntScreen.Complete.name)
        } else {
            navController.navigate(TreasureHuntScreen.Solved.name)
        }
        timerViewModel.toggleTimer()
    }

    // show the alert when the guess is incorrect
    fun notFoundCallback() {
        clueViewModel.showAlert()
    }

    fun foundCheck() {
        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    locationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token,
                    ).await()
                } catch (e: Exception) {
                    null
                }
            }
            result?.let { fetchedLocation ->
                locationInfo =
                    "Current location is \nlat: ${fetchedLocation.latitude}\nlong: ${fetchedLocation.longitude}\nfetched at ${System.currentTimeMillis()}"
                Log.d("clue location", currentClue.geo.toString())
                val current = Geo(fetchedLocation.latitude, fetchedLocation.longitude)
                val distance = currentClue.geo.haversine(current)
                val distanceInMeters = distance * 1000
                Log.d("location", "$distanceInMeters M from location")
                if (distanceInMeters < 5) {
                    foundCallback()
                } else {
                    notFoundCallback()
                }
            }
        }
    }

    fun nextClue() {
        navController.navigate(TreasureHuntScreen.Clue.name)
        clueViewModel.nextClue()
        timerViewModel.toggleTimer()
    }

    val timeString = getTimerString(elapsedSeconds)

    val isCurrentRouteClueOrSolved = navController.currentDestination?.route in listOf(
        TreasureHuntScreen.Clue.name, TreasureHuntScreen.Solved.name
    )
    val canNavigateBack = navController.previousBackStackEntry != null && isCurrentRouteClueOrSolved

    // alert logic
    val showIncorrectAlert by clueViewModel.showIncorrectAlert.collectAsState()

    fun closeAlert() {
        clueViewModel.closeAlert()
    }

    if (showIncorrectAlert) {
        AlertDialog(onDismissRequest = { closeAlert() },
            title = { Text(text = stringResource(R.string.try_again)) },
            text = { Text(text = stringResource(R.string.not_right)) },
            confirmButton = {
                Button(onClick = { closeAlert() }) {
                    Text(text = stringResource(R.string.ok))
                }
            })
    }

    Scaffold(topBar = {
        TreasureHuntAppBar(
            title = title,
            canNavigateBack = canNavigateBack,
            quit = { quit() },
            elapsedSeconds = elapsedSeconds
        )
    }) { innerPadding ->
        NavHost(
            navController = navController, startDestination = startDestination
        ) {
            composable(route = TreasureHuntScreen.Start.name) {
                StartScreen(
                    onStart = { handleHuntStart() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(route = TreasureHuntScreen.Permission.name) {
                PermissionsScreen(
                    onPermissionsGranted = { navController.navigate(TreasureHuntScreen.Start.name) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(route = TreasureHuntScreen.Clue.name) {
                ClueScreen(
                    currentClue = currentClue,
                    isHintVisible = hintVisible,
                    afterFound = { foundCheck() },
                    showHint = clueViewModel::showHint,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(route = TreasureHuntScreen.Solved.name) {
                SolvedScreen(
                    currentClue = currentClue,
                    next = { nextClue() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(route = TreasureHuntScreen.Complete.name) {
                CompleteScreen(
                    timeString = timeString,
                    currentClue = currentClue,
                    home = { quit() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Assignment6Theme {
        TreasureHuntApp()
    }
}
