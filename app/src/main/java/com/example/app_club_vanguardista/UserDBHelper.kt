package com.example.app_club_vanguardista

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.util.Log
import androidx.core.database.getBlobOrNull
import java.io.Serializable
import kotlin.math.log

class UserDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubDepotivoDB", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE Usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                contrasenia TEXT NOT NULL   )
            
        """.trimIndent()
        )

        db.execSQL(
            """
           
            CREATE TABLE socios(
               
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE, -- Hacemos el DNI ÃšNICO, es mÃ¡s comÃºn
                tipoCliente TEXT CHECK(tipoCliente IN ('Mensual', 'Diario')) NOT NULL, -- RESTRICCIÃ“N AQUÃ
                fechaAlta DATE NOT NULL, -- Usamos DATE para mejor manejo de fechas
                aptoFisico INTEGER NOT NULL,
                foto BLOB
            )

            
        """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE pagos(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dni TEXT NOT NULL,
                fechaPago TEXT NOT NULL
            )
        """.trimIndent()
        )
        // Corrected version within UserDBHelper.kt
         db.execSQL(
            """
                CREATE TABLE DeportesTarifas (
                                              id INTEGER PRIMARY KEY AUTOINCREMENT, -- CORRECTED
                                              nombre_deporte TEXT NOT NULL UNIQUE,    -- VARCHAR(100) becomes TEXT
                                              tarifa_mensual INTEGER NOT NULL      -- INT is fine, or INTEGER
         )
         """.trimIndent()
           )
        /// DENTRO DE TU UserDBHelper.kt, en el método onCreate(db: SQLiteDatabase)

// Insertar datos de ejemplo para socios (SIN TILDES)
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Ana', 'Perez', '30123456', 'Mensual', '2023-01-15', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Luis', 'Garcia', '32789012', 'Diario', '2022-11-20', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Carla', 'Martinez', '35456789', 'Mensual', '2023-03-10', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Jorge', 'Rodriguez', '28098765', 'Diario', '2021-07-01', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Sofia', 'Lopez', '38123123', 'Mensual', '2023-05-25', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Miguel', 'Sanchez', '31567890', 'Diario', '2022-09-05', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Elena', 'Fernandez', '36789456', 'Mensual', '2020-02-18', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Diego', 'Gomez', '29321654', 'Diario', '2023-08-30', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Laura', 'Diaz', '40159753', 'Mensual', '2023-06-12', 1, NULL);")
        db.execSQL("INSERT INTO socios (nombre, apellido, dni, tipoCliente, fechaAlta, aptoFisico, foto) VALUES ('Pablo', 'Ruiz', '33654987', 'Diario', '2022-12-01', 1, NULL);")

