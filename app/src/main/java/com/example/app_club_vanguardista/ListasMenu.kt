package com.example.app_club_vanguardista



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity




class ListasMenu : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listas_menu)

        val LIST_TO_SHOW = "LIST_TO_SHOW"
        val btnVencimientos = findViewById<Button>(R.id.btnAlta)
        val btnDeudores = findViewById<Button>(R.id.btnPagos)
        val btnBack = findViewById<Button>(R.id.btnOut)
        val btnCerrar = findViewById<ImageButton>(R.id.btncerrarx5)


        // Asumiendo que R.id.btnPagos es el ID correcto

        btnVencimientos.setOnClickListener {
            val intent = Intent(this, Listas::class.java)
            intent.putExtra(LIST_TO_SHOW,"listaVencimientos")
            startActivity(intent)
        }

        btnDeudores.setOnClickListener {
            val intent = Intent(this, Listas::class.java)
            intent.putExtra(LIST_TO_SHOW,"listaDeudores")
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }

        btnCerrar.setOnClickListener {
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
        }
    }}