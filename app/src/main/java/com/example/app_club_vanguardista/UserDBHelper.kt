package com.example.app_club_vanguardista

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(context:Context): SQLiteOpenHelper(context,"ClubDepotivoDB",null,1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE Usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                contrasenia TEXT NOT NULL
            )

        """.trimIndent())
        db.execSQL("INSERT INTO usuarios(usuario,contrasenia) VALUES ('admin','1234') ")
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun login (nombre: String,contrasenia:String): Boolean{
        var db=readableDatabase
        var cursor = db.rawQuery(
            " SELECT * FROM usuarios WHERE usuario=? AND contrasenia=?",
            arrayOf(nombre,contrasenia)
        )

        var correctUser =cursor.count > 0
        return correctUser
    }

}