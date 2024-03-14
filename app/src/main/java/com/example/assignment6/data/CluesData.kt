/* Assignment 6: Mobile Treasure Hunt
    Guru Updesh Singh / singguru@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.data

import com.example.assignment6.R
import com.example.assignment6.model.Clue
import com.example.assignment6.model.Geo

object ClueDataSource {
    val clues = listOf(
        Clue(
            R.string.clue_1_textual,
            R.string.clue_1_hint,
            R.string.library_info,
            Geo(44.0485, -123.0949)
        ),
        Clue(
            R.string.clue_2_textual,
            R.string.clue_2_hint,
            R.string.butte_info,
            Geo(43.9894, -123.0973)
        ),
    )
}