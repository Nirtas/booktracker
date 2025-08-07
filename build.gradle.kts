plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false

    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktor) apply false

    alias(libs.plugins.kotlin.serialization) apply false
}

group = "ru.jerael.booktracker"
version = property("booktracker.version.name")!!