package com.example.lightimg.core.navigation

import kotlinx.serialization.Serializable

/** Type-safe navigation destinations for Nav3. */
sealed interface Screen {

    @Serializable data object Home      : Screen
    @Serializable data object Compress  : Screen
    @Serializable data object ResizeCrop: Screen
    @Serializable data object Convert   : Screen
    @Serializable data object Pdf       : Screen
    @Serializable data object Settings  : Screen

    /** Bottom-nav tabs in order. */
    companion object {
        val bottomNavItems = listOf(Compress, ResizeCrop, Convert, Pdf)
    }
}
