package com.shryne.colortable

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * All offers of an item in bricklink.
 *
 * This class is necessary because Unirest doesn't seem to be able to
 * deserialize into JSON and then into the list of offers. Additionally,
 * Jackson fails deserialization when this class implements the [List]
 * interface.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Offers(
    @JsonProperty("list")
    val offers: MutableList<Offer>
)
