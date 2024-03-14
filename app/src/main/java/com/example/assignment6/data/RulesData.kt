/* Assignment 6: Mobile Treasure Hunt
    Guru Updesh Singh / singguru@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.assignment6.R

object RulesSource {
    @Composable
    fun getRules(): List<String> {
        return listOf(
            stringResource(R.string.rules_pre),
            stringResource(R.string.rule_1),
            stringResource(R.string.rule_2),
            stringResource(R.string.rule_3),
            stringResource(R.string.rule_4),
            stringResource(R.string.rule_5),
            stringResource(R.string.rule_6),
            stringResource(R.string.rule_7),
            stringResource(R.string.rule_8),
            stringResource(R.string.rule_9),
            stringResource(R.string.rule_10),
        )
    }
}
