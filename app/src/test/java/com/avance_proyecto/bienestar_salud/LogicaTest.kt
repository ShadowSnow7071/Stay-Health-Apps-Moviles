package com.avance_proyecto.bienestar_salud

import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.data.ValidadorRegistro
import com.avance_proyecto.bienestar_salud.model.Animo
import com.avance_proyecto.bienestar_salud.model.Energia
import com.avance_proyecto.bienestar_salud.model.Esfuerzo
import com.avance_proyecto.bienestar_salud.model.Sueno
import com.avance_proyecto.bienestar_salud.model.comoEnum
import com.avance_proyecto.bienestar_salud.model.prioridad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas unitarias de la lógica pura (no necesitan emulador).
// Van en app/src/test/java/com/avance_proyecto/bienestar_salud/LogicaTest.kt
class LogicaTest {

    private fun ordenPara(energia: Energia?) =
        Esfuerzo.entries.sortedBy { it.prioridad(energia) }.map { it.name }

    @Test
    fun energiaBaja_priorizaTareasLigeras() {
        assertEquals(listOf("LIGERA", "MEDIA", "EXIGENTE"), ordenPara(Energia.BAJA))
    }

    @Test
    fun energiaMedia_combinaMedianasYLigeras() {
        assertEquals(listOf("MEDIA", "LIGERA", "EXIGENTE"), ordenPara(Energia.MEDIA))
    }

    @Test
    fun energiaAlta_priorizaTareasExigentes() {
        assertEquals(listOf("EXIGENTE", "MEDIA", "LIGERA"), ordenPara(Energia.ALTA))
    }

    @Test
    fun sinCheckin_usaElOrdenDeEnergiaMedia() {
        assertEquals(ordenPara(Energia.MEDIA), ordenPara(null))
    }

    @Test
    fun sumarDias_cruzaDeMesYDeAnio() {
        assertEquals("2026-11-01", Fechas.sumarDias("2026-10-31", 1))
        assertEquals("2026-12-30", Fechas.sumarDias("2027-01-02", -3))
    }

    @Test
    fun ultimosSieteDias_terminaEnHoy() {
        val semana = Fechas.ultimosSieteDias("2026-10-31")
        assertEquals(7, semana.size)
        assertEquals("2026-10-25", semana.first())
        assertEquals("2026-10-31", semana.last())
    }

    @Test
    fun fechas_seMuestranEnEspanol() {
        assertEquals("SÁBADO, 31 DE OCTUBRE", Fechas.encabezado("2026-10-31"))
        assertEquals("Hoy, 31 de octubre", Fechas.detalle("2026-10-31", "2026-10-31"))
        assertEquals("Sáb", Fechas.diaCorto("2026-10-31"))
        assertEquals("Mañana", Fechas.etiqueta("2026-11-01", "2026-10-31"))
    }

    @Test
    fun comoEnum_leeValoresGuardadosEnLaBaseDeDatos() {
        assertEquals(Sueno.DE_5_A_7, "DE_5_A_7".comoEnum<Sueno>())
        assertNull(null.comoEnum<Energia>())
        assertNull("NO_EXISTE".comoEnum<Animo>())
    }

    @Test
    fun rangosDeSueno_estanOrdenadosDeMenorAMayor() {
        val horas = Sueno.entries.map { it.horasEstimadas }
        assertTrue(horas == horas.sorted())
    }

    @Test
    fun validadorRegistro_aceptaNombreConEspacios() {
        assertNull(ValidadorRegistro.validarNombreUsuario("Joshua Gil"))
    }

    @Test
    fun validadorRegistro_rechazaNombreCorto() {
        assertEquals("El nombre de usuario debe tener al menos 3 caracteres", ValidadorRegistro.validarNombreUsuario("Jo"))
    }

    @Test
    fun validadorRegistro_validaRequisitosDePassword() {
        // Y0ctac28 no tiene símbolo
        val erroresSinSimbolo = ValidadorRegistro.validarPassword("Y0ctac28")
        assertTrue(erroresSinSimbolo.contains("Al menos un símbolo (ej: !@#$)"))

        // Y0ctac28! cumple todos los requisitos
        val erroresCompleto = ValidadorRegistro.validarPassword("Y0ctac28!")
        assertTrue(erroresCompleto.isEmpty())

        // Registro completo válido con "Joshua Gil" y "Y0ctac28!"
        assertNull(ValidadorRegistro.validarRegistro("Joshua Gil", "Y0ctac28!"))
    }
}
