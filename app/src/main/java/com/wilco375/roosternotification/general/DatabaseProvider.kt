package com.wilco375.roosternotification.general

import android.content.Context
import android.database.sqlite.SQLiteDatabase

object DatabaseProvider {
    @Volatile private var initialized = false
    private lateinit var db: SQLiteDatabase

    @Synchronized fun getDatabase(context: Context): SQLiteDatabase {
        if (!initialized) {
            db = context.openOrCreateDatabase("Schedule", Context.MODE_PRIVATE, null)!!
            
            db.enableWriteAheadLogging()

            createTables()
            doMigrations()

            initialized = true
        }

        return db
    }


    private fun createTables() {
        val lessonTable = "CREATE TABLE IF NOT EXISTS Lesson (" +
                "   instance INTEGER," +
                "   subject TEXT," +
                "   lessonGroup TEXT," +
                "   location TEXT," +
                "   type TEXT," +
                "   cancelled INTEGER," +
                "   start INTEGER," +
                "   end INTEGER," +
                "   timeslot INTEGER," +
                "   day INTEGER," +
                "   username TEXT," +
                "   PRIMARY KEY (instance, username)" +
                ")"
        db.execSQL(lessonTable)

        val notificationTable = "CREATE TABLE IF NOT EXISTS Notification (" +
                "   instance INTEGER," +
                "   type TEXT," +
                "   PRIMARY KEY (instance, type)" +
                ")"
        db.execSQL(notificationTable)

        val namesTable = "CREATE TABLE IF NOT EXISTS Name (" +
                "   code TEXT PRIMARY KEY," +
                "   name TEXT" +
                ")"
        db.execSQL(namesTable)
    }

    private fun doMigrations() {
        val migrationsTable = "CREATE TABLE IF NOT EXISTS Migrations (" +
                "   version INTEGER PRIMARY KEY" +
                ")"
        db.execSQL(migrationsTable)

        val cursor = db.rawQuery("SELECT MAX(version) FROM Migrations", null)
        var lastMigration = 1
        if (!cursor.isClosed && cursor.moveToNext()) {
            lastMigration = cursor.getInt(0)
        }
        cursor.close()

        if (lastMigration < 2) {
            // Migration to version 2
            db.execSQL("ALTER TABLE Lesson ADD COLUMN teacher TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE Lesson ADD COLUMN teacherFull TEXT NOT NULL DEFAULT ''")
            db.execSQL("INSERT INTO Migrations VALUES (2)")
        }
    }

}