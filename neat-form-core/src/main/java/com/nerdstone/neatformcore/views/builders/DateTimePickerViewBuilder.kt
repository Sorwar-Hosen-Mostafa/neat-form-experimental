package com.nerdstone.neatformcore.views.builders

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import com.google.android.material.textfield.TextInputEditText
import com.nerdstone.neatformcore.R
import com.nerdstone.neatformcore.domain.builders.ViewBuilder
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.utils.*
import com.nerdstone.neatformcore.views.widgets.DateTimePickerNFormView
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import java.util.regex.Pattern

private const val TODAY = "today"

open class DateTimePickerViewBuilder(final override val nFormView: NFormView) :
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, ViewBuilder {

    private var dateDisplayFormat = "yyyy-MM-dd"
    private var simpleDateFormat = SimpleDateFormat(dateDisplayFormat, Locale.getDefault())
    private var minDate: Long? = null
    private var maxDate: Long? = null
    private val dateTimePickerNFormView = nFormView as DateTimePickerNFormView
    override val acceptedAttributes = DateTimePickerProperties::class.java.convertEnumToSet()
    override lateinit var resourcesMap: MutableMap<String, Int>

    private val calendar = getInstance()
    val selectedDate: Calendar = getInstance()
    val textInputEditText = TextInputEditText(dateTimePickerNFormView.context)

    enum class DateTimePickerProperties {
        HINT, TYPE, DISPLAY_FORMAT, MIN_DATE, MAX_DATE
    }

    private object DatePickerType {
        const val DATE_PICKER = "date_picker"
        const val TIME_PICKER = "time_picker"
    }

    override fun buildView() {
        dateTimePickerNFormView.apply {
            applyViewAttributes(
                acceptedAttributes, this@DateTimePickerViewBuilder::setViewProperties
            )
            addView(textInputEditText)
        }
        textInputEditText.apply {
            compoundDrawablePadding = 8
            isFocusable = false
        }

    }

    override fun setViewProperties(attribute: Map.Entry<String, Any>) {
        textInputEditText.apply {
            when (attribute.key.toUpperCase(Locale.getDefault())) {
                DateTimePickerProperties.HINT.name -> {
                    hint = attribute.value.toString()
                    formatHintForRequiredFields()
                }
                DateTimePickerProperties.TYPE.name -> {
                    when {
                        attribute.value.toString() == DatePickerType.DATE_PICKER -> {
                            textInputEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_today, 0, 0, 0
                            )
                            textInputEditText.setOnClickListener {
                                launchDatePickerDialog()
                            }
                        }
                        attribute.value.toString() == DatePickerType.TIME_PICKER -> {
                            textInputEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                R.drawable.ic_schedule, 0, 0, 0
                            )
                            textInputEditText.setOnClickListener {
                                launchTimePickerDialog()
                            }
                        }
                    }
                }
                DateTimePickerProperties.DISPLAY_FORMAT.name -> {
                    dateDisplayFormat = attribute.value.toString()
                    simpleDateFormat = SimpleDateFormat(dateDisplayFormat, Locale.getDefault())
                }
                DateTimePickerProperties.MIN_DATE.name -> {
                    minDate = when {
                        attribute.value.toString().contains(TODAY) -> {
                            getDate(attribute.value.toString())
                        }
                        else -> {
                            setDate(attribute)
                        }
                    }
                }
                DateTimePickerProperties.MAX_DATE.name -> {
                    maxDate = when {
                        attribute.value.toString().contains(TODAY) -> {
                            getDate(attribute.value.toString())
                        }
                        else -> {
                            setDate(attribute)
                        }
                    }

                }
            }
        }
    }

    private fun setDate(attribute: Map.Entry<String, Any>) =
        simpleDateFormat.parse(attribute.value.toString())!!.time

    private fun formatHintForRequiredFields() {
        with(dateTimePickerNFormView) {
            if (!viewProperties.requiredStatus.isNullOrBlank() && isFieldRequired()) {
                textInputEditText.hint = textInputEditText.hint.toString().addRedAsteriskSuffix()
            }
        }
    }

    private fun launchDatePickerDialog() {
        val year = selectedDate.get(YEAR)
        val month = selectedDate.get(MONTH)
        val dayOfMonth = selectedDate.get(DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(
                nFormView.viewDetails.getViewContext(), this, year, month, dayOfMonth
            )

        minDate?.let { datePickerDialog.datePicker.minDate = this.minDate!! }
        maxDate?.let { datePickerDialog.datePicker.maxDate = this.maxDate!! }
        datePickerDialog.show()

    }

    private fun launchTimePickerDialog() {
        val hour = selectedDate.get(HOUR_OF_DAY)
        val minute = selectedDate.get(MINUTE)
        val isTwentyFourHour = false
        val timePickerDialog =
            TimePickerDialog(dateTimePickerNFormView.context, this, hour, minute, isTwentyFourHour)
        timePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedDate.set(year, month, dayOfMonth)
        updateViewData()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selectedDate.set(
            calendar.get(YEAR), calendar.get(MONTH), calendar.get(DAY_OF_MONTH), hourOfDay, minute
        )
        updateViewData()
    }

    fun updateViewData() {
        textInputEditText.setText(simpleDateFormat.format(Date(selectedDate.timeInMillis)))
        dateTimePickerNFormView.viewDetails.value = selectedDate.timeInMillis
        dateTimePickerNFormView.dataActionListener?.onPassData(dateTimePickerNFormView.viewDetails)
    }

    fun resetDatetimePickerValue() {
        textInputEditText.setText("")
        dateTimePickerNFormView.viewDetails.value = null
        dateTimePickerNFormView.dataActionListener?.onPassData(dateTimePickerNFormView.viewDetails)
    }

    /**
     * This method returns a [Calendar] object at mid-day corresponding to a date matching the
     * format specified in `DATE_FORMAT` or a day in reference to today e.g today, today-1,
     * today+10
     *
     * @param day The string to be converted to a date
     * @return The calendar object corresponding to the day, or object corresponding to today's date
     * if an error occurred
     */
    open fun getDate(day: String?): Long? {
        val calendarDate = getInstance()
        if (day != null && day.trim { it <= ' ' }.isNotEmpty()) {
            val dayString = day.trim { it <= ' ' }.toLowerCase(Locale.getDefault())
            if (TODAY != dayString) {
                val pattern =
                    Pattern.compile("$TODAY\\s*([-\\+])\\s*(\\d+)([dmywDMYW]{1})")
                val matcher = pattern.matcher(dayString)
                if (matcher.find()) {
                    var timeValue = matcher.group(2)!!.toInt()
                    if ("-" == matcher.group(1)) {
                        timeValue *= -1
                    }
                    var field = DATE
                    when {
                        matcher.group(3).equals("y", ignoreCase = true) -> {
                            field = YEAR
                        }
                        matcher.group(3).equals("m", ignoreCase = true) -> {
                            field = MONTH
                        }
                        matcher.group(3).equals("w", ignoreCase = true) -> {
                            field = WEEK_OF_MONTH
                        }
                        matcher.group(3).equals("d", ignoreCase = true) -> {
                            field = DAY_OF_MONTH
                        }
                    }
                    calendarDate.add(field, timeValue)
                } else {
                    try {
                        calendarDate.time = simpleDateFormat.parse(dayString)!!
                    } catch (e: ParseException) {
                        Timber.e(e)
                    }
                }
            }
        }

        //set time to mid-day
        calendarDate[HOUR_OF_DAY] = 12
        calendarDate[MINUTE] = 0
        calendarDate[SECOND] = 0
        calendarDate[MILLISECOND] = 0
        return calendarDate.timeInMillis
    }
}