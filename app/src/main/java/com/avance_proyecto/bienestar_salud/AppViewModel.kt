package com.avance_proyecto.bienestar_salud

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.avance_proyecto.bienestar_salud.data.AppDatabase
import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.data.Preferencias
import com.avance_proyecto.bienestar_salud.data.RegistroDia
import com.avance_proyecto.bienestar_salud.data.Seguridad
import com.avance_proyecto.bienestar_salud.data.ValidadorRegistro
import com.avance_proyecto.bienestar_salud.data.Tarea
import com.avance_proyecto.bienestar_salud.data.Usuario
import com.avance_proyecto.bienestar_salud.model.Animo
import com.avance_proyecto.bienestar_salud.model.Energia
import com.avance_proyecto.bienestar_salud.model.Esfuerzo
import com.avance_proyecto.bienestar_salud.model.META_VASOS
import com.avance_proyecto.bienestar_salud.model.Sueno
import com.avance_proyecto.bienestar_salud.recordatorios.Recordatorios
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

// Resultado de iniciar sesión o crear cuenta: hay un error que mostrar, o un destino al que ir.
data class ResultadoAuth(val error: String? = null, val destino: String? = null)

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.obtener(app)
    val prefs = Preferencias(app)

    // Evita perder un toque cuando se pulsa "+" muy rápido (dos escrituras a la vez).
    private val candado = Mutex()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // null = todavía restaurando la sesión; luego trae la ruta con la que arranca la app.
    private val _destinoInicial = MutableStateFlow<String?>(null)
    val destinoInicial: StateFlow<String?> = _destinoInicial.asStateFlow()

    private val hoyFlow = MutableStateFlow(Fechas.hoy())
    private val diaSeleccionado = MutableStateFlow<String?>(null)

    val diaDetalle: StateFlow<String> =
        combine(hoyFlow, diaSeleccionado) { hoy, dia -> dia ?: hoy }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Fechas.hoy())

    // Registro (check-in, agua, hábitos) del día de hoy.
    val registroHoy: StateFlow<RegistroDia?> =
        combine(_usuario, hoyFlow) { u, fecha -> u?.id to fecha }
            .flatMapLatest { (uid, fecha) ->
                if (uid == null) flowOf(null) else db.registroDao().observar(uid, fecha)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Registros de los últimos 7 días (para la gráfica y los totales).
    val semana: StateFlow<List<RegistroDia>> =
        combine(_usuario, hoyFlow) { u, fecha -> u?.id to fecha }
            .flatMapLatest { (uid, fecha) ->
                if (uid == null) flowOf(emptyList())
                else db.registroDao().observarRango(uid, Fechas.sumarDias(fecha, -6), fecha)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Registro del día elegido en "Detalle del día".
    val registroDetalle: StateFlow<RegistroDia?> =
        combine(_usuario, diaDetalle) { u, dia -> u?.id to dia }
            .flatMapLatest { (uid, dia) ->
                if (uid == null) flowOf(null) else db.registroDao().observar(uid, dia)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val tareas: StateFlow<List<Tarea>> =
        _usuario
            .flatMapLatest { u ->
                if (u == null) flowOf(emptyList()) else db.tareaDao().observar(u.id)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Restaurar la sesión si la persona marcó "Mantener sesión iniciada".
        viewModelScope.launch {
            val id = prefs.sesionUsuarioId
            val guardado = if (id > 0) db.usuarioDao().buscarPorId(id) else null
            if (guardado == null) {
                prefs.sesionUsuarioId = -1L
                _destinoInicial.value = Rutas.BIENVENIDA
            } else {
                _usuario.value = guardado
                _destinoInicial.value = destinoPara(guardado.id)
            }
        }
    }

    // Si ya hizo el check-in de hoy va a "Mi día"; si no, al check-in.
    private suspend fun destinoPara(usuarioId: Long): String {
        val registro = db.registroDao().obtener(usuarioId, hoyFlow.value)
        return if (registro?.checkinCompleto() == true) Rutas.MIDIA else Rutas.CHECKIN
    }

    fun actualizarFecha() {
        hoyFlow.value = Fechas.hoy()
    }

    // ---------- Cuenta y sesión ----------

    fun registrarse(nombre: String, password: String, alResultar: (ResultadoAuth) -> Unit) {
        viewModelScope.launch {
            val limpio = nombre.trim()
            val problema = validarRegistro(limpio, password)
            if (problema != null) {
                alResultar(ResultadoAuth(error = problema))
                return@launch
            }
            if (db.usuarioDao().buscarPorNombre(limpio) != null) {
                alResultar(ResultadoAuth(error = "Ese nombre de usuario ya existe"))
                return@launch
            }
            val hash = withContext(Dispatchers.Default) { Seguridad.hashear(password) }
            val id = try {
                db.usuarioDao().insertar(Usuario(nombreUsuario = limpio, passwordHash = hash))
            } catch (e: SQLiteConstraintException) {
                alResultar(ResultadoAuth(error = "Ese nombre de usuario ya existe"))
                return@launch
            }
            _usuario.value = Usuario(id = id, nombreUsuario = limpio, passwordHash = hash)
            prefs.sesionUsuarioId = -1L
            alResultar(ResultadoAuth(destino = Rutas.CHECKIN))
        }
    }

    fun iniciarSesion(
        nombre: String,
        password: String,
        mantenerSesion: Boolean,
        alResultar: (ResultadoAuth) -> Unit
    ) {
        viewModelScope.launch {
            val mensaje = "Usuario o contraseña incorrectos"
            val encontrado = db.usuarioDao().buscarPorNombre(nombre.trim())
            if (encontrado == null) {
                alResultar(ResultadoAuth(error = mensaje))
                return@launch
            }
            val coincide = withContext(Dispatchers.Default) {
                Seguridad.verificar(password, encontrado.passwordHash)
            }
            if (!coincide) {
                alResultar(ResultadoAuth(error = mensaje))
                return@launch
            }
            _usuario.value = encontrado
            prefs.sesionUsuarioId = if (mantenerSesion) encontrado.id else -1L
            alResultar(ResultadoAuth(destino = destinoPara(encontrado.id)))
        }
    }

    fun cerrarSesion() {
        prefs.sesionUsuarioId = -1L
        diaSeleccionado.value = null
        _usuario.value = null
    }

    fun eliminarCuenta() {
        val actual = _usuario.value ?: return
        viewModelScope.launch {
            // ON DELETE CASCADE borra también sus registros y tareas.
            db.usuarioDao().eliminar(actual.id)
            Recordatorios.cancelarTodo(getApplication())
            prefs.recordarAgua = false
            prefs.recordarPausas = false
            cerrarSesion()
        }
    }

    fun validarPassword(password: String): List<String> =
        ValidadorRegistro.validarPassword(password)

    fun validarRegistro(nombre: String, password: String): String? =
        ValidadorRegistro.validarRegistro(nombre, password)

    // ---------- Check-in, agua y hábitos ----------

    private fun actualizarHoy(cambio: (RegistroDia) -> RegistroDia) {
        val actual = _usuario.value ?: return
        viewModelScope.launch {
            candado.withLock {
                val fecha = hoyFlow.value
                val registro = db.registroDao().obtener(actual.id, fecha)
                    ?: RegistroDia(usuarioId = actual.id, fecha = fecha)
                db.registroDao().guardar(cambio(registro))
            }
        }
    }

    fun guardarCheckin(sueno: Sueno, energia: Energia, animo: Animo) = actualizarHoy {
        it.copy(sueno = sueno.name, energia = energia.name, animo = animo.name)
    }

    fun cambiarEnergia(energia: Energia) = actualizarHoy { it.copy(energia = energia.name) }

    fun sumarVaso() = actualizarHoy {
        if (it.vasos < META_VASOS) it.copy(vasos = it.vasos + 1) else it
    }

    fun restarVaso() = actualizarHoy {
        if (it.vasos > 0) it.copy(vasos = it.vasos - 1) else it
    }

    fun alternarPausa() = actualizarHoy { it.copy(pausaHecha = !it.pausaHecha) }

    fun alternarConcentracion() = actualizarHoy { it.copy(concentracionHecha = !it.concentracionHecha) }

    fun seleccionarDia(fecha: String?) {
        diaSeleccionado.value = fecha
    }

    // ---------- Tareas ----------

    suspend fun obtenerTarea(id: Long): Tarea? {
        val actual = _usuario.value ?: return null
        return db.tareaDao().obtener(id, actual.id)
    }

    fun guardarTarea(id: Long, nombre: String, fecha: String, hora: String?, esfuerzo: Esfuerzo) {
        val actual = _usuario.value ?: return
        viewModelScope.launch {
            val existente = if (id != 0L) db.tareaDao().obtener(id, actual.id) else null
            val base = existente ?: Tarea(
                usuarioId = actual.id,
                nombre = nombre,
                fecha = fecha,
                esfuerzo = esfuerzo.name
            )
            db.tareaDao().guardar(
                base.copy(nombre = nombre.trim(), fecha = fecha, hora = hora, esfuerzo = esfuerzo.name)
            )
        }
    }

    fun alternarTarea(tarea: Tarea) {
        viewModelScope.launch {
            val hecha = !tarea.hecha
            db.tareaDao().actualizar(
                tarea.copy(hecha = hecha, fechaHecha = if (hecha) hoyFlow.value else null)
            )
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch { db.tareaDao().eliminar(tarea) }
    }
}
