package com.shryne.colortable

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * All offers of an item in bricklink.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Offers(
    @JsonProperty("total_count")
    private val total_count: Int,
    private val idColor: Int,
    @JsonProperty("list")
    private val offers: MutableList<Offer>
)//: List<Offer> by offers
