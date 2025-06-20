package com.example.app_club_vanguardista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Listas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_listas)
        val btnOut = findViewById<ImageButton>(R.id.btnCerrarListax7)
        btnOut.setOnClickListener {
            val intent = Intent(this,ListasMenu::class.java)
            startActivity(intent)
            finish()}
        val LIST_TO_SHOW = "LIST_TO_SHOW"
        val listToShow = intent.getStringExtra(LIST_TO_SHOW)
        var socios = listOf<UserDBHelper.Socio>()
        val myDBHelper = UserDBHelper(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (listToShow == "listaVencimientos") {
            socios = myDBHelper.getVencimientosDelDia()
        }
        if (listToShow == "listaDeudores") {
            socios = myDBHelper.getListaDeudores()
        }

        Log.d("DEBUG", "Cantidad de socios: ${socios.size}")
        for (socio in socios) {
            Log.d("DEBUG", "Socio: ${socio.nombre} ${socio.apellido}")
        }

        recyclerView.adapter = SocioAdapter(socios)



    }
}

