package com.example.sistematicket

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sistematicket.ui.componentes.BarraInferior
import com.example.sistematicket.ui.componentes.BarraSuperior
import com.example.sistematicket.ui.componentes.MenuLateral
import com.example.sistematicket.ui.pantallas.LoginScreen
import com.example.sistematicket.ui.pantallas.PantallaCargar
import com.example.sistematicket.ui.pantallas.PantallaCerrarSesion
import com.example.sistematicket.ui.pantallas.PantallaDetalleTicket
import com.example.sistematicket.ui.pantallas.PantallaEliminar
import com.example.sistematicket.ui.pantallas.PantallaNuevoTicket
import com.example.sistematicket.ui.pantallas.PantallaRegistro
import com.example.sistematicket.ui.pantallas.PantallaSplash
import com.example.sistematicket.ui.pantallas.PantallaTickets
import com.example.sistematicket.ui.pantallas.PantallaVisor
import com.example.sistematicket.ui.pantallas.PantallaVisualizar
import com.example.sistematicket.viewmodel.AuthViewModel
import com.example.sistematicket.viewmodel.DocumentoViewModel
import com.example.sistematicket.viewmodel.TicketViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    val authViewModel: AuthViewModel = viewModel()
    val ticketViewModel: TicketViewModel = viewModel()
    val documentoViewModel: DocumentoViewModel = viewModel()
    val usuario by authViewModel.usuario.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val mostrarMensaje: (String) -> Unit = { texto ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(texto)
        }
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val mostrarBarras = rutaActual != null && rutaActual !in Rutas.SIN_BARRAS
    val esPrincipal = rutaActual in Rutas.PRINCIPALES

    fun irAPestana(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Rutas.TICKETS) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun irDesdeMenu(ruta: String) {
        scope.launch { drawerState.close() }
        if (usuario == null) return
        if (ruta in Rutas.PRINCIPALES) irAPestana(ruta) else navController.navigate(ruta) { launchSingleTop = true }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    LaunchedEffect(usuario, rutaActual) {
        if (usuario == null && rutaActual != null && rutaActual !in Rutas.SIN_BARRAS) {
            navController.navigate(Rutas.LOGIN) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || esPrincipal,
        drawerContent = {
            MenuLateral(
                usuario = usuario,
                rutaActual = rutaActual,
                onSeleccion = ::irDesdeMenu,
                onCerrarSesion = { irDesdeMenu(Rutas.CERRAR_SESION) }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (mostrarBarras) {
                    BarraSuperior(
                        titulo = Rutas.titulo(rutaActual),
                        mostrarMenu = esPrincipal,
                        usuario = usuario,
                        onAbrirMenu = { scope.launch { drawerState.open() } },
                        onVolver = { navController.popBackStack() },
                        onCerrarSesion = { navController.navigate(Rutas.CERRAR_SESION) { launchSingleTop = true } }
                    )
                }
            },
            bottomBar = {
                if (mostrarBarras && esPrincipal) {
                    BarraInferior(rutaActual = rutaActual, onSeleccion = ::irAPestana)
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Rutas.SPLASH,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .imePadding(),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                composable(Rutas.SPLASH) {
                    PantallaSplash(haySesion = usuario != null) { haySesion ->
                        navController.navigate(if (haySesion) Rutas.TICKETS else Rutas.LOGIN) {
                            popUpTo(Rutas.SPLASH) { inclusive = true }
                        }
                    }
                }

                composable(Rutas.LOGIN) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginExitoso = {
                            navController.navigate(Rutas.TICKETS) {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        },
                        onRegistroClick = { navController.navigate(Rutas.REGISTRO) }
                    )
                }

                composable(Rutas.REGISTRO) {
                    PantallaRegistro(
                        viewModel = authViewModel,
                        onRegistrado = {
                            navController.popBackStack()
                            mostrarMensaje("Cuenta creada. Ahora inicia sesión")
                        },
                        onVolver = {
                            authViewModel.limpiarRegistro()
                            navController.popBackStack()
                        }
                    )
                }

                composable(Rutas.TICKETS) {
                    PantallaTickets(
                        viewModel = ticketViewModel,
                        nombreUsuario = usuario?.nombre.orEmpty(),
                        onNuevoTicket = { navController.navigate(Rutas.NUEVO_TICKET) },
                        onAbrirTicket = { id -> navController.navigate(Rutas.detalleTicket(id)) }
                    )
                }

                composable(Rutas.NUEVO_TICKET) {
                    PantallaNuevoTicket(
                        viewModel = ticketViewModel,
                        onCreado = {
                            navController.popBackStack()
                            mostrarMensaje("Ticket creado correctamente")
                        }
                    )
                }

                composable(
                    Rutas.DETALLE_TICKET,
                    arguments = listOf(navArgument("ticketId") { type = NavType.LongType })
                ) { entradaPila ->
                    PantallaDetalleTicket(
                        ticketId = entradaPila.arguments?.getLong("ticketId") ?: 0L,
                        ticketViewModel = ticketViewModel,
                        documentoViewModel = documentoViewModel,
                        onAbrirDocumento = { id -> navController.navigate(Rutas.visor(id)) },
                        onEliminado = {
                            navController.popBackStack()
                            mostrarMensaje("Ticket eliminado")
                        },
                        onMensaje = mostrarMensaje
                    )
                }

                composable(Rutas.CARGAR) {
                    PantallaCargar(
                        documentoViewModel = documentoViewModel,
                        ticketViewModel = ticketViewModel,
                        onSubido = {
                            mostrarMensaje("Documento cargado correctamente")
                            irAPestana(Rutas.VISUALIZAR)
                        }
                    )
                }

                composable(Rutas.VISUALIZAR) {
                    PantallaVisualizar(
                        viewModel = documentoViewModel,
                        onAbrirDocumento = { id -> navController.navigate(Rutas.visor(id)) },
                        onCargar = { irAPestana(Rutas.CARGAR) }
                    )
                }

                composable(
                    Rutas.VISOR,
                    arguments = listOf(navArgument("documentoId") { type = NavType.LongType })
                ) { entradaPila ->
                    PantallaVisor(
                        documentoId = entradaPila.arguments?.getLong("documentoId") ?: 0L,
                        viewModel = documentoViewModel,
                        onMensaje = mostrarMensaje
                    )
                }

                composable(Rutas.ELIMINAR) {
                    PantallaEliminar(
                        viewModel = documentoViewModel,
                        onMensaje = mostrarMensaje
                    )
                }

                composable(Rutas.CERRAR_SESION) {
                    PantallaCerrarSesion(
                        usuario = usuario,
                        ticketViewModel = ticketViewModel,
                        documentoViewModel = documentoViewModel,
                        onConfirmar = {
                            authViewModel.cerrarSesion()
                            ticketViewModel.limpiar()
                            documentoViewModel.limpiar()
                            navController.navigate(Rutas.LOGIN) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                            mostrarMensaje("Sesión cerrada")
                        },
                        onCancelar = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
