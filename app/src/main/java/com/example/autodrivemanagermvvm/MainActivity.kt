package com.example.autodrivemanagermvvm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavOptions
import com.example.autodrivemanagermvvm.databinding.ActivityMainBinding
import com.example.autodrivemanagermvvm.data.session.SessionEvent
import com.example.autodrivemanagermvvm.data.session.SessionEventBus
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asegura que el NavHostFragment está inicializado.
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                SessionEventBus.events.collect { event ->
                    if (event is SessionEvent.Expired) {
                        Toast.makeText(this@MainActivity, "Sesión expirada", Toast.LENGTH_LONG).show()
                        val options = NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(R.id.nav_graph, false)
                            .build()
                        navController.navigate(R.id.loginFragment, null, options)
                    }
                }
            }
        }
    }
}