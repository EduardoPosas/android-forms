package com.example.composeformandvalidation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeformandvalidation.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val TAG = "STATE"

@SuppressLint("SimpleDateFormat")
@Composable
fun MedicationForm(
    modifier: Modifier = Modifier,
    viewModel: MedicationFormViewModel = viewModel(),
) {

    val formState by viewModel.formUiState.collectAsState()
    Log.i(TAG, formState.toString())

    var openDateDialog by rememberSaveable {
        mutableStateOf(false)
    }


    // Convert milliseconds to LocalDateTime
    val localDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(formState.endDate!!), ZoneOffset.UTC)
    // Specify the target time zone
    val targetTimeZone = ZoneId.of("America/Mexico_City")
    // Convert LocalDateTime to ZonedDateTime in the target time zone
    val zonedDateTime = ZonedDateTime.of(localDateTime, targetTimeZone)
    // Format the date in the target time zone
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDate = zonedDateTime.format(formatter)
    val initialDate = zonedDateTime.toInstant().toEpochMilli() // back to millis

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.add_medication),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        FormTextField(
            value = formState.name,
            onValueChange = { viewModel.updateFormState(formState.copy(name = it)) },
            label = { Text(text = "Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormTextField(
                value = formState.dosage,
                onValueChange = {
                    viewModel.updateFormState(formState.copy(dosage = it))
                },
                label = { Text(text = "Dosage") },
                modifier = Modifier.weight(2F),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            DropDownMenu(
                options = getRecurrenceList(),
                onClick = { viewModel.updateFormState(formState.copy(recurrence = it)) },
                modifier = Modifier.weight(3F),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FormTextField(
                value = formattedDate,
                onValueChange = { },
                modifier = Modifier.weight(2F),
                label = { Text(text = "End Date") },
                readOnly = true,
                enabled = false
            )
            OutlinedButtonWithTrailingIcon(
                onClick = {
                    openDateDialog = true
                },
                icon = Icons.Default.DateRange,
                modifier = Modifier.weight(1F)
            )
        }

        if (openDateDialog) {
            FormDatePickerDialog(
                onOpenDialog = {
                    openDateDialog = false
                },
                initialSelectedDateMillis = initialDate,
                onSelectedDate = {
                    viewModel.updateFormState(
                        formState.copy(
                            endDate = it
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        FilterChipGroup(
            formState = formState,
            updateFormState = viewModel::updateFormState,
            context = context
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Save",
                style = MaterialTheme.typography.bodyLarge
            )
        }

    }
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit) = {},
    colors: TextFieldColors? = null,
    enabled: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = label,
        modifier = modifier,
        singleLine = true,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodySmall,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        colors = colors ?: TextFieldDefaults.colors(),
        enabled = enabled
    )
}

@Composable
fun OutlinedButtonWithTrailingIcon(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Select")
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFilterChip(
    selected: Boolean,
    onToggle: () -> Unit,
    label: @Composable () -> Unit,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onToggle,
        modifier = modifier,
        label = label,
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    options: List<Recurrence>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val optionList = options.map { it.name }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableStateOf(optionList[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        FormTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(text = "Recurrence") },
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            optionList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        selectedOption = option
                        onClick(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDatePickerDialog(
    onOpenDialog: () -> Unit,
    initialSelectedDateMillis: Long?,
    onSelectedDate: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        //initialDisplayMode = DisplayMode.Input
    )
    val confirmEnabled = remember {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    DatePickerDialog(
        onDismissRequest = onOpenDialog,
        confirmButton = {
            TextButton(
                onClick = {
                    onOpenDialog()
                    onSelectedDate(
                        datePickerState.selectedDateMillis!!
                    )
                },
                enabled = confirmEnabled.value
            ) {
                Text(text = "OK")
            }
        },
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(
                onClick = onOpenDialog,
            ) {
                Text(text = "Cancel")
            }
        },
        //shape = RoundedCornerShape(8.dp)
    ) {
        FormDatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = "End Date",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDatePicker(
    state: DatePickerState,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    DatePicker(
        state = state,
        modifier = modifier,
        title = title,
        headline = {
            DatePickerDefaults.DatePickerHeadline(
                state = state,
                dateFormatter = DatePickerFormatter(
                    selectedDateSkeleton = "MMM d y"
                ),
                modifier = Modifier.padding(16.dp)
            )
        },
    )
}

@Composable
fun FilterChipGroup(
    formState: FormState,
    updateFormState: (FormState) -> Unit,
    context: Context
) {

    var selectionCount by remember { mutableIntStateOf(0) }

    Text(
        text = "Times of Day",
        style = MaterialTheme.typography.bodyLarge
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormFilterChip(
            selected = formState.timesOfDay.isMorningSelected,
            onToggle = {
                handleChipFilterChange(
                    isSelected = formState.timesOfDay.isMorningSelected,
                    selectionCount = selectionCount,
                    onStateChange = {
                        selectionCount = it
                        updateFormState(
                            formState.copy(
                                timesOfDay = formState.timesOfDay.copy(
                                    isMorningSelected = !formState.timesOfDay.isMorningSelected
                                )
                            )
                        )
                    },
                    canSelectMoreTimesOfDay = canSelectMoreTimesOfDay(
                        selectionCount = selectionCount,
                        numberOfDosage = formState.dosage.toIntOrNull() ?: 0
                    ),
                    onShowMaxSelectionError = {
                        Toast.makeText(
                            context,
                            "You're selecting ${(formState.dosage.toIntOrNull() ?: 0) + 1} time(s) of days which is more than the number of dosage.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            label = { Text(text = TimesOfDayNames.Morning.name) },
            leadingIcon = Icons.Default.Done,
            modifier = Modifier.weight(1F)
        )
        FormFilterChip(
            selected = formState.timesOfDay.isAfternoonSelected,
            onToggle = {
                handleChipFilterChange(
                    isSelected = formState.timesOfDay.isAfternoonSelected,
                    selectionCount = selectionCount,
                    onStateChange = {
                        selectionCount = it
                        updateFormState(
                            formState.copy(
                                timesOfDay = formState.timesOfDay.copy(
                                    isAfternoonSelected = !formState.timesOfDay.isAfternoonSelected
                                )
                            )
                        )
                    },
                    canSelectMoreTimesOfDay = canSelectMoreTimesOfDay(
                        selectionCount = selectionCount,
                        numberOfDosage = formState.dosage.toIntOrNull() ?: 0
                    ),
                    onShowMaxSelectionError = {
                        Toast.makeText(
                            context,
                            "You're selecting ${(formState.dosage.toIntOrNull() ?: 0) + 1} time(s) of days which is more than the number of dosage.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            label = { Text(text = TimesOfDayNames.Afternoon.name) },
            leadingIcon = Icons.Default.Done,
            modifier = Modifier.weight(1F)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormFilterChip(
            selected = formState.timesOfDay.isEveningSelected,
            onToggle = {
                handleChipFilterChange(
                    isSelected = formState.timesOfDay.isEveningSelected,
                    selectionCount = selectionCount,
                    onStateChange = {
                        selectionCount = it
                        updateFormState(
                            formState.copy(
                                timesOfDay = formState.timesOfDay.copy(
                                    isEveningSelected = !formState.timesOfDay.isEveningSelected
                                )
                            )
                        )
                    },
                    canSelectMoreTimesOfDay = canSelectMoreTimesOfDay(
                        selectionCount = selectionCount,
                        numberOfDosage = formState.dosage.toIntOrNull() ?: 0
                    ),
                    onShowMaxSelectionError = {
                        Toast.makeText(
                            context,
                            "You're selecting ${(formState.dosage.toIntOrNull() ?: 0) + 1} time(s) of days which is more than the number of dosage.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            label = { Text(text = TimesOfDayNames.Evening.name) },
            leadingIcon = Icons.Default.Done,
            modifier = Modifier.weight(1F)
        )
        FormFilterChip(
            selected = formState.timesOfDay.isNightSelected,
            onToggle = {
                handleChipFilterChange(
                    isSelected = formState.timesOfDay.isNightSelected,
                    selectionCount = selectionCount,
                    onStateChange = {
                        selectionCount = it
                        updateFormState(
                            formState.copy(
                                timesOfDay = formState.timesOfDay.copy(
                                    isNightSelected = !formState.timesOfDay.isNightSelected
                                )
                            )
                        )
                    },
                    canSelectMoreTimesOfDay = canSelectMoreTimesOfDay(
                        selectionCount = selectionCount,
                        numberOfDosage = formState.dosage.toIntOrNull() ?: 0
                    ),
                    onShowMaxSelectionError = {
                        Toast.makeText(
                            context,
                            "You're selecting ${(formState.dosage.toIntOrNull() ?: 0) + 1} time(s) of days which is more than the number of dosage.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            },
            label = { Text(text = TimesOfDayNames.Night.name) },
            leadingIcon = Icons.Default.Done,
            modifier = Modifier.weight(1F)
        )
    }
}

private fun handleChipFilterChange(
    isSelected: Boolean,
    selectionCount: Int,
    onStateChange: (Int) -> Unit,
    canSelectMoreTimesOfDay: Boolean,
    onShowMaxSelectionError: () -> Unit
) {
    if (isSelected) {
        onStateChange(selectionCount - 1)
    } else {
        if (canSelectMoreTimesOfDay) {
            onStateChange(selectionCount + 1)
        } else {
            onShowMaxSelectionError()
        }
    }
}

private fun canSelectMoreTimesOfDay(selectionCount: Int, numberOfDosage: Int): Boolean {
    return selectionCount < numberOfDosage
}

@Preview(showBackground = true)
@Composable
fun MedicationFormPreview() {
    MedicationForm()
}