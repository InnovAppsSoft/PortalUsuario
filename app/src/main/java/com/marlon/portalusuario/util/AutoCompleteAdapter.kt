package com.marlon.portalusuario.util

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable

class AutoCompleteAdapter(
    context: Context,
    resource: Int,
) : ArrayAdapter<String>(context, resource),
    Filterable {
    var data: MutableList<String> = mutableListOf()

    override fun getCount() = data.size

    override fun getItem(position: Int) = data[position]

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    val suggestions = mutableListOf<String>()
                    val input = constraint.toString()
                    if (!input.contains("@")) {
                        suggestions.add("$input@nauta.com.cu")
                        suggestions.add("$input@nauta.co.cu")
                    } else {
                        val atIndex = input.indexOf("@")
                        val domainPart = input.substring(atIndex)
                        if ("@nauta.com.cu".contains(domainPart)) {
                            val index = "@nauta.com.cu".indexOf(domainPart)
                            val suffix = "@nauta.com.cu".substring(index + domainPart.length)
                            suggestions.add(input + suffix)
                        }
                        if ("@nauta.co.cu".contains(domainPart)) {
                            val index = "@nauta.co.cu".indexOf(domainPart)
                            val suffix = "@nauta.co.cu".substring(index + domainPart.length)
                            suggestions.add(input + suffix)
                        }
                    }
                    results.values = suggestions
                    results.count = suggestions.size
                    data = suggestions
                }
                return results
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?,
            ) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}
