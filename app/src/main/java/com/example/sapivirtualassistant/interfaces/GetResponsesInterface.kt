package com.example.sapivirtualassistant.interfaces

interface GetResponsesInterface {
    fun getResponses(responseMap: Map<String,Map<String, List<String>>>)
}