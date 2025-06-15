package com.example.app_club_vanguardista

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream

class Alta3 : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper
    private lateinit var imageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var imagenSeleccionada: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alta3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = UserDBHelper(this)
        imageView = findViewById(R.id.imageView)

        val btnCargar = findViewById<Button>(R.id.btncargar)
        val btnCamara = findViewById<ImageView>(R.id.imageView)
        val btnCerrar = findViewById<ImageButton>(R.id.btn_cerrarX_alta)

        // Registrar launcher para elegir una imagen de la galería
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageUri = result.data?.data
                imageView.setImageURI(imageUri)
                imagenSeleccionada = true
            }
        }

        // Al tocar la imagen se abre la galería de fotos
        btnCamara.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        btnCargar.setOnClickListener {
            val nombre = findViewById<EditText>(R.id.nombre_alta).text.toString()
            val apellido = findViewById<EditText>(R.id.apellido_alta).text.toString()
            val dni = findViewById<EditText>(R.id.DNI_alta).text.toString()
            val tipoCliente = findViewById<EditText>(R.id.tipodecliente_alta).text.toString()
            val fechaAlta = findViewById<EditText>(R.id.fecha_alta).text.toString()
            val aptoFisico = findViewById<CheckBox>(R.id.CheckBox_alta).isChecked
            val foto = obtenerImagenComoByteArray()

            if (nombre.isBlank() || apellido.isBlank() || dni.isBlank() || tipoCliente.isBlank() || fechaAlta.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val exito = dbHelper.insertarSocio(
                nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto
            )

            if (exito) {
                Toast.makeText(this, "Socio guardado con éxito", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AltaExitosa::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el socio", Toast.LENGTH_SHORT).show()
            }
        }

        btnCerrar.setOnClickListener {
            startActivity(Intent(this, MainMenu::class.java))
            finish()
        }
    }

    // Convierte la imagen mostrada en el ImageView a un ByteArray para guardarla
    private fun obtenerImagenComoByteArray(): ByteArray? {
        if (!imagenSeleccionada) return null
        val drawable = imageView.drawable as? BitmapDrawable ?: return null
        val bitmap = drawable.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}