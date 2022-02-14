plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}
android {
    compileSdk = 31

    defaultConfig {
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.versionCode
        versionName = Config.versionName

        testInstrumentationRunner =
            "com.denis.github_users.MainTestRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
                arguments["room.expandProjection"] = "true"
            }
        }
    }
    // this solves Espresso issue https://stackoverflow.com/questions/67358179/android-espresso-test-error-no-static-method-loadsingleserviceornull
    configurations.all {
        resolutionStrategy {
            force("androidx.test:monitor:1.4.0")
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders["usesCleartextTraffic"] = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["usesCleartextTraffic"] = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        isEnabled = true
    }
}

dependencies {
    Deps.apply {
        // core section
        implementation(core_ktx)
        implementation(appcompat)
        implementation(fragment_ktx)
        // presentation section
        implementation(material)
        implementation(constraintlayout)
        // lifecycle section
        implementation(lifecycle_livedata_ktx)
        implementation(lifecycle_viewmodel_ktx)
        // navigation component
        implementation(navigation_fragment_ktx)
        implementation(navigation_ui_ktx)
        // dependency injection section
        implementation(hilt_android)
        implementation(hilt_lifecycle_viewmodel)
        kapt(hilt_android_compiler)
        kapt(hilt_compiler)
        // network section
        implementation(retrofit)
        implementation(converter_gson)
        // paging section
        implementation(paging_runtime_ktx)
        // image downloading and presenting
        implementation(coil_kt)
        // database section
        implementation(room_ktx)
        implementation(room_runtime)
        implementation(room_paging)
        kapt(room_compiler)
    }
    // unit test section
    TestDeps.apply {
        testImplementation(junit)
        testImplementation(mockk)
        testImplementation(coroutine_test)
        testImplementation(turbine)
        // instrumentation test section
        androidTestImplementation(androidx_junit)
        androidTestImplementation(espresso_core)
        androidTestImplementation(espresso_web)
        androidTestImplementation(espresso_intents)
        androidTestImplementation(espresso_contrib)
        androidTestImplementation(hilt_android_testing)
        androidTestImplementation(mockk_android)
        androidTestImplementation(mockwebserver)
        androidTestImplementation(uiautomator)
        androidTestImplementation(androidx_test_rules)
        androidTestImplementation(androidx_test_runner)
        androidTestImplementation(awaitility)
        debugImplementation(androidx_fragment)
        debugImplementation(androidx_fragment_testing)
        androidTestImplementation(navigation_testing)
        androidTestImplementation(room_testing)
        androidTestImplementation(Deps.fragment_ktx)
        kaptAndroidTest(Deps.hilt_android_compiler)
        androidTestAnnotationProcessor(Deps.hilt_android_compiler)
        androidTestImplementation(coroutine_test)
    }
}