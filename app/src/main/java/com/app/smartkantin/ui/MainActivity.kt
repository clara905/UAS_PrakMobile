package com.app.smartkantin.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.smartkantin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvWelcome.text = "Selamat Datang di Smart Kantin"
    }
}