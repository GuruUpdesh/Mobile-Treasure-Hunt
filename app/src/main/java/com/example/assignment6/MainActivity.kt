package com.example.assignment6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assignment6.ui.theme.Assignment6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment6Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TreasureHuntApp()
                }
            }
        }
    }
}

enum class TreasureHuntScreen(@StringRes val title: Int) {
    Permission(title = R.string.permissions_page),
    Start(title = R.string.start_page),
    Clue(title = R.string.clue_page),
    Solved(title = R.string.clue_solved_page),
    Complete(title = R.string.treasure_hunt_completed_page)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreasureHuntAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(title = {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    },
        modifier = modifier.border(width = 1.dp, color = Color.Gray),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        })
}

@Composable
fun TreasureHuntApp() {
    val navController = rememberNavController()

    var title = navController.currentBackStackEntryAsState().value?.destination?.route
    if (title == null) {
        title = ""
    }

    Scaffold(
        topBar = {
            TreasureHuntAppBar(
                title = title,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() })
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TreasureHuntScreen.Permission.name
        ) {
            composable(route = TreasureHuntScreen.Permission.name) {
                Box(modifier = Modifier.padding(innerPadding)) {
                    Text(text = "test")
                }
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
