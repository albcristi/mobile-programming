package com.example.cityinyourpocket.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cityinyourpocket.R
import kotlinx.android.synthetic.main.city_list_item.view.*
import com.example.cityinyourpocket.model.Event

class CityListAdaptor(private val events: MutableList<Event>, private val onEventListener: OnEventListener): RecyclerView.Adapter<CityListAdaptor.CityListViewHolder>(){

    class CityListViewHolder(itemView: View, onEventListener: OnEventListener): RecyclerView.ViewHolder(itemView){
        val eventTitleView: TextView = itemView.eventNameListItem
        val eventCityView: TextView = itemView.locationCityListItem
        val eventHostView: TextView = itemView.hostNameCityListItem
        val onEventListener: OnEventListener = onEventListener

        init {
            itemView.setOnClickListener{
                onEventListener.onEventClickListener(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.city_list_item, parent, false);
        return CityListViewHolder(itemView, onEventListener)
    }

    override fun getItemCount(): Int {
        return events.size
    }
    
    override fun onBindViewHolder(holder: CityListViewHolder, position: Int) {
        val currentItem = events[position]
        holder.eventTitleView.text = "Event: ${currentItem.name}"
        holder.eventCityView.text = "Location: ${currentItem.location.city}"
        holder.eventHostView.text = "Hosted by @${currentItem.hostUserName}"
    }

    interface OnEventListener{
        fun  onEventClickListener(position: Int);
    }
}