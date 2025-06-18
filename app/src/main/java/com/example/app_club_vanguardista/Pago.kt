package com.example.app_club_vanguardista


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.view.isVisible
private lateinit var dbHelper: UserDBHelper
class Pago : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)
        val spinnerMetodoPago = findViewById<Spinner>(R.id.spinner)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinner2)
        val lblCuotas = findViewById<TextView>(R.id.lblMontodepago4)
        val dni = intent.getStringExtra("dni")
        val monto=findViewById<EditText>(R.id.edtUsuario5)
        monto.setText("50000")

        dbHelper = UserDBHelper(this)



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
            val okintent: Intent

            val exitoInsercion = dbHelper.insertarPago(dni, fechaActual)
            if (exitoInsercion) {
                Toast.makeText(this, "Pago registrado exitosamente para DNI: $dni", Toast.LENGTH_LONG).show()
                // Aquí podrías, por ejemplo, finalizar esta actividad o limpiar campos
                // finish()
                okintent = Intent(this, PagoExitoso::class.java)
                startActivity(okintent)
            } else {
                Toast.makeText(this, "Error al registrar el pago. Verifique Logcat.", Toast.LENGTH_LONG).show()
                // Revisa Logcat para ver si e.printStackTrace() en UserDBHelper imprimió algo.
            }


    }
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarpagar)
        btnCerrar.setOnClickListener {
            finish()
        }
}
}