package com.example.app_club_vanguardista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast // Aunque ya no cargamos de BD, puede ser útil para otros mensajes
import androidx.appcompat.app.AppCompatActivity

data class DeporteLocalConTarifa(val nombre: String, val tarifa: Int)

class Pago2 : AppCompatActivity() {

    // private lateinit var dbHelper: UserDBHelper // Ya no es necesario para esto
    private lateinit var spinnerDeportes: Spinner
    private lateinit var editTextMonto: EditText

    // Lista local de deportes con sus tarifas
    // Define tus deportes y precios aquí directamente
    private val listaLocalDeportes: List<DeporteLocalConTarifa> = listOf(
        DeporteLocalConTarifa("Fútbol", 50000),
        DeporteLocalConTarifa("Baloncesto", 55000),
        DeporteLocalConTarifa("Tenis", 60000),
        DeporteLocalConTarifa("Natación", 65000),
        DeporteLocalConTarifa("Voleibol", 45000),
        DeporteLocalConTarifa("Padel", 30000), // Ejemplo adicional
        DeporteLocalConTarifa("Jockey", 52000)  // Ejemplo adicional
        // Añade más deportes según necesites
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago2) // Asegúrate que este sea el layout correcto

        // dbHelper = UserDBHelper(this) // Ya no es necesario inicializar dbHelper para esto
        spinnerDeportes = findViewById(R.id.spinnerdeporte) // USA EL ID CORRECTO DE TU SPINNER
        editTextMonto = findViewById(R.id.montos)         // USA EL ID CORRECTO DE TU EDITTEXT

        cargarDeportesLocalesEnSpinner()

        spinnerDeportes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0 && position < listaLocalDeportes.size) {
                    val tarifaSeleccionada = listaLocalDeportes[position].tarifa
                    editTextMonto.setText(tarifaSeleccionada.toString())
                } else {
                    editTextMonto.setText("") // Limpiar si la selección no es válida
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                editTextMonto.setText("") // Limpiar si no se selecciona nada
            }
        }
    }

    private fun cargarDeportesLocalesEnSpinner() {
        if (listaLocalDeportes.isEmpty()) {
            Toast.makeText(this, "No hay deportes definidos localmente.", Toast.LENGTH_LONG).show()
            spinnerDeportes.adapter = null
            editTextMonto.setText("0")
            return
        }

        // Extraer solo los nombres de los deportes para el ArrayAdapter del Spinner
        val nombresDeportes = listaLocalDeportes.map { it.nombre }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresDeportes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDeportes.adapter = adapter
        val btnPagar = findViewById<Button>(R.id.btnPagar3)
        var okintent2: Intent
        btnPagar.setOnClickListener {
            okintent2 = Intent(this, PagoExitoso::class.java)
            startActivity(okintent2)
        }

    }
}
