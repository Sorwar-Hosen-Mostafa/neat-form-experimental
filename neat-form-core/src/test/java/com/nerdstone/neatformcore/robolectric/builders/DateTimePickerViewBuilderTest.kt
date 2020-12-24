package com.nerdstone.neatformcore.robolectric.builders

import android.view.View
import com.nerdstone.neatformcore.domain.model.NFormViewProperty
import com.nerdstone.neatformcore.utils.setupView
import com.nerdstone.neatformcore.views.builders.DateTimePickerViewBuilder
import com.nerdstone.neatformcore.views.widgets.DateTimePickerNFormView
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class DateTimePickerViewBuilderTest : BaseJsonViewBuilderTest() {

    private val viewProperty = spyk(NFormViewProperty())
    private val dateTimePickerNFormView = DateTimePickerNFormView(activity.get())
    private val dateTimePickerViewBuilder = spyk(
        objToCopy = DateTimePickerViewBuilder(dateTimePickerNFormView),
        recordPrivateCalls = true
    )

    @Before
    fun `Before doing anything else`() {
        viewProperty.name = "dob"
        viewProperty.type = "datetime_picker"
        //Set EditText properties and assign EditText view builder
        dateTimePickerNFormView.viewProperties = viewProperty
    }

    @Test
    fun `Should set hint on the datetime picker`() {
        val hint = "Am a hint"
        viewProperty.viewAttributes = hashMapOf("hint" to hint, "display_format" to "dd/MM/yyyy")
        dateTimePickerViewBuilder.buildView()
        Assert.assertTrue(
            dateTimePickerViewBuilder.textInputEditText.hint!!.isNotEmpty() &&
                    dateTimePickerViewBuilder.textInputEditText.hint.toString() == hint
        )
    }

    @Test
    fun `Should set required on datetime hint `() {
        val hint = "Am a required field"
        viewProperty.viewAttributes = hashMapOf("hint" to hint)
        viewProperty.requiredStatus = "yes:Am required"
        dateTimePickerViewBuilder.buildView()
        Assert.assertTrue(
            dateTimePickerViewBuilder.textInputEditText.hint!!.isNotEmpty() && dateTimePickerViewBuilder.textInputEditText.hint!!.endsWith(
                "*"
            )
        )
    }

    @Test
    fun `Should reset the datetime picker  value when visibility is gone`() {
        dateTimePickerNFormView.visibility = View.GONE
        Assert.assertTrue(dateTimePickerViewBuilder.textInputEditText.text.toString().isEmpty())
        Assert.assertNull(dateTimePickerNFormView.viewDetails.value)
    }

    @Test
    fun `Ensure that the right icon is added for date picker`() {
        val hint = "Am a hint"
        val type = "date_picker"
        viewProperty.viewAttributes =
            hashMapOf("hint" to hint, "display_format" to "dd/MM/yyyy", "type" to type)
        dateTimePickerViewBuilder.buildView()
        Assert.assertTrue(dateTimePickerViewBuilder.textInputEditText.compoundDrawablePadding == 8)
        val compoundDrawables = dateTimePickerViewBuilder.textInputEditText.compoundDrawables
        Assert.assertNotNull(compoundDrawables)

    }

    @Test
    fun `Ensure that the right icon is added for time picker`() {
        val hint = "Am a hint"
        val type = "time_picker"
        viewProperty.viewAttributes =
            hashMapOf("hint" to hint, "display_format" to "dd/MM/yyyy", "type" to type)
        dateTimePickerViewBuilder.buildView()
        Assert.assertTrue(dateTimePickerViewBuilder.textInputEditText.compoundDrawablePadding == 8)
        val compoundDrawables = dateTimePickerViewBuilder.textInputEditText.compoundDrawables
        Assert.assertNotNull(compoundDrawables)
    }

    @Test
    fun `Should launch date picker dialog, display formatted time and update view details value`() {
        val hint = "Am a hint"
        val type = "date_picker"
        viewProperty.viewAttributes =
            hashMapOf("hint" to hint, "display_format" to "dd/MM/yyyy", "type" to type)
        dateTimePickerViewBuilder.buildView()
        dateTimePickerViewBuilder.textInputEditText.performClick()
        dateTimePickerViewBuilder.onDateSet(spyk(), 2019, 0, 1)
        Assert.assertTrue(dateTimePickerViewBuilder.textInputEditText.text.toString() == "01/01/2019")
    }

    @Test
    fun `Should return date from passed pattern`() {
        val today = Calendar.getInstance()
        today[Calendar.HOUR_OF_DAY] = 12
        today[Calendar.MINUTE] = 0
        today[Calendar.SECOND] = 0
        today[Calendar.MILLISECOND] = 0

        Assert.assertEquals(today.timeInMillis, dateTimePickerViewBuilder.getDate("today"))

        today.add(Calendar.YEAR, 1)
        Assert.assertEquals(today.timeInMillis, dateTimePickerViewBuilder.getDate("today+1y"))

        val hint = "Am a hint"
        val type = "date_picker"
        viewProperty.viewAttributes =
            hashMapOf("hint" to hint, "display_format" to "dd/MM/yyyy", "type" to type, "min_date" to "today-80y", "max_date" to "today")
        dateTimePickerViewBuilder.buildView()
        verify { dateTimePickerViewBuilder.getDate(any()) }
    }

    @Test
    fun `Should launch time picker dialog, display formatted time and update view details value`() {
        val hint = "Am a hint"
        val type = "time_picker"
        viewProperty.viewAttributes =
            hashMapOf("hint" to hint, "display_format" to "hh:m a", "type" to type)
        dateTimePickerViewBuilder.buildView()
        dateTimePickerViewBuilder.textInputEditText.performClick()
        dateTimePickerViewBuilder.onTimeSet(spyk(), 11, 30)
        Assert.assertTrue(dateTimePickerViewBuilder.textInputEditText.text.toString() == "11:30 AM")
    }

    @Test
    fun `Should set value to the date picker when provided`() {
        dateTimePickerViewBuilder.buildView()
        val timestamp = 1589555422331
        dateTimePickerNFormView.setValue(timestamp)
        Assert.assertEquals(dateTimePickerNFormView.initialValue, timestamp)
        Assert.assertEquals(
            dateTimePickerNFormView.viewBuilder.selectedDate.timeInMillis, 1589555422331
        )
        Assert.assertEquals(
            dateTimePickerNFormView.viewBuilder.textInputEditText.text.toString(), "2020-05-15"
        )
    }

    @Test
    fun `Should reset date pickers value`() {
        dateTimePickerViewBuilder.buildView()
        dateTimePickerViewBuilder.resetDatetimePickerValue()
        Assert.assertTrue(dateTimePickerViewBuilder.textInputEditText.text.isNullOrEmpty())
    }

    @Test
    fun `Should validate the date picker widget`() {
        dateTimePickerNFormView.setupView(viewProperty, formBuilder)
        dateTimePickerNFormView.viewDetails.value = 1589555422331
        Assert.assertTrue(dateTimePickerNFormView.validateValue())
    }

    @After
    fun `After everything else`() {
        unmockkAll()
    }
}