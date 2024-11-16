package com.example.fable.view.matcher

import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ToastMatcher : TypeSafeMatcher<Root>() {
    override fun matchesSafely(root: Root): Boolean {
        return root.decorView.windowToken != null && root.decorView.windowToken == root.decorView.applicationWindowToken
    }

    override fun describeTo(description: Description) {
        description.appendText("is toast")
    }
}