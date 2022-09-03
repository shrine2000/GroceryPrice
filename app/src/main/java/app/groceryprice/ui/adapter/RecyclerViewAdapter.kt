package app.groceryprice.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.groceryprice.data.model.Records
import app.groceryprice.databinding.LayoutRvItemBinding

class RecyclerViewAdapter(var records: ArrayList<Records>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    fun updateRecords(newRecords: List<Records>) {
        //  records.clear()
        records.addAll(newRecords)
        notifyDataSetChanged()


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewAdapter.ViewHolder {
        val itemBinding =
            LayoutRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        val records: Records = records[position]
        holder.bind(records)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    class ViewHolder(private val binding: LayoutRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(records: Records) {
            binding.tvDate.text = records.arrivalDate
            binding.tvCommodity.text = records.commodity
            binding.tvDistrict.text = "District - " + records.district
            binding.tvPrice.text = records.minPrice + " INR"
            binding.tvVillage.text = "Market - " + records.market

        }
    }
}