// Insertar datos de ejemplo para pagos
        db.execSQL("INSERT INTO pagos (dni, fechaPago) VALUES ('32789012', '2025-04-20');") // Luis Garcia
        db.execSQL("INSERT INTO pagos (dni, fechaPago) VALUES ('35456789', '2025-05-01');") // Carla Martinez
        db.execSQL("INSERT INTO pagos (dni, fechaPago) VALUES ('28098765', '2025-05-19');") // Jorge Rodriguez
        db.execSQL("INSERT INTO pagos (dni, fechaPago) VALUES ('38123123', '2025-06-01');") // Sofia Lopez
        db.execSQL("INSERT INTO pagos (dni, fechaPago) VALUES ('31567890', '2025-06-10');") // Miguel Sanchez
        db.execSQL("INSERT INTO pagos (dni, fechaPago) VALUES ('36789456', '2025-04-30');") // Elena Fernandez
        db.execSQL("INSERT INTO pagos (dni, fechaPago) VALUES ('29321654', '2025-05-19');") // Diego Gomez



        // --- INICIO: INSERT DATOS DEPORTES Y TARIFAS (Nombres directos) ---
        db.execSQL("INSERT INTO DeportesTarifas (nombre_deporte, tarifa_mensual) VALUES ('Futbol', 500);")
        db.execSQL("INSERT INTO DeportesTarifas (nombre_deporte, tarifa_mensual) VALUES ('Padel', 300);")
        db.execSQL("INSERT INTO DeportesTarifas (nombre_deporte, tarifa_mensual) VALUES ('Tenis', 1200);")
        db.execSQL("INSERT INTO DeportesTarifas (nombre_deporte, tarifa_mensual) VALUES ('Basquet', 450);")
        db.execSQL("INSERT INTO DeportesTarifas (nombre_deporte, tarifa_mensual) VALUES ('Jokey', 600);")
        db.execSQL("INSERT INTO DeportesTarifas (nombre_deporte, tarifa_mensual) VALUES ('Rugby', 900);")
        // --- FIN: INSERT DATOS DEPORTES Y TARIFAS ---

        db.execSQL("INSERT INTO usuarios(usuario, contrasenia) VALUES ('admin', '1234')")

    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS pagos")
        db.execSQL("DROP TABLE IF EXISTS DeportesTarifas")
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


    data class Socio(
        val id: Int, //
        val nombre: String,
        val apellido: String,
        val dni: String,
        val tipoCliente: String,
        val fechaAlta: String,
        val aptoFisico: Boolean,
        val foto: ByteArray?
    ) : Serializable

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
    // Función auxiliar para extraer un socio de un cursor
    private fun extraerSocioDeCursor(cursor: Cursor): Socio {
        return Socio(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
            apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
            dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
            tipoCliente = cursor.getString(cursor.getColumnIndexOrThrow("tipoCliente")),
            fechaAlta = cursor.getString(cursor.getColumnIndexOrThrow("fechaAlta")),
            aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1,
            foto = cursor.getBlob(cursor.getColumnIndexOrThrow("foto"))

        )
    }

    fun buscarSocio(dni: String): Socio? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM socios WHERE dni=?",
            arrayOf(dni)
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


    fun insertarPago(dniSocio: String, fechaPago: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("dni", dniSocio)         // Nombre de la columna DNI en tu tabla 'pagos'
            put("fechaPago", fechaPago) // Nombre de la columna fechaPago en tu tabla 'pagos'
        }

        return try {
            val resultado = db.insert("pagos", null, values) // Nombre de tu tabla de pagos
            resultado != -1L // Retorna true si la inserción fue exitosa, false si no
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }

    }
    data class DeporteConTarifa(val nombre: String, val tarifa: Int)

    // 1. Función para obtener todos los deportes y sus tarifas
    fun getAllDeportesConTarifas(): List<DeporteConTarifa> {
        val deportesList = mutableListOf<DeporteConTarifa>()
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT nombre_deporte, tarifa_mensual FROM DeportesTarifas ORDER BY nombre_deporte ASC", null)
            if (cursor.moveToFirst()) {
                do {
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre_deporte"))
                    val tarifa = cursor.getInt(cursor.getColumnIndexOrThrow("tarifa_mensual"))
                    deportesList.add(DeporteConTarifa(nombre, tarifa))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace() // Maneja la excepción adecuadamente
        } finally {
            cursor?.close()
            db.close() // Considera si cerrar la BD aquí o gestionarla a nivel de Activity/ViewModel
        }
        return deportesList
    }

    fun getTarifaDeporte(nombreDeporte: String): Int? {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var tarifa: Int? = null
        try {
            cursor = db.rawQuery(
                "SELECT tarifa_mensual FROM DeportesTarifas WHERE nombre_deporte = ?",
                arrayOf(nombreDeporte)
            )
            if (cursor.moveToFirst()) {
                tarifa = cursor.getInt(cursor.getColumnIndexOrThrow("tarifa_mensual"))
            }
        } catch (e: Exception) {
            e.printStackTrace() // Maneja la excepción adecuadamente
        } finally {
            cursor?.close()
            db.close() // Considera si cerrar la BD aquí o gestionarla a nivel de Activity/ViewModel
        }
        return tarifa
    }

    fun getVencimientosDelDia(): List<Socio> {
            val sociosVencHoy = mutableListOf<Socio>()

            val db = readableDatabase
            val query = """SELECT s.* FROM socios s
                             LEFT JOIN (
                                SELECT dni, MAX(fechaPago) AS ultimaFecha
                                FROM pagos
                                GROUP BY dni
                            ) p ON s.dni = p.dni
                            WHERE s.tipoCliente != 'Diario'
                        """.trimIndent()

            val cursor = db.rawQuery(query, null)

            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val calendar = java.util.Calendar.getInstance()
            val hoy = dateFormat.format(java.util.Date())

            if (cursor.moveToFirst()) {
                do {
                    val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                    val ultimaFechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fechaAlta")) // placeholder

                    // Buscamos la fecha de pago desde una subconsulta separada por dni
                    val subCursor = db.rawQuery(
                        "SELECT MAX(fechaPago) FROM pagos WHERE dni = ?",
                        arrayOf(dni)
                    )

                    var fechaPago: String? = null
                    if (subCursor.moveToFirst()) {
                        fechaPago = subCursor.getString(0)
                    }
                    subCursor.close()

                    if (fechaPago != null) {
                        try {
                            val fecha = dateFormat.parse(fechaPago)
                            calendar.time = fecha
                            calendar.add(Calendar.MONTH, 1)
                            val vencimiento = dateFormat.format(calendar.time)

                            Log.d("Fechas::", vencimiento.toString() + " and " + hoy.toString())

                            if (vencimiento == hoy) {
                                // Mapear los campos del socio
                                val socio = Socio(
                                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                                    dni = dni,
                                    tipoCliente = cursor.getString(cursor.getColumnIndexOrThrow("tipoCliente")),
                                    fechaAlta = cursor.getString(cursor.getColumnIndexOrThrow("fechaAlta")),
                                    aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1,
                                    foto = cursor.getBlobOrNull(cursor.getColumnIndexOrThrow("foto"))
                                )
                                sociosVencHoy.add(socio)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.d("DEBUG:: Error", e.message.toString())
                        }
                    }

                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()

            return sociosVencHoy
    }

    fun getListaDeudores(): List<Socio>{
        val sociosVencidos = mutableListOf<Socio>()

        val db = readableDatabase
        val query = """
                    SELECT s.*
                    FROM socios s
                    LEFT JOIN (
                        SELECT dni, MAX(fechaPago) AS ultimaFecha
                        FROM pagos
                        GROUP BY dni
                    ) p ON s.dni = p.dni
                    WHERE s.tipoCliente != 'Diario'
                """.trimIndent()

            val cursor = db.rawQuery(query, null)

            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val calendar = java.util.Calendar.getInstance()
            val hoy = dateFormat.parse(dateFormat.format(java.util.Date()))!!

            if (cursor.moveToFirst()) {
                do {
                    val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))

                    // Buscar la última fecha de pago
                    val subCursor = db.rawQuery(
                        "SELECT MAX(fechaPago) FROM pagos WHERE dni = ?",
                        arrayOf(dni)
                    )

                    var fechaPago: String? = null
                    if (subCursor.moveToFirst()) {
                        fechaPago = subCursor.getString(0)
                    }
                    subCursor.close()

                    if (fechaPago != null) {
                        try {
                            val fecha = dateFormat.parse(fechaPago)
                            calendar.time = fecha
                            calendar.add(Calendar.MONTH, 1)
                            val vencimiento = calendar.time

                            if (vencimiento.before(hoy)) {
                                // Mapear socio vencido
                                val socio = Socio(
                                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                                    dni = dni,
                                    tipoCliente = cursor.getString(cursor.getColumnIndexOrThrow("tipoCliente")),
                                    fechaAlta = cursor.getString(cursor.getColumnIndexOrThrow("fechaAlta")),
                                    aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1,
                                    foto = cursor.getBlobOrNull(cursor.getColumnIndexOrThrow("foto"))
                                )
                                sociosVencidos.add(socio)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }else{
                        val socio = Socio(
                            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                            apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                            dni = dni,
                            tipoCliente = cursor.getString(cursor.getColumnIndexOrThrow("tipoCliente")),
                            fechaAlta = cursor.getString(cursor.getColumnIndexOrThrow("fechaAlta")),
                            aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1,
                            foto = cursor.getBlobOrNull(cursor.getColumnIndexOrThrow("foto"))
                        )
                        sociosVencidos.add(socio)
                    }

                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()

            return sociosVencidos
        }

    companion object

}

