package com.example.app_club_vanguardista


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.view.isVisible

class Pago : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        val spinnerMetodoPago = findViewById<Spinner>(R.id.spinner)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinner2)
        val lblCuotas = findViewById<TextView>(R.id.lblMontodepago4)
        val dni = intent.getStringExtra("dni")

        spinnerMetodoPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val metodoSeleccionado = parent.getItemAtPosition(position).toString()
                if (metodoSeleccionado.equals("Tarjeta", ignoreCase = true)) {
                    spinnerCuotas.visibility = View.VISIBLE
                    lblCuotas.visibility = View.VISIBLE
                } else {
                    spinnerCuotas.visibility = View.GONE
                    lblCuotas.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                spinnerCuotas.visibility = View.GONE
                lblCuotas.visibility = View.GONE
            }
        }

        val btnPagar = findViewById<Button>(R.id.btnPagar2)
        btnPagar.setOnClickListener {
            if (dni == null) {
                Toast.makeText(this, "Error: DNI no encontrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val metodoPago = spinnerMetodoPago.selectedItem.toString()
            val cuotas = if (spinnerCuotas.isVisible) {
                spinnerCuotas.selectedItem.toString()
            } else {
                null
            }




    }
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarpagar)
        btnCerrar.setOnClickListener {
            finish()
        }
}
}