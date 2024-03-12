package com.example.assignment6.ui.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.assignment6.R
import com.example.assignment6.data.RulesSource

@Composable
fun StartScreen(onStart: () -> Unit, modifier: Modifier = Modifier) {
    val rules = RulesSource.getRules()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier
            .weight(1f)
            .padding(8.dp)) {
            items(rules) { rule ->
                Text(
                    text = rule,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onStart() }) {
            Text(text = stringResource(R.string.start))
        }
    }
}
