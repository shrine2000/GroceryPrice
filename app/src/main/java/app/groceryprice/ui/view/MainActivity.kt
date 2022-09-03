package app.groceryprice.ui.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.groceryprice.data.model.FilterModel
import app.groceryprice.data.model.Records
import app.groceryprice.databinding.ActivityMainBinding
import app.groceryprice.ui.adapter.RecyclerViewAdapter
import app.groceryprice.ui.viewmodel.ViewModel
import app.groceryprice.utils.Constants.Companion.API_KEY
import app.groceryprice.utils.Constants.Companion.FORMAT
import app.groceryprice.utils.Constants.Companion.LIMIT
import app.groceryprice.utils.Constants.Companion.OFFSET
import app.groceryprice.utils.Filter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var newRecords: ArrayList<Records> = arrayListOf()
    var offset = 0
    var filterModel = FilterModel(Filter.NIL, "")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        observeViewModel()
        viewModel.refresh(apiKey = API_KEY, format = FORMAT, OFFSET, LIMIT.toString(), filterModel)

        filterByDistrict()
    }


    private fun setupRecyclerView() {
        recyclerViewAdapter = RecyclerViewAdapter(arrayListOf())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            addOnScrollListener(this@MainActivity.scrollListener)
        }
    }

    private fun observeViewModel() {
        viewModel.govLoading.observe(this) { data ->
            data.let { isLoading ->
                if (isLoading) showProgressBar()
            }
        }

        viewModel.govLoadError.observe(this) { data ->
            data.let { isError ->
                Toast.makeText(this@MainActivity, isError.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.govRecord.observe(this) { data ->
            data?.let { records ->
                hideProgressBar()
                newRecords.addAll(records)
                recyclerViewAdapter.updateRecords(records)
            }
        }
    }

    @SuppressLint("CheckResult", "NotifyDataSetChanged")
    private fun filterByDistrict() {

        binding.imgCloseFilter.setOnClickListener {
            closeFilter()
        }
        binding.imgCloseFilter.visibility = View.GONE
        binding.btnFilter.setOnClickListener {
            MaterialDialog(this@MainActivity).show {
                title(text = "Filter by")
                listItems(items = listOf("Market", "Districts")) { dialog, index, text ->


                    when (index) {
                        0 -> {
                            MaterialDialog(this@MainActivity).show {
                                listItems(items = newRecords.distinctBy { it.market }
                                    .map { it.market.toString() }.toList()) { dialog, index, text ->

                                    filterModel = FilterModel(
                                        filter = Filter.MARKET,
                                        filterBy = text.toString()
                                    )

                                    binding.tvFilterBy.text = text.toString()
                                    binding.imgCloseFilter.visibility = View.VISIBLE

                                    recyclerViewAdapter.records.clear()

                                    viewModel.refresh(
                                        apiKey = API_KEY,
                                        format = FORMAT,
                                        offset = 1,
                                        limit = "10",
                                        filterModel = filterModel
                                    )
                                }
                            }
                        }
                        1 -> {
                            MaterialDialog(this@MainActivity).show {
                                listItems(items = newRecords.distinctBy { it.district }
                                    .map { it.district.toString() }
                                    .toList()) { dialog, index, text ->

                                    filterModel = FilterModel(
                                        filter = Filter.DISTRICT,
                                        filterBy = text.toString()
                                    )
                                    binding.tvFilterBy.text = text.toString()
                                    binding.imgCloseFilter.visibility = View.VISIBLE

                                    recyclerViewAdapter.records.clear()
                                    viewModel.refresh(
                                        apiKey = API_KEY,
                                        format = FORMAT,
                                        offset = 1,
                                        limit = "10",
                                        filterModel = filterModel
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.btnSort.setOnClickListener {
            MaterialDialog(this@MainActivity).show {
                title(text = "Sort by")
                listItems(
                    items = listOf(
                        "Price: Low to High (minPrice)",
                        "Price: High to Low (minPrice)",
                        "Newest Arrivals"
                    )
                ) { dialog, index, text ->
                    when (index) {
                        0 -> {
                            recyclerViewAdapter.records.sortBy { it.minPrice?.toInt() }
                            recyclerViewAdapter.notifyDataSetChanged()

                        }
                        1 -> {
                            recyclerViewAdapter.records.sortByDescending { it.minPrice?.toInt() }
                            recyclerViewAdapter.notifyDataSetChanged()
                        }
                        2 -> {
                            recyclerViewAdapter.records.sortBy { it.arrivalDate?.toLocalDate() }
                            recyclerViewAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }


    private fun closeFilter() {
        recyclerViewAdapter.records.clear()

        filterModel = FilterModel(
            filter = Filter.NIL,
            filterBy = ""
        )
        binding.imgCloseFilter.visibility = View.GONE
        binding.tvFilterBy.text = ""
        offset = 0
        viewModel.refresh(apiKey = API_KEY, FORMAT, OFFSET, LIMIT.toString(), filterModel)
    }


    fun String.toLocalDate(): Date? {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.parse(this)
    }


    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= 10
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                offset += 10
                viewModel.refresh(apiKey = API_KEY, FORMAT, offset, "10", filterModel)
                isScrolling = false
            } else {
                binding.recyclerView.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}


