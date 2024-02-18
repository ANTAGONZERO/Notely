
buildscript {
    repositories {
        // Add repositories for the build script itself if needed
        google()
        // Other repositories may be listed here
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }

}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
}



