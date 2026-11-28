package com.avance_proyecto.bienestar_salud.recordatorios

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.avance_proyecto.bienestar_salud.MainActivity
import java.util.Calendar
import java.util.concurrent.TimeUnit

object Recordatorios {
    const val CANAL_ID = "recordatorios_bienestar"
    private const val TRABAJO_AGUA = "recordatorio_agua"
    private const val TRABAJO_PAUSAS = "recordatorio_pausas"
    private const val HORAS_ENTRE_PAUSAS = 2L

    fun crearCanal(contexto: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(
            CANAL_ID,
            "Recordatorios de bienestar",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Avisos para tomar agua y hacer pausas activas" }
        contexto.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun tienePermiso(contexto: Context): Boolean {
        if (Build.VERSION.SDK_INT < 33) return true
        return ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun programarAgua(contexto: Context, horas: Int) = programar(contexto, TRABAJO_AGUA, "agua", horas.toLong())

    fun programarPausas(contexto: Context) = programar(contexto, TRABAJO_PAUSAS, "pausas", HORAS_ENTRE_PAUSAS)

    fun cancelarAgua(contexto: Context) { WorkManager.getInstance(contexto).cancelUniqueWork(TRABAJO_AGUA) }

    fun cancelarPausas(contexto: Context) { WorkManager.getInstance(contexto).cancelUniqueWork(TRABAJO_PAUSAS) }

    fun cancelarTodo(contexto: Context) {
        cancelarAgua(contexto)
        cancelarPausas(contexto)
    }

    private fun programar(contexto: Context, nombre: String, tipo: String, horas: Long) {
        val peticion = PeriodicWorkRequestBuilder<RecordatorioWorker>(horas, TimeUnit.HOURS)
            .setInitialDelay(horas, TimeUnit.HOURS)
            .setInputData(workDataOf("tipo" to tipo))
            .build()
        WorkManager.getInstance(contexto)
            .enqueueUniquePeriodicWork(nombre, ExistingPeriodicWorkPolicy.UPDATE, peticion)
    }
}

// Se ejecuta en segundo plano cada cierto número de horas y muestra la notificación.
class RecordatorioWorker(contexto: Context, parametros: WorkerParameters) : Worker(contexto, parametros) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val tipo = inputData.getString("tipo") ?: return Result.success()
        if (!Recordatorios.tienePermiso(applicationContext)) return Result.success()

        // No molestar de noche: solo entre 8:00 y 21:59.
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (hora < 8 || hora >= 22) return Result.success()

        val (titulo, texto, id) = if (tipo == "agua") {
            Triple("Hora de tomar agua", "Un vaso ahora te acerca a tu meta de hoy.", 1001)
        } else {
            Triple("Haz una pausa activa", "Estírate un par de minutos y descansa la vista.", 1002)
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendiente = PendingIntent.getActivity(
            applicationContext, id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(applicationContext, Recordatorios.CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(id, notificacion)
        return Result.success()
    }
}
