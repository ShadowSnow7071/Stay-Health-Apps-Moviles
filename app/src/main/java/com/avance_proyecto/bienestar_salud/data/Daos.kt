package com.avance_proyecto.bienestar_salud.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :nombre COLLATE NOCASE LIMIT 1")
    suspend fun buscarPorNombre(nombre: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: Long): Usuario?

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminar(id: Long)
}

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registros_dia WHERE usuarioId = :usuarioId AND fecha = :fecha")
    fun observar(usuarioId: Long, fecha: String): Flow<RegistroDia?>

    @Query("SELECT * FROM registros_dia WHERE usuarioId = :usuarioId AND fecha BETWEEN :desde AND :hasta ORDER BY fecha")
    fun observarRango(usuarioId: Long, desde: String, hasta: String): Flow<List<RegistroDia>>

    @Query("SELECT * FROM registros_dia WHERE usuarioId = :usuarioId AND fecha = :fecha")
    suspend fun obtener(usuarioId: Long, fecha: String): RegistroDia?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: RegistroDia)
}

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas WHERE usuarioId = :usuarioId ORDER BY fecha, hora")
    fun observar(usuarioId: Long): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id AND usuarioId = :usuarioId")
    suspend fun obtener(id: Long, usuarioId: Long): Tarea?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(tarea: Tarea): Long

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)
}
