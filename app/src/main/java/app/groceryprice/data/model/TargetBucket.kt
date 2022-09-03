package app.groceryprice.data.model

import com.google.gson.annotations.SerializedName

data class TargetBucket(
    @SerializedName("field") var field: String? = null,
    @SerializedName("index") var index: String? = null,
    @SerializedName("type") var type: String? = null
)