package app.groceryprice.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.groceryprice.data.api.GovService
import app.groceryprice.data.model.FilterModel
import app.groceryprice.data.model.Records
import app.groceryprice.utils.Constants
import app.groceryprice.utils.Filter

import kotlinx.coroutines.*


class ViewModel : ViewModel() {
    private val govService = GovService().getGovService()
    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val govRecord = MutableLiveData<List<Records>>()
    val govLoadError = MutableLiveData<String>()
    val govLoading = MutableLiveData<Boolean>()


    fun refresh(
        apiKey: String = Constants.API_KEY,
        format: String = Constants.FORMAT,
        offset: Int = 0,
        limit: String = "20",
        filterModel: FilterModel

    ) {
        fetchUsers(apiKey, format, offset, limit, filterModel)
    }


    private fun fetchUsers(
        apiKey: String,
        format: String,
        offset: Int,
        limit: String,
        filterModel: FilterModel
    ) {
        govLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = when (filterModel.filter) {
                Filter.MARKET -> {
                    govService.getFilteredListByMarket(
                        apiKey = apiKey,
                        format = format,
                        offset = offset,
                        limit = limit,
                        filterBy = filterModel.filterBy
                    )
                }
                Filter.DISTRICT -> {
                    govService.getFilteredListByDistrict(
                        apiKey = apiKey,
                        format = format,
                        offset = offset,
                        limit = limit,
                        filterBy = filterModel.filterBy

                    )
                }
                else ->
                    govService.getList(
                        apiKey = apiKey,
                        format = format,
                        offset = offset,
                        limit = limit
                    )

            }

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    govRecord.value =
                        response.body()?.records

                    response.body()?.records?.forEach {
                        Log.v("records 132", it.toString())

                    }
                    govLoading.value = false
                } else {
                    onError("Error : ${response.message()} ")
                    govLoading.value = false
                }
            }
        }
    }

    private fun onError(message: String) {
        govLoadError.value = message
        govLoading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}