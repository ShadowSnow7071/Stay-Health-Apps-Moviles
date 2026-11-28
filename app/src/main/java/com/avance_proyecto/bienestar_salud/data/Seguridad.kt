package com.avance_proyecto.bienestar_salud.data

import android.os.Build
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// La contraseña nunca se guarda en texto plano: se guarda un hash PBKDF2 con sal aleatoria.
// Formato guardado: algoritmo:iteraciones:sal:hash
object Seguridad {
    private const val ITERACIONES = 120_000
    private const val LONGITUD_BITS = 256

    fun hashear(password: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val algoritmo = algoritmoDisponible()
        val hash = derivar(password, sal, ITERACIONES, algoritmo)
        return listOf(algoritmo, ITERACIONES.toString(), codificar(sal), codificar(hash))
            .joinToString(":")
    }

    fun verificar(password: String, almacenado: String): Boolean {
        val partes = almacenado.split(":")
        if (partes.size != 4) return false
        val iteraciones = partes[1].toIntOrNull() ?: return false
        return try {
            val sal = Base64.decode(partes[2], Base64.NO_WRAP)
            val esperado = Base64.decode(partes[3], Base64.NO_WRAP)
            val calculado = derivar(password, sal, iteraciones, partes[0])
            MessageDigest.isEqual(esperado, calculado)
        } catch (e: Exception) {
            false
        }
    }

    private fun algoritmoDisponible(): String =
        if (Build.VERSION.SDK_INT >= 26) "PBKDF2WithHmacSHA256" else "PBKDF2WithHmacSHA1"

    private fun derivar(password: String, sal: ByteArray, iteraciones: Int, algoritmo: String): ByteArray {
        val especificacion = PBEKeySpec(password.toCharArray(), sal, iteraciones, LONGITUD_BITS)
        return SecretKeyFactory.getInstance(algoritmo).generateSecret(especificacion).encoded
    }

    private fun codificar(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
}

object ValidadorRegistro {
    fun validarPassword(password: String): List<String> {
        val errores = mutableListOf<String>()
        if (password.length < 6) errores.add("Mínimo 6 caracteres")
        if (!password.any { it.isUpperCase() }) errores.add("Al menos una letra mayúscula")
        if (!password.any { it.isDigit() }) errores.add("Al menos un número")
        if (!password.any { !it.isLetterOrDigit() }) errores.add("Al menos un símbolo (ej: !@#$)")
        return errores
    }

    fun validarNombreUsuario(nombre: String): String? {
        val limpio = nombre.trim()
        if (limpio.length < 3) return "El nombre de usuario debe tener al menos 3 caracteres"
        if (!Regex("^[\\p{L}\\p{N}._\\- ]+$").matches(limpio)) {
            return "Usa solo letras, números, espacios, punto, guion o guion bajo"
        }
        return null
    }

    fun validarRegistro(nombre: String, password: String): String? {
        val errorNombre = validarNombreUsuario(nombre)
        if (errorNombre != null) return errorNombre
        val erroresPass = validarPassword(password)
        return if (erroresPass.isNotEmpty()) {
            "La contraseña debe cumplir: " + erroresPass.joinToString(", ")
        } else null
    }
}
