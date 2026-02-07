package com.fourshil.musicya.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.*

/**
 * Animated navigation transitions for smooth screen changes.
 */
object NavigationAnimations {

    /**
     * Default enter transition - fade in with slide up.
     */
    @Composable
    fun defaultEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> Unit = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.NavigationDirection.Left,
            initialOffset = { it / 4 }
        ) + fadeIn(animationSpec = tween(300))
    }

    /**
     * Default exit transition - fade out with slide left.
     */
    @Composable
    fun defaultExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> Unit = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.NavigationDirection.Left,
            targetOffset = { -it / 4 }
        ) + fadeOut(animationSpec = tween(300))
    }

    /**
     * Pop enter transition - fade in with slide right.
     */
    @Composable
    fun popEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> Unit = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.NavigationDirection.Right,
            initialOffset = { -it / 4 }
        ) + fadeIn(animationSpec = tween(300))
    }

    /**
     * Pop exit transition - fade out with slide right.
     */
    @Composable
    fun popExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> Unit = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.NavigationDirection.Right,
            targetOffset = { it / 4 }
        ) + fadeOut(animationSpec = tween(300))
    }

    /**
     * Fade only transition for modals.
     */
    @Composable
    fun modalEnterTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> Unit = {
        fadeIn(animationSpec = tween(200))
    }

    /**
     * Fade out for modal dismissal.
     */
    @Composable
    fun modalExitTransition(): AnimatedContentTransitionScope<NavBackStackEntry>.() -> Unit = {
        fadeOut(animationSpec = tween(200))
    }
}

/**
 * Animated visibility for content that needs to fade/scale in.
 */
@Composable
fun AnimatedContent(
    visible: Boolean,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.95f),
        exit = fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.95f)
    ) {
        content()
    }
}