package com.example.tournaMake.ui.theme

import com.example.tournaMake.R

/**
 * A function that returns the path to the right background image, based
 * on the theme settings of the application.
 * */
fun chooseBackgroundBasedOnTheme(darkMode: Boolean) : Int {
    return if (darkMode) R.drawable.dark_background else R.drawable.light_background
}