package com.example.sapivirtualassistant.interfaces

import com.example.sapivirtualassistant.model.HelpModel

interface GetHelpModelInterface {
    fun getHelpModel(helpList: MutableList<HelpModel>)
}