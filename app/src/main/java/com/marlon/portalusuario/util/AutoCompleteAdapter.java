package com.marlon.portalusuario.util;


import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter implements Filterable {
    ArrayList<String> data;

    public AutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.data = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    ArrayList<String> suggestions = new ArrayList<>();
                    if (!constraint.toString().contains("@")) {
                        suggestions.add(constraint.toString() + "@nauta.com.cu");
                        suggestions.add(constraint.toString() + "@nauta.co.cu");
                    } else {
                        int indexOf = constraint.toString().indexOf("@");
                        String substring = constraint.toString().substring(indexOf);
                        if ("@nauta.com.cu".contains(substring)) {
                            indexOf = "@nauta.com.cu".indexOf(substring);
                            String substring2 = "@nauta.com.cu".substring(indexOf +
                                    substring.length());
                            suggestions.add(constraint + substring2);
                        }
                        if ("@nauta.co.cu".contains(substring)) {
                            indexOf = "@nauta.co.cu".indexOf(substring);
                            String substring2 = "@nauta.co.cu".substring(indexOf +
                                    substring.length());
                            suggestions.add(constraint + substring2);
                        }
                    }

                    results.values = suggestions;
                    results.count = suggestions.size();
                    data = suggestions;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else notifyDataSetInvalidated();
            }
        };
    }
}
