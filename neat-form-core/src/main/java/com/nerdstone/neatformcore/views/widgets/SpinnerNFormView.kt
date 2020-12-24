package com.nerdstone.neatformcore.views.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.nerdstone.neatformcore.domain.listeners.DataActionListener
import com.nerdstone.neatformcore.domain.listeners.VisibilityChangeListener
import com.nerdstone.neatformcore.domain.model.NFormViewData
import com.nerdstone.neatformcore.domain.model.NFormViewDetails
import com.nerdstone.neatformcore.domain.model.NFormViewProperty
import com.nerdstone.neatformcore.domain.view.FormValidator
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.utils.VALUE
import com.nerdstone.neatformcore.utils.handleRequiredStatus
import com.nerdstone.neatformcore.utils.setReadOnlyState
import com.nerdstone.neatformcore.views.builders.SpinnerViewBuilder
import com.nerdstone.neatformcore.views.handlers.ViewVisibilityChangeHandler

class SpinnerNFormView : LinearLayout, NFormView {

    override lateinit var viewProperties: NFormViewProperty
    override lateinit var formValidator: FormValidator
    override var dataActionListener: DataActionListener? = null
    override val viewBuilder = SpinnerViewBuilder(this)
    override var viewDetails = NFormViewDetails(this)
    override var visibilityChangeListener: VisibilityChangeListener? = ViewVisibilityChangeHandler
    override var initialValue: Any? = null

    init {
        orientation = VERTICAL
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun trackRequiredField() = handleRequiredStatus()

    override fun resetValueWhenHidden() = viewBuilder.resetSpinnerValue()

    override fun setValue(value: Any, enabled: Boolean) {
        initialValue = value
        viewBuilder.materialSpinner.apply {
            when (value) {
                is Map<*, *> -> {
                    setSelection(item.indexOf(value[VALUE]), false)
                }
                is NFormViewData -> {
                    setSelection(item.indexOf(value.value as String), false)
                }
            }
            setReadOnlyState(enabled)
        }
    }

    override fun validateValue() = formValidator.validateField(this).first

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        visibilityChangeListener?.onVisibilityChanged(this, visibility)
    }
}