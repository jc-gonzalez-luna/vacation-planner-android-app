package com.example.d308vacationplanner.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.repository.VacationRepository


@Composable
fun AppNavGraph (navController: NavHostController){
    val viewModel: VacationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val context = LocalContext.current

    NavHost(navController, startDestination = "vacation_list") {
        composable("vacation_list") {
            val vacations = viewModel.allVacations.collectAsState().value

            VacationListScreen(
                vacations = vacations,
                onAddClick = { navController.navigate("vacation_details/0") },
                onVacationClick = { id -> navController.navigate("vacation_details/$id") }
            )
        }
        composable("vacation_details/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")!!.toLong()
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
                return@composable
            }
            VacationDetailsScreen(
                vacation = vacation,
                excursions = excursions,
                onSave = { v ->
                    if(v.id == 0L){
                        viewModel.addVacation(v){ newId ->
                            navController.popBackStack()
                            navController.navigate("vacation_details/$newId")
                        }
                    } else {
                        viewModel.updateVacation(v)
                        navController.popBackStack()
                    }
                },
                onDelete = { v ->
                    viewModel.deleteVacation(v, object : VacationRepository.DeleteCallback{
                        override fun onDeleteSuccess(){
                            android.os.Handler(android.os.Looper.getMainLooper()).post {
                                navController.popBackStack()
                            }
                        }
                        override fun onDeleteFailed(message: String){

                        }
                    })


                },

                onAddExcursion = {
                    navController.navigate("excursion_details/0?vacationId=$id")
                },
                onEditExcursion = { excursionId ->
                    navController.navigate("excursion_details/$excursionId?vacationId=$id")
                },
                onSetAlerts = { v ->
                    AlertScheduler.scheduleAlert(context, v.startDate, v.title, "starting")
                    AlertScheduler.scheduleAlert(context, v.endDate, v.title, "ending")
                }
            )
        }

        composable("excursion_details/{id}?vacationId={vacationId}") { backStack ->
            val id = backStack.arguments?.getString("id")!!.toLong()
            val vacationId = backStack.arguments?.getString("vacationId")!!.toLong()
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
                            "excursion"
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
    }
}