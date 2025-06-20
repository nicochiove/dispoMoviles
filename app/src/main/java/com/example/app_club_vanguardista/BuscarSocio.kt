package com.example.app_club_vanguardista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BuscarSocio : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_socio)

        dbHelper = UserDBHelper(this)

        val btnBuscar = findViewById<Button>(R.id.btnBuscar)

        val edtDni = findViewById<EditText>(R.id.edtUsuario4)
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrar)

        btnBuscar.setOnClickListener {

            val dni = edtDni.text.toString().trim()

            if (dni.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val socio = dbHelper.buscarSocio(dni)

            if (socio != null) {
                val intent = Intent(this, SocioEncontradoActivity::class.java).apply {

                    putExtra("nombre", socio.nombre)
                    putExtra("apellido", socio.apellido)
                    putExtra("dni", socio.dni)
                    putExtra("tipoCliente", socio.tipoCliente)
                    putExtra("fechaAlta", socio.fechaAlta)
                    putExtra("aptoFisico", socio.aptoFisico)
                    putExtra("foto", socio.foto)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        btnCerrar.setOnClickListener {
            finish()
        }
    }
}