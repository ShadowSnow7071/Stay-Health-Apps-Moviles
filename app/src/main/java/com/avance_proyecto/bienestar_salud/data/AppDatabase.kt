package com.avance_proyecto.bienestar_salud.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Usuario::class, RegistroDia::class, Tarea::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun registroDao(): RegistroDao
    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(contexto: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "stay_healthy.db"
                ).build().also { instancia = it }
            }
        }
    }
}
