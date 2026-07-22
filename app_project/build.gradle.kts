// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Note: AGP 9.0+ has built-in Kotlin support; org.jetbrains.kotlin.android is not needed.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kotlin.serialization) apply false
}