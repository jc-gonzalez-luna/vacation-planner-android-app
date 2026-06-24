package com.example.d308vacationplanner.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation
import com.example.d308vacationplanner.repository.ExcursionRepository
import com.example.d308vacationplanner.repository.VacationRepository
import com.example.d308vacationplanner.ui.alerts.AlertScheduler
import com.example.d308vacationplanner.ui.components.BaseItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import java.text.NumberFormat
import java.util.Locale

class VacationViewModel (application: Application) : AndroidViewModel(application) {
    private val vacationRepository = VacationRepository(application)
    private val excursionRepository = ExcursionRepository(application)
    private val _allVacations = MutableStateFlow<List<Vacation>>(emptyList())
    val allVacations: StateFlow<List<Vacation>> = _allVacations.asStateFlow()
    private val _excursions = MutableStateFlow<List<Excursion>>(emptyList())
    val excursions: StateFlow<List<Excursion>> = _excursions.asStateFlow()
    private val _totalSpent = MutableStateFlow(0.0)
    val totalSpent: StateFlow<Double> = _totalSpent.asStateFlow()

    init {
        viewModelScope.launch {
            vacationRepository.getAllVacations().collect { list ->
                _allVacations.value = list
            }
        }
    }

    fun getVacation(id: Long): StateFlow<Vacation?> {
        return vacationRepository.getVacationFlow(id)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )
    }

    fun addVacation(vacation: Vacation, onInserted: (Long) -> Unit) {
        viewModelScope.launch {
            val newId = vacationRepository.insert(vacation)
            _allVacations.value = vacationRepository.getAllVacations().first()
            onInserted(newId)
        }
    }

    fun updateVacation(vacation: Vacation) {
        viewModelScope.launch {
            vacationRepository.updateVacation(vacation)
        }
    }

    fun deleteVacation(vacation: Vacation, callback: VacationRepository.DeleteCallback) {
        viewModelScope.launch {
            vacationRepository.deleteVacation(vacation, callback)
        }
    }

    fun loadExcursionsForVacation(vacationId: Long) {
        viewModelScope.launch {
            excursionRepository.getExcursionsForVacation(vacationId).collectLatest { list ->
                _excursions.value = list
                _totalSpent.value = list.sumOf { it.price }
            }
        }
    }

    fun loadExcursionsByPrice(vacationId: Long) {
        viewModelScope.launch {
            excursionRepository.getExcursionsByPrice(vacationId).collect { list ->
                _excursions.value = list
            }
        }
    }

    fun loadTotalSpent(vacationId: Long) {
        viewModelScope.launch {
            val total = excursionRepository.getTotalSpent(vacationId)
            _totalSpent.value = total
        }
    }

    fun getExcursion(id: Long): StateFlow<Excursion?> {
        return excursionRepository.getExcursionFlow(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    }

    fun addExcursion(excursion: Excursion) {
        viewModelScope.launch {
            excursionRepository.insert(excursion)
            loadExcursionsForVacation(excursion.vacationID)
        }
    }

    fun updateExcursion(excursion: Excursion) {
        viewModelScope.launch {
            excursionRepository.update(excursion)
            loadExcursionsForVacation(excursion.vacationID)
        }
    }

    fun deleteExcursion(excursion: Excursion) {
        viewModelScope.launch {
            excursionRepository.delete(excursion)
            loadExcursionsForVacation(excursion.vacationID)
        }
    }

    fun clearExcursions() {
        _excursions.value = emptyList()
        _totalSpent.value = 0.0
    }

    fun shareVacation(
        context: Context,
        vacation: Vacation,
        excursions: List<Excursion>,
        totalSpent: Double,
        budget: Double
    ) {
        val currency = NumberFormat.getCurrencyInstance(Locale.US)
        val remaining = budget - totalSpent
        val excursionText = if (excursions.isEmpty()){
            "No excursions added yet."
        }else {
            excursions.joinToString("\n"){ "• ${it.title}: ${currency.format(it.price)}"}
        }
        val shareText = """
            Vacation Details:
            
            Title: ${vacation.title}
            Hotel: ${vacation.hotel}
            Start Date: ${vacation.startDate}
            End Date: ${vacation.endDate}
            
            Budget: $${currency.format(budget)}
            Total Spent: $${currency.format(totalSpent)}
            Remaining: $${currency.format(remaining)}
            
            Excursions:
            $excursionText
        """.trimMargin()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Share Vacation Details")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun setAlerts(
        context: Context,
        vacation: Vacation,
        reminderDays: Set<Int>
    ) {
        viewModelScope.launch {
            excursionRepository.getExcursionsForVacation(vacation.id)
                .collectLatest { excursions ->

            AlertScheduler.scheduleAllAlerts(
                context = context,
                vacationId = vacation.id,
                startDate = vacation.startDate,
                reminderDays = reminderDays,
                excursions = excursions
            )
        }
        }

    }
    fun printSummaries(items: List<BaseItem>){
        items.forEach { item ->
            Log.d("Summary", item.displaySummary())
        }
    }
    fun logPolymorphism( vacation: Vacation, excursions: List<Excursion>){
        val items = mutableListOf<BaseItem>()
        items.add(vacation)
        items.addAll(excursions)
        printSummaries(items)
    }


    fun getExcursionsForVacation(vacationId: Long) =
        excursionRepository.getExcursionsForVacation(vacationId)
}