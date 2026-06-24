package com.example.d308vacationplanner.ui.navigation

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.repository.VacationRepository
import com.example.d308vacationplanner.ui.alerts.AlertScheduler
import com.example.d308vacationplanner.ui.screens.ExcursionDetailsScreen
import com.example.d308vacationplanner.ui.screens.VacationDetailsScreen
import com.example.d308vacationplanner.ui.screens.VacationListScreen
import com.example.d308vacationplanner.ui.screens.VacationReportScreen
import com.example.d308vacationplanner.ui.utils.DateUtils
import com.example.d308vacationplanner.ui.viewmodel.VacationViewModel


@Composable
fun AppNavGraph (
    navController: NavHostController,
    deepLinkVacationId: Long = -1,
    deepLinkExcursionId: Long = -1
){
    val viewModel: VacationViewModel = viewModel()
    val context = LocalContext.current
    var handleDeepLink by remember { mutableStateOf(false) }

    LaunchedEffect(deepLinkVacationId, deepLinkExcursionId) {
        if (!handleDeepLink){
            handleDeepLink = true

            if (deepLinkExcursionId != -1L){
                navController.navigate(
                    "excursion_details/$deepLinkExcursionId?vacationId=0&totalSpent=0.0"
                )
            } else if(deepLinkVacationId != -1L){
                navController.navigate("vacation_details/$deepLinkVacationId")
            }
        }


    }
    val startDestination = remember {
        when {
            deepLinkExcursionId != -1L -> "excursion_details/$deepLinkExcursionId?vacationId=0&totalSpent=0.0"
            deepLinkVacationId != -1L -> "vacation_details/$deepLinkVacationId"
            else -> "vacation_list"
        }
    }


    NavHost(navController = navController, startDestination = startDestination) {



        composable("vacation_list") {
            val vacations = viewModel.allVacations.collectAsState().value

            VacationListScreen(
                vacations = vacations,
                onAddClick = { navController.navigate("vacation_details/0") },
                onVacationClick = { id -> navController.navigate("vacation_details/$id") },
                onToggleFavorite = { updatedVacation ->
                    viewModel.updateVacation(updatedVacation)
                }
            )
        }
        composable("vacation_details/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: 0L
            val vacationFlow = remember(id) { viewModel.getVacation(id) }
            val vacation = vacationFlow.collectAsState().value

            LaunchedEffect(id) {
                if (id == 0L) {
                    viewModel.clearExcursions()
                } else {
                    viewModel.loadExcursionsForVacation(id)
                }
            }

            val excursions = viewModel.excursions.collectAsState().value
            if(id != 0L && vacation == null){
                Text(
                    "Loading...",
                    modifier = Modifier.padding(16.dp)
                )
                return@composable
            }

            val totalSpent = excursions.sumOf {
                it.price
            }

            VacationDetailsScreen(
                vacation = vacation,
                excursions = excursions,
                onSave = { v ->
                    if (v.id == 0L) {
                        viewModel.addVacation(v) { newId ->
                            navController.popBackStack()
                            navController.navigate("vacation_details/$newId")
                        }
                    } else {
                        viewModel.updateVacation(v)
                        navController.popBackStack()
                    }
                },
                onDelete = { v ->
                    viewModel.deleteVacation(v, object : VacationRepository.DeleteCallback {
                        override fun onDeleteSuccess() {
                            Handler(Looper.getMainLooper()).post {
                                navController.popBackStack()
                            }
                        }

                        override fun onDeleteFailed(message: String) {

                        }
                    })


                },

                onAddExcursion = {
                    navController.navigate("excursion_details/0?vacationId=$id&totalSpent=$totalSpent")
                },
                onEditExcursion = { excursionId ->
                    navController.navigate("excursion_details/$excursionId?vacationId=$id&totalSpent=$totalSpent")
                },
                onSetAlerts = { v, selectedDays ->
                    val updateVacation = v.copy(reminderDays = selectedDays.toList())
                    viewModel.updateVacation(updateVacation)

                    selectedDays.forEach { daysBefore ->
                        val alertDate = DateUtils.daysBefore(v.startDate, daysBefore)

                        AlertScheduler.scheduleAlert(
                            context,
                            alertDate,
                            updateVacation.title,
                            "starting_soon_${daysBefore}",
                            updateVacation.id
                        )
                    }

                    AlertScheduler.scheduleAlert(
                        context,
                        updateVacation.startDate,
                        updateVacation.title,
                        "starting",
                        updateVacation.id
                    )
                    AlertScheduler.scheduleAlert(
                        context,
                        updateVacation.endDate,
                        updateVacation.title,
                        "ending",
                        updateVacation.id
                    )
                },
                onViewReport = {
                    navController.navigate("report/$id")
                }
            )
        }

        composable("excursion_details/{id}?vacationId={vacationId}&totalSpent={totalSpent}") { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: 0L
            val vacationId = backStack.arguments?.getString("vacationId")?.toLongOrNull() ?: 0L
            val totalSpent = backStack.arguments?.getString("totalSpent")?.toDoubleOrNull() ?: 0.0
            val excursionFlow = remember(id) { viewModel.getExcursion(id) }
            val excursion = excursionFlow.collectAsState().value
            val vacationFlow = remember(vacationId){ viewModel.getVacation(vacationId)}
            val vacation = vacationFlow.collectAsState().value
            if (vacation == null) {
                Text("loading...", modifier = Modifier.padding(16.dp))
                return@composable
            }
            key("$vacationId-$id") {
                ExcursionDetailsScreen(
                    excursion = excursion,
                    vacation = vacation,
                    vacationId = vacationId,
                    totalSpent = totalSpent,
                    onSave = { update ->
                        if (update.id == 0L) {
                            viewModel.addExcursion(update.copy(vacationID = vacationId))
                        } else {
                            viewModel.updateExcursion(update)
                        }
                        AlertScheduler.scheduleAlert(
                            context,
                            update.date,
                            update.title,
                            "excursion",
                            update.id
                        )
                        navController.popBackStack()
                    },
                    onDelete = { e ->
                        viewModel.deleteExcursion(e)
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            route = "report/{vacationId}",
            arguments = listOf(navArgument("vacationId"){ type = NavType.LongType})
        ){ backStackEntry ->
            val vacationId = backStackEntry.arguments!!.getLong("vacationId")

            val vacationFlow = remember(vacationId){
                viewModel.getVacation(vacationId)
            }
            val vacation by vacationFlow.collectAsState(initial = null)
            val excursions by viewModel.getExcursionsForVacation(vacationId).collectAsState(initial = emptyList())

            if (vacation != null){
                VacationReportScreen(
                    vacation = vacation!!,
                    excursions = excursions,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}