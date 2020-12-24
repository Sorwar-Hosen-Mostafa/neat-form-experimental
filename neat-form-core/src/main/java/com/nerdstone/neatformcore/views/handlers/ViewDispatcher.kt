package com.nerdstone.neatformcore.views.handlers

import android.view.View
import com.nerdstone.neatformcore.domain.listeners.DataActionListener
import com.nerdstone.neatformcore.domain.model.NFormViewData
import com.nerdstone.neatformcore.domain.model.NFormViewDetails
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.rules.RulesFactory
import com.nerdstone.neatformcore.utils.getDataViewModel
import com.nerdstone.neatformcore.utils.isNotNull

/**
 * @author Elly Nerdstone
 *
 * This class provides a listener method that is called when a field's value
 * has been changed. Say for instance when the text of an EditText has been updated the listener
 * will be triggered. At this point the fields value can be validated and then the rules are fired
 * for the fields watching on the field that has passed its value.
 *
 * The rules are handled by the [RulesFactory] class and the validation
 */
class ViewDispatcher(val rulesFactory: RulesFactory) : DataActionListener {

    /**
     * Dispatches an action when view value changes. If value is the same as what had already been
     * dispatched then do nothing
     *  [viewDetails] are the details of the view that has just dispatched a value
     */
    override fun onPassData(viewDetails: NFormViewDetails) {
        val formData = viewDetails.getDataViewModel().details
        with(viewDetails) {
            if (value.isNotNull()) {
                formData.value?.also { details ->
                    if (details[name] != value) {
                        details[name] =
                            NFormViewData(
                                type = view.javaClass.simpleName, value = value,
                                metadata = metadata, visible = view.visibility == View.VISIBLE
                            )

                        val nFormView = view as NFormView
                        nFormView.validateValue()

                        //Fire rules for calculations and other fields watching on this current field
                        val calculations = (view as NFormView).viewProperties.calculations
                        if (rulesFactory.subjectsRegistry.containsKey(name.trim()) || calculations.isNotNull()) {
                            rulesFactory.updateFactsAndExecuteRules(this)
                        }
                    }
                }
            }
        }
    }
}
