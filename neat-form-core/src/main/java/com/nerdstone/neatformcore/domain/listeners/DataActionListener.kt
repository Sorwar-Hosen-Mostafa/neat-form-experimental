package com.nerdstone.neatformcore.domain.listeners

import com.nerdstone.neatformcore.domain.model.NFormViewDetails

interface DataActionListener {

    fun onPassData(viewDetails: NFormViewDetails)

}
