object Deps {
    // core section
    const val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx_version}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat_version}"
    const val fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.fragment_ktx_version}"

    // presentation section
    const val material = "com.google.android.material:material:${Versions.material_version}"
    const val constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout_version}"

    // lifecycle section
    const val lifecycle_livedata_ktx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle_livedata_ktx_version}"
    const val lifecycle_viewmodel_ktx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_viewmodel_ktx_version}"

    // navigation component
    const val navigation_fragment_ktx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation_version}"
    const val navigation_ui_ktx =
        "androidx.navigation:navigation-ui-ktx:${Versions.navigation_version}"
    const val navigation_safe_args_gradle_plugin =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation_version}"

    // retrofit 2 section
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    const val converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit_version}"

    // androidx - paging
    const val paging_runtime_ktx = "androidx.paging:paging-runtime-ktx:${Versions.paging_version}"

    // image downloading and presenting section
    const val coil_kt = "io.coil-kt:coil:${Versions.coil_kt_version}"

    // database section
    const val room_ktx = "androidx.room:room-ktx:${Versions.room_version}"
    const val room_runtime = "androidx.room:room-runtime:${Versions.room_version}"
    const val room_compiler = "androidx.room:room-compiler:${Versions.room_version}"
    const val room_paging = "androidx.room:room-paging:${Versions.room_version}"

    // dependency injection section
    const val hilt_android = "com.google.dagger:hilt-android:${Versions.hilt_version}"
    const val hilt_android_compiler =
        "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"
    const val hilt_android_gradle_plugin =
        "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt_version}"
    const val hilt_compiler = "androidx.hilt:hilt-compiler:${Versions.androidx_hilt_version}"
    const val hilt_lifecycle_viewmodel =
        "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.androidx_hilt_version}"
}

object TestDeps {

    // unit test section
    const val junit = "junit:junit:${Versions.junit_version}"

    // mockk test section
    const val mockk = "io.mockk:mockk:${Versions.mockk_version}"
    const val mockk_android = "io.mockk:mockk-android:${Versions.mockk_version}"

    // coroutine test section
    const val coroutine_test =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinx_coroutines_test_version}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine_version}"

    // instrumentation test section
    const val androidx_junit = "androidx.test.ext:junit:${Versions.androidx_junit_version}"
    const val androidx_test_rules = "androidx.test:rules:${Versions.androidx_test_version}"
    const val androidx_test_runner = "androidx.test:runner:${Versions.androidx_test_version}"
    const val awaitility = "org.awaitility:awaitility-kotlin:${Versions.awaitility_version}"
    const val espresso_core =
        "androidx.test.espresso:espresso-core:${Versions.androidx_espresso_core_version}"
    const val espresso_web = "androidx.test.espresso:espresso-web:${Versions.androidx_espresso_core_version}"
    const val espresso_intents = "androidx.test.espresso:espresso-intents:${Versions.androidx_espresso_core_version}"
    const val espresso_contrib = "androidx.test.espresso:espresso-contrib:${Versions.androidx_espresso_core_version}"
    const val hilt_android_testing =
        "com.google.dagger:hilt-android-testing:${Versions.hilt_version}"
    const val uiautomator = "androidx.test.uiautomator:uiautomator:${Versions.uiautomator_version}"
    const val androidx_fragment = "androidx.fragment:fragment:${Versions.fragment_version}"
    const val androidx_fragment_testing = "androidx.fragment:fragment-testing:${Versions.fragment_version}"
    const val navigation_testing = "androidx.navigation:navigation-testing:${Versions.navigation_version}"

    // network mock
    const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${Versions.mockwebserver_version}"

    // database test section
    const val room_testing = "androidx.room:room-testing:${Versions.room_version}"
}