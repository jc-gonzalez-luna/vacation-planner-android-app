package com.example.d308vacationplanner.ui


import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.repository.VacationRepository
import com.example.d308vacationplanner.entities.Vacation
import com.example.d308vacationplanner.repository.ExcursionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class VacationViewModel (application: Application) : AndroidViewModel(application) {
    private val vacationRepository = VacationRepository(application)
    private val excursionRepository = ExcursionRepository(application)
    private val _allVacations = MutableStateFlow<List<Vacation>>(emptyList())
    val allVacations: StateFlow<List<Vacation>> = _allVacations.asStateFlow()
    private val _excursions = MutableStateFlow<List<Excursion>>(emptyList())
    val excursions: StateFlow<List<Excursion>> = _excursions.asStateFlow()

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
    fun addVacation(vacation: Vacation, onInserted: (Long) -> Unit){
        viewModelScope.launch {
            val newId = vacationRepository.insert(vacation)
            _allVacations.value = vacationRepository.getAllVacations().first()
            onInserted(newId)
        }
    }
    fun updateVacation(vacation: Vacation){
        viewModelScope.launch {
            vacationRepository.updateVacation(vacation)
        }
    }
    fun deleteVacation(vacation: Vacation, callback: VacationRepository.DeleteCallback){
        viewModelScope.launch {
            vacationRepository.deleteVacation(vacation, callback)
        }
    }
    fun loadExcursionsForVacation(vacationId: Long){
        viewModelScope.launch {
            val list = excursionRepository.getExcursionsForVacation(vacationId)
            _excursions.value = list
        }
    }
    fun getExcursion(id: Long): StateFlow<Excursion?>{
        return excursionRepository.getExcursionFlow(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
    }
    fun addExcursion(excursion: Excursion){
        viewModelScope.launch {
            excursionRepository.insert(excursion)
            loadExcursionsForVacation(excursion.vacationID)
        }
    }
    fun updateExcursion(excursion: Excursion){
        viewModelScope.launch {
            excursionRepository.update(excursion)
            loadExcursionsForVacation(excursion.vacationID)
        }
    }
    fun deleteExcursion(excursion: Excursion){
        viewModelScope.launch{
            excursionRepository.delete(excursion)
            loadExcursionsForVacation(excursion.vacationID)
        }
    }

    fun clearExcursions(){
        _excursions.value = emptyList()
    }
    fun shareVacation(context: Context, vacation: Vacation){
        val shareText = """
            |Vacation Details:
            |Title: ${vacation.title}
            |Hotel: ${vacation.hotel}
            |Start Date: ${vacation.startDate}
            |End Date: ${vacation.endDate}
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
}

