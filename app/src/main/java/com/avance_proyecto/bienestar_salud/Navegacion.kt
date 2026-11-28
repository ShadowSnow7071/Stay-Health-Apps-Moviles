package com.avance_proyecto.bienestar_salud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.avance_proyecto.bienestar_salud.ui.components.esPantallaAncha
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaAjustes
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaBienvenida
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaCheckin
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaLogin
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaMiDia
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaProgreso
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaRegistro
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaTareaForm
import com.avance_proyecto.bienestar_salud.ui.screens.PantallaTareas
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.ChipFondo
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val CHECKIN = "checkin"
    const val CHECKIN_RUTA = "checkin?edicion={edicion}"
    const val CHECKIN_EDICION = "checkin?edicion=true"
    const val MIDIA = "midia"
    const val TAREAS = "tareas"
    const val PROGRESO = "progreso"
    const val AJUSTES = "ajustes"
    const val TAREA_FORM = "tarea_form/{id}"

    fun tareaForm(id: Long) = "tarea_form/$id"
}

private data class DestinoTab(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val destinosTab = listOf(
    DestinoTab(Rutas.MIDIA, "Mi día", Icons.Outlined.Today),
    DestinoTab(Rutas.TAREAS, "Tareas", Icons.Outlined.Checklist),
    DestinoTab(Rutas.PROGRESO, "Progreso", Icons.Outlined.Insights)
)

// Espera a que se restaure la sesión para saber con qué pantalla arrancar.
@Composable
fun AppNav(vm: AppViewModel) {
    val inicio by vm.destinoInicial.collectAsState()
    val destino = inicio
    if (destino == null) {
        Box(Modifier.fillMaxSize().background(FondoApp))
    } else {
        AppNavContenido(vm, destino)
    }
}

@Composable
private fun AppNavContenido(vm: AppViewModel, destinoInicial: String) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val mostrarTabs = rutaActual == Rutas.MIDIA || rutaActual == Rutas.TAREAS || rutaActual == Rutas.PROGRESO
    val ancha = esPantallaAncha()

    Scaffold(
        containerColor = FondoApp,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Teléfono en vertical: barra inferior.
            if (mostrarTabs && !ancha) {
                BarraInferior(rutaActual) { nav.irATab(it) }
            }
        }
    ) { relleno ->
        Row(Modifier.fillMaxSize().padding(relleno)) {
            // Horizontal o tablet: riel de navegación a la izquierda.
            if (mostrarTabs && ancha) {
                RielLateral(rutaActual) { nav.irATab(it) }
            }
            NavHost(
                navController = nav,
                startDestination = destinoInicial,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                composable(Rutas.BIENVENIDA) {
                    PantallaBienvenida(
                        onIniciarSesion = { nav.navigate(Rutas.LOGIN) },
                        onCrearCuenta = { nav.navigate(Rutas.REGISTRO) }
                    )
                }
                composable(Rutas.LOGIN) {
                    PantallaLogin(
                        vm = vm,
                        onAtras = { nav.popBackStack() },
                        onCrearCuenta = {
                            nav.navigate(Rutas.REGISTRO) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                        },
                        onExito = { destino -> nav.irAInicio(destino) }
                    )
                }
                composable(Rutas.REGISTRO) {
                    PantallaRegistro(
                        vm = vm,
                        onAtras = { nav.popBackStack() },
                        onIniciarSesion = {
                            nav.navigate(Rutas.LOGIN) { popUpTo(Rutas.REGISTRO) { inclusive = true } }
                        },
                        onExito = { destino -> nav.irAInicio(destino) }
                    )
                }
                composable(
                    route = Rutas.CHECKIN_RUTA,
                    arguments = listOf(
                        navArgument("edicion") {
                            type = NavType.BoolType
                            defaultValue = false
                        }
                    )
                ) { entradaActual ->
                    val esEdicion = entradaActual.arguments?.getBoolean("edicion") ?: false
                    PantallaCheckin(
                        vm = vm,
                        esEdicion = esEdicion,
                        onAjustes = { nav.navigate(Rutas.AJUSTES) },
                        onTerminar = {
                            if (esEdicion) nav.popBackStack() else nav.irAInicio(Rutas.MIDIA)
                        }
                    )
                }
                composable(Rutas.MIDIA) {
                    PantallaMiDia(
                        vm = vm,
                        onAjustes = { nav.navigate(Rutas.AJUSTES) },
                        onEditarCheckin = { nav.navigate(Rutas.CHECKIN_EDICION) },
                        onAbrirTarea = { id -> nav.navigate(Rutas.tareaForm(id)) },
                        onAgregarTarea = { nav.navigate(Rutas.tareaForm(0L)) },
                        onVerTodas = { nav.irATab(Rutas.TAREAS) }
                    )
                }
                composable(Rutas.TAREAS) {
                    PantallaTareas(
                        vm = vm,
                        onAgregar = { nav.navigate(Rutas.tareaForm(0L)) },
                        onEditar = { id -> nav.navigate(Rutas.tareaForm(id)) }
                    )
                }
                composable(Rutas.PROGRESO) {
                    PantallaProgreso(vm = vm, onAjustes = { nav.navigate(Rutas.AJUSTES) })
                }
                composable(
                    route = Rutas.TAREA_FORM,
                    arguments = listOf(navArgument("id") { type = NavType.LongType })
                ) { entradaActual ->
                    val id = entradaActual.arguments?.getLong("id") ?: 0L
                    PantallaTareaForm(vm = vm, id = id, onVolver = { nav.popBackStack() })
                }
                composable(Rutas.AJUSTES) {
                    PantallaAjustes(
                        vm = vm,
                        onAtras = { nav.popBackStack() },
                        onSesionCerrada = {
                            nav.navigate(Rutas.BIENVENIDA) {
                                popUpTo(nav.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    }
}

// Cambiar de pestaña conservando el estado de cada una.
private fun NavHostController.irATab(ruta: String) {
    navigate(ruta) {
        popUpTo(Rutas.MIDIA) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// Entrar a la app (o ir a una pantalla raíz) limpiando el historial anterior.
private fun NavHostController.irAInicio(destino: String) {
    navigate(destino) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun BarraInferior(rutaActual: String?, onIr: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        destinosTab.forEach { destino ->
            val seleccionado = rutaActual == destino.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { onIr(destino.ruta) },
                icon = { Icon(destino.icono, contentDescription = null) },
                label = {
                    Text(
                        destino.etiqueta,
                        fontSize = 13.sp,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AzulTexto,
                    selectedTextColor = AzulTexto,
                    indicatorColor = ChipFondo,
                    unselectedIconColor = TextoSecundario,
                    unselectedTextColor = TextoSecundario
                )
            )
        }
    }
}

@Composable
private fun RielLateral(rutaActual: String?, onIr: (String) -> Unit) {
    NavigationRail(containerColor = Color.White) {
        Spacer(Modifier.height(8.dp))
        destinosTab.forEach { destino ->
            val seleccionado = rutaActual == destino.ruta
            NavigationRailItem(
                selected = seleccionado,
                onClick = { onIr(destino.ruta) },
                icon = { Icon(destino.icono, contentDescription = null) },
                label = {
                    Text(
                        destino.etiqueta,
                        fontSize = 13.sp,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = AzulTexto,
                    selectedTextColor = AzulTexto,
                    indicatorColor = ChipFondo,
                    unselectedIconColor = TextoSecundario,
                    unselectedTextColor = TextoSecundario
                )
            )
        }
    }
}
