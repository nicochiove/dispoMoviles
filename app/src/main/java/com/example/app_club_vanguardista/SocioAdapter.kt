package com.example.app_club_vanguardista

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class SocioAdapter(private val socios: List<UserDBHelper.Socio>) :
    RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    class SocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImageView: ImageView = view.findViewById(R.id.avatarImageView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_card, parent, false)
        return SocioViewHolder(view)
    }

    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = socios[position]
        holder.nameTextView.text = "${socio.nombre} ${socio.apellido}"

        if (socio.foto != null) {
            val bitmap = BitmapFactory.decodeByteArray(socio.foto, 0, socio.foto.size)
            holder.avatarImageView.setImageBitmap(bitmap)
        } else {
            holder.avatarImageView.setImageResource(R.drawable.avatar)
        }
    }

    override fun getItemCount() = socios.size
}
