package ru.snakelord.philosofidget.presentation.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import ru.snakelord.philosofidget.R
import ru.snakelord.philosofidget.presentation.view.widget_settings.WidgetSettingsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container_view, WidgetSettingsFragment.newInstance(), WidgetSettingsFragment.TAG)
            }
        }
    }
}