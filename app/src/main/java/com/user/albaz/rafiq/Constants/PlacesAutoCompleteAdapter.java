package com.user.albaz.rafiq.Constants;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * Class Is Adapter AutoComplete Result For Searching Places In Map
*/

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    ArrayList<String> resultList; // ArrayList For Set All Result Pleases

    Context mContext;
    int mResource;

    PlaceAPI mPlaceAPI = new PlaceAPI(); // Instance From Class Places Continents

    // Method Constrictor To Set Context
    public PlacesAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
    }

    // Method Getting Number Of Count ArrayList
    @Override
    public int getCount() {
        // Last item will be the footer
        return resultList.size();
    }

    // Method Getting Position Item In ArrayList
    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    // Method Set The Result In ArrayList
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    resultList = mPlaceAPI.autocomplete(constraint.toString());

                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}
