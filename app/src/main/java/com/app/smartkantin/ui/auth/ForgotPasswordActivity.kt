package com.app.smartkantin.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.smartkantin.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val username = binding.etUsername.text.toString()
            if (username.isEmpty()) {
                Toast.makeText(this, "Masukkan username Anda", Toast.LENGTH_SHORT).show()
            } else {
                // Karena ini aplikasi lokal Room, kita hanya simulasi
                Toast.makeText(
                    this,
                    "Instruksi telah dikirim (Simulasi). Silakan hubungi admin kantin.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}