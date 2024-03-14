/* Assignment 6: Mobile Treasure Hunt
    Guru Updesh Singh / singguru@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6.model

import androidx.annotation.StringRes

data class Clue(
    @StringRes val textualClueResourceId: Int,
    @StringRes val hintResourceId: Int,
    @StringRes val locationInfoResourceId: Int,
    val geo: Geo
)
