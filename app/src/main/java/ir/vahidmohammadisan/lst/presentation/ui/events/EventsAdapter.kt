package ir.vahidmohammadisan.lst.presentation.ui.events

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.vahidmohammadisan.lst.R
import ir.vahidmohammadisan.lst.data.remote.dto.Event
import ir.vahidmohammadisan.lst.databinding.ItemLayoutBinding
import ir.vahidmohammadisan.lst.utils.Constants.TIME_FORMAT
import ir.vahidmohammadisan.lst.utils.toDateString

class EventsAdapter(
    itemList: List<Event>,
    private var eventClickListener: EventClickListener,
    private var context: Context
) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private var items = itemList

    class ViewHolder(
        private val context: Context,
        private val adapter: EventsAdapter,
        private val binding: ItemLayoutBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Event) {
            binding.category.text = item.category
            binding.state.text = item.state
            binding.title.text = item.title
            binding.desc.text = item.description
            binding.start.text = item.start.toLong().toDateString(TIME_FORMAT)
            binding.end.text = item.end.toLong().toDateString(TIME_FORMAT)
            binding.country.text = item.country
            binding.isPrivate.text = item.isPrivate.toString()

            Glide.with(context)
                .load(item.image)
                .into(binding.eventImage);

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    adapter.eventClickListener.onEventClick(item)
                }
            }
            if (item.state == "Active") {
                binding.state.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_green_700
                    )
                )
            } else {
                binding.state.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.md_grey_900
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(context, this, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(events: List<Event>) {
        items = events
        notifyDataSetChanged()
    }

    interface EventClickListener {
        fun onEventClick(event: Event)
    }

}
