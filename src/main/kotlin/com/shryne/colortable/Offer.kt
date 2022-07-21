package com.shryne.colortable

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * An offer of an item in bricklink.
 *
 * @param condition The condition of the item.
 * @param price The price of the item.
 * @param sellersCountry The country where the seller is located.
 * @param quantity The offered quantity of the item.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Offer(
    @JsonProperty("codeNew")
    val condition: Condition,
    @JsonProperty("mDisplaySalePrice")
    val price: String,
    @JsonProperty("strSellerCountryName")
    val sellersCountry: String,
    @JsonProperty("n4Qty")
    val quantity: Int
) {
    /**
     * The condition of the item.
     */
    enum class Condition {
        @JsonProperty("N")
        NEW,
        @JsonProperty("U")
        USED
    }
}