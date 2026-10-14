package com.example.postmanager.model.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.postmanager.model.beans.Post

class OpenHelper(context: Context) : SQLiteOpenHelper(
    context, "PostDB", null, 1
){
    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            CREATE TABLE posts(
                userId INT,
                id INT PRIMARY KEY,
                title TEXT,
                body TEXT
            )
        """.trimIndent()
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS posts")
        onCreate(db)
    }

    fun newPost(post: Post){
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("userId", post.userId)
            put("id", post.id)
            put("title", post.title)
            put("body", post.body)
        }

        db.insertWithOnConflict("posts", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun readAll(): MutableList<Post>{
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM posts", null)
        val list = mutableListOf<Post>()

        if (cursor.moveToFirst()){
            do {
                list.add(
                    Post(
                        userId = cursor.getInt(0),
                        id = cursor.getInt(1),
                        title = cursor.getString(2),
                        body = cursor.getString(3)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun deletePost(id: Int): Int {
        val db = this.writableDatabase
        val rows = db.delete("posts", "id = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }

    fun updateItem(post: Post): Int{
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", post.title)
            put("body", post.body)
        }
        val rows = db.update("posts", values, "id = ?", arrayOf(post.id.toString()))
        db.close()
        return rows
    }




}