package com.example.app_club_vanguardista

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.add
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.text.format
import java.text.SimpleDateFormat // Importar para formatear la fecha
import java.util.Calendar         // Importar Calendar
import java.util.Locale

// --- Asegúrate que estas data class estén definidas (pueden estar en otro archivo) ---
data class DatosSocioParaCarnet(
    val nombreCompleto: String,
    val dni: String,
    val tipoSocio: String,
    val fechaVencimiento: String, // Asegúrate de tener esta información
    val fotoBytes: ByteArray?,
    val idParaCodigoBarras: String? = null // Opcional
)

// Suponiendo que tienes una clase Socio similar a esta en tu UserDBHelper
// data class Socio(
// val id: Int,
// val nombre: String,
// val apellido: String,
// val dni: String,
// val telefono: String,
// val email: String,
// val domicilio: String,
// val tipoSocio: String, // Ejemplo: "Mensual", "Anual"
// val fechaAlta: String, // Ejemplo: "2023-01-15"
// val fechaVencimiento: String, // Ejemplo: "2024-01-15" (¡IMPORTANTE PARA EL CARNET!)
// val foto: ByteArray?
// )


class PagoExitoso : AppCompatActivity() {

    private lateinit var dbHelper: UserDBHelper // Necesitarás instanciar esto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_exitoso)

        dbHelper = UserDBHelper(this) // Instancia tu DBHelper

        val btnGenerarCarnet: Button = findViewById(R.id.btnGenerarCarnet)
        val dniRecibido = intent.getStringExtra("dni_pagado")




