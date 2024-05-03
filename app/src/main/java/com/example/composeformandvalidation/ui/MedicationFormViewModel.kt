package com.example.composeformandvalidation.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicationFormViewModel : ViewModel() {
    private var _formUiState = MutableStateFlow(FormState())
    val formUiState = _formUiState.asStateFlow()

    fun updateFormState(state: FormState) {
        _formUiState.value = state
    }

}

data class FormState(
    val name: String = "",
    val dosage: String = "",
    val recurrence: String = Recurrence.Daily.name,
    val endDate: Long? = System.currentTimeMillis(),
    val timesOfDay: TimesOfDay = TimesOfDay()
)

data class TimesOfDay(
    val isMorningSelected: Boolean = false,
    val isAfternoonSelected: Boolean = false,
    val isEveningSelected: Boolean = false,
    val isNightSelected: Boolean = false
)

enum class TimesOfDayNames {
    Morning,
    Afternoon,
    Evening,
    Night
}

enum class Recurrence {
    Daily,
    Weekly,
    Monthly
}

fun getRecurrenceList(): List<Recurrence> {
    val recurrenceList = mutableListOf<Recurrence>()
    recurrenceList.add(Recurrence.Daily)
    recurrenceList.add(Recurrence.Weekly)
    recurrenceList.add(Recurrence.Monthly)

    return recurrenceList
}