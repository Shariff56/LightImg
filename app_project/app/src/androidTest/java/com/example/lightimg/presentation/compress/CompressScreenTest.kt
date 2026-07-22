package com.example.lightimg.presentation.compress

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class CompressScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun compressScreen_rendersEmptyState() {
        composeTestRule.setContent {
            val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
            val useCase = com.example.lightimg.domain.usecase.CompressImageUseCase(context)
            val viewModel = com.example.lightimg.presentation.compress.CompressViewModel(useCase)
            com.example.lightimg.presentation.compress.CompressScreen(
                viewModel = viewModel
            )
        }
        
        // Wait for idle
        composeTestRule.waitForIdle()
        
        // Assert empty state is shown (the add button should be there)
        composeTestRule.onNodeWithText("Selected Files (0)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap to select images").assertIsDisplayed()
    }
}
