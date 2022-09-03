package app.groceryprice.data.model

import com.google.gson.annotations.SerializedName

data class Field(
    @SerializedName("name") var name: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("type") var type: String? = null
)