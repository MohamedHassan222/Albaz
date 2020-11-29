package com.user.albaz.rafiq.Constants;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.models.PlaceAutoComplete;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import static com.user.albaz.rafiq.R.string.google_map_api;

/**
 * Adapter Auto Complete Places
 */

public class AutoCompleteAdapter extends
        ArrayAdapter<PlaceAutoComplete> {
    ViewHolder holder;
    Context context;
    List<PlaceAutoComplete> Places;
    private Activity mActivity;
    public List<Place.Field> placeFields;
    PlacesClient placesClient;
    com.google.android.libraries.places.api.Places places;

    // Invoking Constrictor To Set Row In ArrayList
    public AutoCompleteAdapter(Context context, List<PlaceAutoComplete> modelsArrayList, Activity activity) {
        super(context, R.layout.autocomplete_row, modelsArrayList);
        this.context = context;
        this.Places = modelsArrayList;
        this.mActivity = activity;
        com.google.android.libraries.places.api.Places.initialize(context, String.valueOf(google_map_api));
        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
    }

    // Initialize Content View
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.autocomplete_row, parent, false);
            holder = new ViewHolder();
            holder.name = rowView.findViewById(R.id.place_name);
            holder.location = rowView.findViewById(R.id.place_detail);
            rowView.setTag(holder);

        } else
            holder = (ViewHolder) rowView.getTag();
        /***** Get each Model object from ArrayList ********/
        holder.Place = Places.get(position);
        StringTokenizer st = new StringTokenizer(holder.Place.getPlaceDesc(), ",");

        /************  Set Model values in Holder elements ***********/

        holder.name.setText(st.nextToken());
        String desc_detail = "";
        for (int i = 1; i < st.countTokens(); i++) {
            if (i == st.countTokens() - 1) {
                desc_detail = desc_detail + st.nextToken();
            } else {
                desc_detail = desc_detail + st.nextToken() + ",";
            }
        }
        holder.location.setText(desc_detail);
        return rowView;
    }

    // Class To Initialize Name & Location Places
    class ViewHolder {
        PlaceAutoComplete Place;
        TextView name, location;
    }

    @Override
    public int getCount() {
        return Places.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Address getFullAddress(Place place) {
        Address address;
        Locale aLocale = new Locale.Builder().setLanguage("en").build();
        Geocoder geocoder = new Geocoder(context, aLocale);
        try {
            List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
            address = addresses.get(0);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setGoogleAddress() {
        com.google.android.libraries.places.api.Places.initialize(context, String.valueOf(google_map_api));
        FetchPlaceRequest request = FetchPlaceRequest.builder(holder.Place.getPlaceID(), placeFields).build();
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();
                Address a = getFullAddress(place);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                }
            }
        });
    }

}