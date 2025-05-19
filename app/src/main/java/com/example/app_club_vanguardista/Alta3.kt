package com.example.app_club_vanguardista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Alta3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alta3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<Button>(R.id.btncargar)
        button.setOnClickListener {
            val intent = Intent(this, AltaExitosa::class.java)
            startActivity(intent)
        }
        val btnCerrar = findViewById<ImageButton>(R.id.btn_cerrarX_alta)
        btnCerrar.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)

        }
    }}