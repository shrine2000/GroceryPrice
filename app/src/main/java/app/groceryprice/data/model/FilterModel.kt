package app.groceryprice.data.model

import app.groceryprice.utils.Filter

data class FilterModel(
    var filter: Filter,
    var filterBy: String
)