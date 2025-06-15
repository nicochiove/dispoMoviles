package com.example.app_club_vanguardista

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SocioEncontradoActivity : AppCompatActivity() {

    private lateinit var edtNamePagar: EditText
    private lateinit var edtSurnamePagar: EditText
    private lateinit var edtDniPagar: EditText
    private lateinit var imgAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.socio_encontrado)

        edtNamePagar = findViewById(R.id.edtNamePagar)
        edtSurnamePagar = findViewById(R.id.edtSurnamepagar)
        edtDniPagar = findViewById(R.id.edtDnipagar)
        imgAvatar = findViewById(R.id.imgAvatar)
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarencontrado)
        val btnPagar = findViewById<Button>(R.id.btnPagar)

        val nombre = intent.getStringExtra("nombre")
        val apellido = intent.getStringExtra("apellido")
        val dni = intent.getStringExtra("dni")
        val fotoBytes = intent.getByteArrayExtra("foto")

        edtNamePagar.setText(nombre)
        edtSurnamePagar.setText(apellido)
        edtDniPagar.setText(dni)

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
            val intent = Intent(this, Pago::class.java)
            intent.putExtra("dni", edtDniPagar.text.toString())
            startActivity(intent)
        }
    }
}