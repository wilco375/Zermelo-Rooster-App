package com.wilco375.roosternotification.general

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class AutoCompleteStringAdapter(context: Context, var items: List<String>) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, items) {
    val originalItems = ArrayList(items)

    override fun getItem(position: Int): String {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var results = items
                if (constraint != null) {
                    val lowerConstraint = constraint.toString().toLowerCase()
                    results = results.filter { it -> it.toLowerCase().contains(lowerConstraint) }
                }
                val filterResults = FilterResults()
                filterResults.values = results
                filterResults.count = results.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (constraint != null) {
                    val lowerConstraint = constraint.toString().toLowerCase()
                    items = originalItems.filter { it -> it.toLowerCase().contains(lowerConstraint) }
                } else {
                    items = originalItems
                }
                notifyDataSetChanged()
            }
        }
    }
}