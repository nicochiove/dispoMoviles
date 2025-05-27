package com.example.app_club_vanguardista
import android.content.Intent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val dbHelper = UserDBHelper(this)

        val user=findViewById<EditText>(R.id.edtUsuario)
        val pass=findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val userString = user.text.toString().trim()
            val passString = pass.text.toString().trim()

            if (dbHelper.login(userString, passString)) {
                val intent = Intent(this, MainMenu::class.java)
                startActivity(intent)
            } else {
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setTitle("Error de inicio de sesión")
                builder.setMessage("Datos incorrectos. Por favor, verifica tu usuario y contraseña.")
                builder.setPositiveButton("Aceptar", null)
                builder.show()
            }

        }
        
    }

}

