package com.corral.androidshoppinglist

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class SplashActivity : Activity() {


    override fun onCreate(icicle : Bundle?) {
        super.onCreate(icicle)

        val intent = Intent(this, ActivityListaList::class.java)
        startActivity (intent)
        finish()
    }
}