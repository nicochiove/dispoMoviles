package com.example.app_club_vanguardista

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.text.equals
import kotlin.text.trim
import android.widget.Toast
class SocioEncontradoActivity : AppCompatActivity() {

    private lateinit var edtNamePagar: EditText
    private lateinit var edtSurnamePagar: EditText
    private lateinit var edtDniPagar: EditText
    private lateinit var imgAvatar: ImageView
    private lateinit var edtTcliente: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.socio_encontrado)

        edtNamePagar = findViewById(R.id.edtNamePagar)
        edtSurnamePagar = findViewById(R.id.edtSurnamepagar)
        edtDniPagar = findViewById(R.id.edtDnipagar)
        imgAvatar = findViewById(R.id.imgAvatar)
        edtTcliente=findViewById(R.id.edtDnipagar2)
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarencontrado)
        val btnPagar = findViewById<Button>(R.id.btnPagar)
        val tcliente=intent.getStringExtra("tipoCliente")
        val nombre = intent.getStringExtra("nombre")
        val apellido = intent.getStringExtra("apellido")
        val dni = intent.getStringExtra("dni")
        val fotoBytes = intent.getByteArrayExtra("foto")

        edtNamePagar.setText(nombre)
        edtSurnamePagar.setText(apellido)
        edtDniPagar.setText(dni)
        edtTcliente.setText(tcliente)
        if (fotoBytes != null && fotoBytes.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.size)
            imgAvatar.setImageBitmap(bitmap)
        } else {
            imgAvatar.setImageResource(R.drawable.avatar)
        }

        btnCerrar.setOnClickListener {
            finish()
        }
        btnPagar.setOnClickListener {
            // Obtener el DNI que se pasará a la siguiente actividad
            val dniParaPago = edtDniPagar.text.toString()

            // Variable para el Intent que se creará
            val intentPago: Intent

            // Decidir qué Activity abrir basado en tipoCliente
            if (tcliente != null) {
                when (tcliente.trim().equals("Mensual", ignoreCase = true)) {
                    true -> {
                        intentPago = Intent(this, Pago::class.java)
                        // Toast.makeText(this, "Abriendo pantalla para socio Mensual", Toast.LENGTH_SHORT).show() // Opcional
                    }
                    false -> {
                        intentPago = Intent(this, Pago2::class.java)
                    }
                    // Opcional: Manejar un caso donde tipoCliente no sea ni "Mensual" ni "Diario"
                    // else -> {
                    //     Toast.makeText(this, "Tipo de cliente no reconocido: $tipoCliente", Toast.LENGTH_LONG).show()
                    //     return@setOnClickListener // No hacer nada si el tipo no es reconocido
                    // }
                }

                // Pasar el DNI a la actividad de pago seleccionada
                intentPago.putExtra("dni", dniParaPago)
                startActivity(intentPago)

            } else {
                // tipoCliente es null, manejar este caso (aunque según tu lógica, debería venir del Intent)
                Toast.makeText(this, "Error: Tipo de cliente no disponible.", Toast.LENGTH_LONG).show()
            }
        }
    }
}