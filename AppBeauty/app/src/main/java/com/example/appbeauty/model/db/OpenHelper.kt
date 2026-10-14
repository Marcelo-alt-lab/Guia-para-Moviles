package com.example.appbeauty.model.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.appbeauty.model.beans.Post

class OpenHelper(context: Context) : SQLiteOpenHelper(
    context, "BeautyDB.db", null, 1
) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val query = "create table posts(id text primary key, nombre text, marca text, precio float, categoria text, tipo text, descripcion text, foto text)"
        p0!!.execSQL(query)
    }

    override fun onUpgrade(
        p0: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {
        val query = "drop table if exists posts"
        p0!!.execSQL(query)
        onCreate(p0)
    }

    // Guardar favorito
    fun newPost(post: Post) {
        val datos = ContentValues()
        datos.put("id", post.id)
        datos.put("nombre", post.nombre)
        datos.put("marca", post.marca)
        datos.put("precio", post.precio)
        datos.put("categoria", post.categoria)
        datos.put("tipo", post.tipo)
        datos.put("descripcion", post.descripcion)
        datos.put("foto", post.foto)
        val db = this.writableDatabase
        db.insertWithOnConflict("posts", null, datos, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()

    }

    // Leer Favoritos
    fun leerData(): MutableList<Post> {
        val db = this.readableDatabase
        val query = "select * from posts"
        val result = db.rawQuery(query, null)
        val listaP = mutableListOf<Post>()

        if (result.moveToFirst()) {
            do {
                listaP.add(
                    Post(
                        result.getString(0),
                        result.getString(1),
                        result.getString(2),
                        result.getDouble(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7),
                    )
                )
            } while (result.moveToNext())
        }
        result.close()
        db.close()
        return listaP
    }

    // Eliminar de la tabla por su Id
    fun eliminarPost(id: String): Int{
        val db =  this.writableDatabase

        val filasEliminadas = db.delete("posts", "id = ?", arrayOf(id))
        db.close()
        return filasEliminadas
    }

    // Editar el Precio
    fun actualizarPost(post: Post): Int {
        val db = this.writableDatabase
        val datos = ContentValues()
        datos.put("precio", post.precio )

        val filasAfectadas = db.update("posts", datos, "id = ?", arrayOf(post.id))
        db.close()
        return filasAfectadas
    }


}