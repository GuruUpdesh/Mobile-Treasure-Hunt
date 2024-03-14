/* Assignment 6: Mobile Treasure Hunt
    Guru Updesh Singh / singguru@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.ui.clue

import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.assignment6.R
import com.example.assignment6.model.Clue
import android.Manifest

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun ClueScreen(
    currentClue: Clue,
    isHintVisible: Boolean,
    afterFound: () -> Unit,
    showHint: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(currentClue.textualClueResourceId),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
        if (isHintVisible) {
            Text(
                text = stringResource(currentClue.hintResourceId),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        } else {
            Button(
                onClick = { showHint() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer),
            ) {
                Text(
                    text = stringResource(R.string.show_hint),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
        Button(onClick = { afterFound() }) {
            Text(
                text = stringResource(R.string.found_it)
            )
        }
    }
}

