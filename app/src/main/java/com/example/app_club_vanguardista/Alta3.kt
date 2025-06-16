package com.example.app_club_vanguardista

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Alta3 : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper
    private lateinit var imageView: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var imagenSeleccionada: Boolean = false

    // Referencias a los EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDni: EditText
    private lateinit var etTipoCliente: EditText
    private lateinit var etFechaAlta: EditText // Este es el EditText para la fecha
    private lateinit var cbAptoFisico: CheckBox

    // Para almacenar la fecha seleccionada de forma mÃ¡s estructurada (opcional pero bueno)
    private var calendarioFechaAltaSeleccionada: Calendar? = null

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

        // Inicializar vistas
        imageView = findViewById(R.id.imageView)
        etNombre = findViewById(R.id.nombre_alta)
        etApellido = findViewById(R.id.apellido_alta)
        etDni = findViewById(R.id.DNI_alta)
        etTipoCliente = findViewById(R.id.tipodecliente_alta)
        etFechaAlta = findViewById(R.id.fecha_alta) // AsegÃºrate que el ID sea correcto
        cbAptoFisico = findViewById(R.id.CheckBox_alta)

        val btnCargar = findViewById<Button>(R.id.btncargar)
        val btnCamara = findViewById<ImageView>(R.id.imageView) // Asumo que es un ImageView que actÃºa como botÃ³n
        val btnCerrar = findViewById<ImageButton>(R.id.btn_cerrarX_alta)

        // Configurar DatePickerDialog para etFechaAlta
        etFechaAlta.isFocusable = false
        etFechaAlta.isClickable = true
        etFechaAlta.setOnClickListener {
            mostrarDialogoFechaAlta()
        }

        // Registrar launcher para elegir una imagen de la galerÃ­a
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageUri = result.data?.data
                imageView.setImageURI(imageUri)
                imagenSeleccionada = true
            }
        }

        // Al tocar la imagen se abre la galerÃ­a de fotos
        btnCamara.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        btnCargar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val tipoClienteTexto = etTipoCliente.text.toString().trim()
            val foto = obtenerImagenComoByteArray()
            cbAptoFisico = findViewById(R.id.CheckBox_alta)
            // Validar campos obligatorios bÃ¡sicos
            if (nombre.isBlank() || apellido.isBlank() || dni.isBlank() || tipoClienteTexto.isBlank()) {
                Toast.makeText(this, "Por favor completa nombre, apellido, DNI y tipo de cliente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar tipo de cliente
            if (tipoClienteTexto != "Mensual" && tipoClienteTexto != "Diario") {
                Toast.makeText(this, "Tipo de cliente debe ser 'Mensual' o 'Diario'.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // *** NUEVA VALIDACIÓN PARA APTO FÍSICO ***
            if (!cbAptoFisico.isChecked) {
                Toast.makeText(this, "El socio debe tener el apto físico presentado.", Toast.LENGTH_LONG).show()
                return@setOnClickListener // Detiene la ejecución si no está marcado
            }
            // Si llegamos aquí, el apto físico está chequeado
            val aptoFisico = cbAptoFisico.isChecked


            // Validar y obtener la fecha de alta
            val fechaAltaString: String
            if (calendarioFechaAltaSeleccionada != null) {
                // Formatear la fecha seleccionada del Calendar al formato YYYY-MM-DD
                val formatoDB = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                fechaAltaString = formatoDB.format(calendarioFechaAltaSeleccionada!!.time)
            } else {
                // Si el usuario no seleccionÃ³ una fecha usando el diÃ¡logo
                Toast.makeText(this, "Por favor selecciona la fecha de alta.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insertar en la base de datos
            val exito = dbHelper.insertarSocio(
                nombre, apellido, dni, tipoClienteTexto, fechaAltaString, aptoFisico, foto
            )

            if (exito) {
                Toast.makeText(this, "Socio guardado con Ã©xito", Toast.LENGTH_SHORT).show()
                // Limpiar campos o navegar a otra actividad
                limpiarCampos() // Ejemplo de funciÃ³n para limpiar
                startActivity(Intent(this, AltaExitosa::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error al guardar el socio", Toast.LENGTH_SHORT).show()
            }
        }

        btnCerrar.setOnClickListener {
            // startActivity(Intent(this, MainMenu::class.java))
            finish() // Simplemente cierra esta actividad
        }
    }

    private fun mostrarDialogoFechaAlta() {
        val calendarioActual = Calendar.getInstance()
        val anio = calendarioActual.get(Calendar.YEAR)
        val mes = calendarioActual.get(Calendar.MONTH)
        val dia = calendarioActual.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearSeleccionado, monthSeleccionado, dayOfMonthSeleccionado ->
                // Guardar la fecha seleccionada en nuestra variable Calendar
                calendarioFechaAltaSeleccionada = Calendar.getInstance().apply {
                    set(yearSeleccionado, monthSeleccionado, dayOfMonthSeleccionado)
                }
                // Formatear para mostrar en el EditText (formato legible para el usuario)
                val formatoVista = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etFechaAlta.setText(formatoVista.format(calendarioFechaAltaSeleccionada!!.time))
            },
            anio,
            mes,
            dia
        )
        // Opcional: Limitar fechas si es necesario (ej. no permitir fechas futuras)
        // datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun limpiarCampos() {
        etNombre.text.clear()
        etApellido.text.clear()
        etDni.text.clear()
        etTipoCliente.text.clear()
        etFechaAlta.text.clear() // Limpia el texto del EditText
        calendarioFechaAltaSeleccionada = null // Resetea la fecha seleccionada
        cbAptoFisico.isChecked = false
        imageView.setImageResource(R.drawable.camara_mini) // Reemplaza con tu imagen placeholder
        imagenSeleccionada = false
    }

    private fun obtenerImagenComoByteArray(): ByteArray? {
        if (!imagenSeleccionada) return null
        val drawable = imageView.drawable as? BitmapDrawable ?: return null
        val bitmap = drawable.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}