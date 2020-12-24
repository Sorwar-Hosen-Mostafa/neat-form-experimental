package com.nerdstone.neatformcore.views.containers

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.nerdstone.neatformcore.domain.listeners.DataActionListener
import com.nerdstone.neatformcore.domain.listeners.VisibilityChangeListener
import com.nerdstone.neatformcore.domain.model.NFormViewDetails
import com.nerdstone.neatformcore.domain.model.NFormViewProperty
import com.nerdstone.neatformcore.domain.view.FormValidator
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.utils.handleRequiredStatus
import com.nerdstone.neatformcore.views.builders.RadioGroupViewBuilder
import com.nerdstone.neatformcore.views.handlers.ViewVisibilityChangeHandler

class RadioGroupView : LinearLayout, NFormView {

    override lateinit var viewProperties: NFormViewProperty
    override lateinit var formValidator: FormValidator
    override var dataActionListener: DataActionListener? = null
    override var visibilityChangeListener: VisibilityChangeListener? =
        ViewVisibilityChangeHandler
    override val viewBuilder = RadioGroupViewBuilder(this)
    override val viewDetails = NFormViewDetails(this)
    override var initialValue: Any? = null

    init {
        orientation = VERTICAL
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun resetValueWhenHidden() {
        viewBuilder.resetRadioButtonsValue()
    }

    override fun trackRequiredField() = handleRequiredStatus()

    override fun validateValue(): Boolean =
        formValidator.validateLabeledField(this)

    override fun setValue(value: Any, enabled: Boolean) {
        initialValue = value
        if (value is Map<*, *>) {
            viewBuilder.setValue(value.keys.first() as String, enabled)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        visibilityChangeListener?.onVisibilityChanged(this, visibility)
    }
}