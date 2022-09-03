package app.groceryprice.data.model

import com.google.gson.annotations.SerializedName

data class Records(
    @SerializedName("state") var state: String? = null,
    @SerializedName("district") var district: String? = null,
    @SerializedName("market") var market: String? = null,
    @SerializedName("commodity") var commodity: String? = null,
    @SerializedName("variety") var variety: String? = null,
    @SerializedName("arrival_date") var arrivalDate: String? = null,
    @SerializedName("min_price") var minPrice: String? = null,
    @SerializedName("max_price") var maxPrice: String? = null,
    @SerializedName("modal_price") var modalPrice: String? = null
)