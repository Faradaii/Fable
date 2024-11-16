package com.example.fable.view.matcher

import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class SnackbarMatcher : TypeSafeMatcher<Root>() {
    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get()?.type
        return type == WindowManager.LayoutParams.TYPE_APPLICATION
    }

    override fun describeTo(description: Description) {
        description.appendText("is snackbar")
    }
}