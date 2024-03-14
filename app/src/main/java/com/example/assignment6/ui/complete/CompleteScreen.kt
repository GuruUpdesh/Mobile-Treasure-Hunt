/* Assignment 6: Mobile Treasure Hunt
    Guru Updesh Singh / singguru@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.ui.complete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.assignment6.R
import com.example.assignment6.model.Clue

@Composable
fun CompleteScreen(
    timeString: String,
    currentClue: Clue,
    home: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.complete),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = stringResource(R.string.total_time),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = timeString,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = stringResource(currentClue.locationInfoResourceId),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )
        Button(onClick = { home() }) {
            Text(
                text = stringResource(R.string.home_button)
            )
        }
    }
}