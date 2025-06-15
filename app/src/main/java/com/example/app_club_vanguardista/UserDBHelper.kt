package com.example.app_club_vanguardista

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.Serializable

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDepotivoDB", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                contrasenia TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE socios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL,
                tipoCliente TEXT NOT NULL,
                fechaAlta TEXT NOT NULL,
                aptoFisico INTEGER NOT NULL,
                foto BLOB
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE pagos(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dni TEXT NOT NULL,
                fechaPago TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("INSERT INTO usuarios(usuario, contrasenia) VALUES ('admin', '1234')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS pagos")
        onCreate(db)
    }

    // Login
    fun login(nombre: String, contrasenia: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE usuario=? AND contrasenia=?",
            arrayOf(nombre, contrasenia)
        )
        val correctUser = cursor.count > 0
        cursor.close()
        db.close()
        return correctUser
    }

    // Insertar socio
    fun insertarSocio(
        nombre: String,
        apellido: String,
        dni: String,
        tipoCliente: String,
        fechaAlta: String,
        aptoFisico: Boolean,
        foto: ByteArray?
    ): Boolean {
        val db = writableDatabase
        return try {
            val valores = ContentValues().apply {
                put("nombre", nombre)
                put("apellido", apellido)
                put("dni", dni)
                put("tipoCliente", tipoCliente)
                put("fechaAlta", fechaAlta)
                put("aptoFisico", if (aptoFisico) 1 else 0)
                put("foto", foto)
            }
            val resultado = db.insert("socios", null, valores)
            resultado != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Verificar si ya pagó en el mes (formato mesAnio = "yyyy-MM")
    fun yaPagoEsteMes(dni: String, mesAnio: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM pagos WHERE dni = ? AND substr(fechaPago, 1, 7) = ?"
        val cursor = db.rawQuery(query, arrayOf(dni, mesAnio))
        var pagado = false
        if (cursor.moveToFirst()) {
            val count = cursor.getInt(0)
            pagado = count > 0
        }
        cursor.close()
        db.close()
        return pagado
    }

    // Data class Socio
    data class Socio(
        val nombre: String,
        val apellido: String,
        val dni: String,
        val tipoCliente: String,
        val fechaAlta: String,
        val aptoFisico: Boolean,
        val foto: ByteArray?
    ) : Serializable

    // Función auxiliar para extraer un socio de un cursor
    private fun extraerSocioDeCursor(cursor: android.database.Cursor): Socio {
        return Socio(
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
            apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
            dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
            tipoCliente = cursor.getString(cursor.getColumnIndexOrThrow("tipoCliente")),
            fechaAlta = cursor.getString(cursor.getColumnIndexOrThrow("fechaAlta")),
            aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1,
            foto = cursor.getBlob(cursor.getColumnIndexOrThrow("foto"))
        )
    }



    fun buscarSocio(nombre: String, apellido: String, dni: String): Socio? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM socios WHERE nombre=? AND apellido=? AND dni=?",
            arrayOf(nombre, apellido, dni)
        )
        val socio: Socio? = if (cursor.moveToFirst()) {
            extraerSocioDeCursor(cursor)
        } else {
            null
        }
        cursor.close()
        db.close()
        return socio
    }
}
