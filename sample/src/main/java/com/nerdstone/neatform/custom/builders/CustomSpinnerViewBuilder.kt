package com.nerdstone.neatform.custom.builders

import androidx.core.content.ContextCompat
import com.nerdstone.neatform.R
import com.nerdstone.neatform.custom.views.CustomSpinnerView
import com.nerdstone.neatformcore.domain.builders.ViewBuilder
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.utils.applyViewAttributes
import com.nerdstone.neatformcore.utils.convertEnumToSet
import com.nerdstone.neatformcore.views.builders.SpinnerViewBuilder
import java.util.*

class CustomSpinnerViewBuilder(override val nFormView: NFormView) : ViewBuilder {

    override val acceptedAttributes = SpinnerViewBuilder.SpinnerProperties::class.java.convertEnumToSet()

    override lateinit var resourcesMap: MutableMap<String, Int>

    private val customSpinnerView = nFormView as CustomSpinnerView

    override fun buildView() {
        customSpinnerView.setBackgroundColor(ContextCompat.getColor(customSpinnerView.context, R.color.colorPurple))
        nFormView.applyViewAttributes(acceptedAttributes, this::setViewProperties)
    }

    override fun setViewProperties(attribute: Map.Entry<String, Any>) {
        customSpinnerView.setBackgroundColor(ContextCompat.getColor(customSpinnerView.context, R.color.colorPurple))
    }
}