        btnGenerarCarnet.setOnClickListener {
            if (dniRecibido != null) {
                Toast.makeText(this, "Iniciando generación de carnet para DNI: $dniRecibido...", Toast.LENGTH_SHORT).show()

                // 1. Obtener los datos completos del socio usando el dniRecibido desde UserDBHelper
                val socio = dbHelper.buscarSocio(dniRecibido) // Asegúrate que este método exista en UserDBHelper y devuelva tu objeto Socio

                if (socio != null) {
                    val calendario = Calendar.getInstance() // Obtiene la fecha y hora actual
                    calendario.add(Calendar.DAY_OF_YEAR, 30) // Añade 30 días
                    val formatoFecha = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()) // Define el formato deseado
                    val fechaVencimientoCalculada = formatoFecha.format(calendario.time)
                    val datosCarnet = DatosSocioParaCarnet(
                        nombreCompleto = "${socio.nombre} ${socio.apellido}",
                        dni = socio.dni,
                        tipoSocio = socio.tipoCliente, // Asume que Socio tiene 'tipoSocio'
                        fechaVencimiento = fechaVencimientoCalculada, // ¡IMPORTANTE! Asume que Socio tiene 'fechaVencimiento'
                        fotoBytes = socio.foto // Asume que Socio tiene 'foto' como ByteArray?
                    )

                    // 3. Llamar a generarBitmapCarnet()
                    val bitmap = generarBitmapCarnet(this, datosCarnet)

                    if (bitmap != null) {
                        // 4. Llamar a generarPdfDesdeBitmap()
                        val nombreArchivo = "Carnet_${datosCarnet.dni.replace(".", "")}.pdf"
                        val archivoPdf = generarPdfDesdeBitmap(this, bitmap, nombreArchivo)

                        if (archivoPdf != null && archivoPdf.exists()) {
                            // 5. Mostrar mensaje o compartir/abrir el PDF
                            Toast.makeText(this, "Carnet PDF guardado en: ${archivoPdf.absolutePath}", Toast.LENGTH_LONG).show()
                            abrirOCompartirPdf(this, archivoPdf)
                        } else {
                            Toast.makeText(this, "Error al guardar el PDF del carnet.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Error al generar la imagen del carnet (bitmap).", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Socio no encontrado con DNI: $dniRecibido", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Error: No hay DNI disponible para generar el carnet.", Toast.LENGTH_LONG).show()
            }
        }

 /*       // Configuración del botón para volver (si lo tienes)
        val btnVolver: Button? = findViewById(R.id.btnVolverAlMenu) // Asume que tienes este ID
        btnVolver?.setOnClickListener {
            val intentMenu = Intent(this, MainMenu::class.java) // O a la actividad que desees
            intentMenu.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentMenu)
            finish()
        }*/
    }

    // --- Coloca aquí las funciones auxiliares que definimos antes ---

    private fun generarBitmapCarnet(context: Context, datosSocio: DatosSocioParaCarnet): Bitmap? {
        val inflater = LayoutInflater.from(context)
        val carnetView = inflater.inflate(R.layout.activity_carnet, null, false) // Usa el nombre de tu layout de carnet

        val imgFotoSocio: ImageView = carnetView.findViewById(R.id.imgFotoSocioEnCarnet)
        val tvNombreSocio: TextView = carnetView.findViewById(R.id.tvNombreSocioEnCarnet)
        val tvDniSocio: TextView = carnetView.findViewById(R.id.tvDniSocioEnCarnet)
        val tvTipoSocio: TextView = carnetView.findViewById(R.id.tvTipoSocioEnCarnet)
        val tvVencimiento: TextView = carnetView.findViewById(R.id.tvVencimientoSocioEnCarnet)
        // Añade otras vistas que tengas en tu layout de carnet (logo, nombre club, etc.)
        // val imgLogoClub: ImageView = carnetView.findViewById(R.id.imgLogoClubCarnet)
        // val tvNombreClub: TextView = carnetView.findViewById(R.id.tvNombreClubCarnet)

        // Poblar datos
        // tvNombreClub.text = "CLUB VANGUARDISTA" // O el nombre de tu club
        tvNombreSocio.text = "Nombre: ${datosSocio.nombreCompleto}"
        tvDniSocio.text = "DNI: ${datosSocio.dni}"
        tvTipoSocio.text = "Tipo: ${datosSocio.tipoSocio}"
        tvVencimiento.text = "Vence: ${datosSocio.fechaVencimiento}"

        if (datosSocio.fotoBytes != null && datosSocio.fotoBytes.isNotEmpty()) {
            val bitmapFoto = BitmapFactory.decodeByteArray(datosSocio.fotoBytes, 0, datosSocio.fotoBytes.size)
            imgFotoSocio.setImageBitmap(bitmapFoto)
        } else {
            imgFotoSocio.setImageResource(R.drawable.avatar) // Un placeholder por defecto
        }
        // imgLogoClub.setImageResource(R.drawable.logo_club_placeholder) // Tu logo

        // Medir y dibujar
        val widthPx = dpToPx(context, 350) // Ancho del carnet en dp
        val heightPx = dpToPx(context, 200) // Alto del carnet en dp

        carnetView.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
        )
        carnetView.layout(0, 0, carnetView.measuredWidth, carnetView.measuredHeight)

        val bitmap = Bitmap.createBitmap(carnetView.measuredWidth, carnetView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        carnetView.draw(canvas)
        return bitmap
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun generarPdfDesdeBitmap(context: Context, bitmapCarnet: Bitmap, nombreArchivo: String): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmapCarnet.width, bitmapCarnet.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        page.canvas.drawBitmap(bitmapCarnet, 0f, 0f, null)
        pdfDocument.finishPage(page)

        val directorioPdf: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (directorioPdf != null && !directorioPdf.exists()) {
            directorioPdf.mkdirs() // Crea el directorio si no existe
        } else if (directorioPdf == null) {
            Toast.makeText(context, "No se pudo acceder al directorio de documentos.", Toast.LENGTH_LONG).show()
            pdfDocument.close()
            return null
        }


        val archivoPdf = File(directorioPdf, nombreArchivo)

        try {
            FileOutputStream(archivoPdf).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            return archivoPdf
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error al escribir el archivo PDF: ${e.message}", Toast.LENGTH_LONG).show()
            pdfDocument.close()
            return null
        }
    }

    private fun abrirOCompartirPdf(context: Context, archivoPdf: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.applicationContext.packageName}.provider",
            archivoPdf
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Para compartir en lugar de solo abrir:
            // action = Intent.ACTION_SEND
            // putExtra(Intent.EXTRA_STREAM, uri)
            // type = "application/pdf" // Asegurar el tipo para el SEND
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Abrir/Compartir Carnet PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "No se encontró una aplicación para abrir PDF.", Toast.LENGTH_SHORT).show()
        }
    }
}