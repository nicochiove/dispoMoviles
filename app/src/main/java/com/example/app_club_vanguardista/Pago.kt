package com.example.app_club_vanguardista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Pago : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val spinnerMetodoPago = findViewById<Spinner>(R.id.spinner)
        val spinnerCuotas = findViewById<Spinner>(R.id.spinner2)
        val lblCuotas = findViewById<TextView>(R.id.lblMontodepago4)


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
        val btnCerrar = findViewById<ImageButton>(R.id.btnCerrarpagar)
        btnCerrar.setOnClickListener {
            finish()
        }
    }
}