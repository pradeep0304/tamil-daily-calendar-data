package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"]) // Needed for some compose + robolectric issues
class PoruthamIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToPorutham() {
        composeTestRule.setContent {
            val poruthamRepository = com.example.data.PoruthamRepository(
                androidx.room.Room.inMemoryDatabaseBuilder(
                    androidx.test.core.app.ApplicationProvider.getApplicationContext(),
                    com.example.data.AppDatabase::class.java
                ).allowMainThreadQueries().build().poruthamDao()
            )
            MainAppContent(
                poruthamViewModel = com.example.ui.PoruthamViewModel(poruthamRepository)
            )
        }
        
        // Wait for idle
        composeTestRule.waitForIdle()

        // Click on Porutham tab
        composeTestRule.onNodeWithTag("nav_item_porutham").performClick()
        
        composeTestRule.waitForIdle()
        
        // Check if we are on the Porutham screen
        // Use Tamil translated string since by default isTamil is true
        composeTestRule.onNodeWithText("ஜாதக பொருத்தம்").assertExists()
    }
}
