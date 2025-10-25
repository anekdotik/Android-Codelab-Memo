package com.sap.codelab

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Extension of the Android Application class.
 */
@HiltAndroidApp
class MemoApplication : Application() {}