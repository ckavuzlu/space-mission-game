package com.example.spacemission.util

import androidx.annotation.ColorInt

object Constants {
    @ColorInt val COLOR_TEXT : Int  = 0xFFFF03DAC5.toInt()

    const val DATABASE_NAME : String = "space_mission"
    const val BASE_URL = "https://run.mocky.io/v3/e7211664-cbb6-4357-9c9d-f12bf8bab2e2/"

    const val GAME_FRAGMENT : String = "GameFragment"
    const val FAV_LIST_FRAGMENT : String = "FavListFragment"
    const val CONFIG_FRAGMENT : String = "ConfigFragment"
    const val FRAGMENT_STATE = "FragmentState"

    const val INITIAL_CURRENT_LOCATION : String = "Dünya"
    const val INITIAL_SKILL_POINT  = 0F
    const val INITIAL_REMAINING_POINTS  = 15

    const val DURABILITY_MULTIPLY = 10000
    const val SPEED_MULTIPLY = 20
    const val STORAGE_MULTIPLY = 10000

}