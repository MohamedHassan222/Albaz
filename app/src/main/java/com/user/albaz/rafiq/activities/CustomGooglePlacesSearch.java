package com.user.albaz.rafiq.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.Constants.AutoCompleteAdapter;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.models.PlaceAutoComplete;
import com.user.albaz.rafiq.models.PlacePredictions;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CustomGooglePlacesSearch extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    Locale lLocale = new Locale.Builder()
            .setLanguage(Locale.getDefault().getLanguage())
            .build();
    Boolean isSource = true;
    Boolean isDistination = false;
    double latitude;
    double longitude;
    private ListView mAutoCompleteList;
    private EditText txtDestination, txtaddressSource;
    private String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions = new PlacePredictions();
    private Location mLastLocation;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private static final int MY_PERMISSIONS_REQUEST_LOC = 30;
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    TextView txtPickLocation;
    Utilities utils = new Utilities();
    ImageView backArrow, imgDestClose, imgSourceClose;
    Activity thisActivity;
    String strSource = "";
    String strSelected = "";
    LatLng latLngDistination = null, latLngSourse = null;
    private PlacePredictions placePredictions = new PlacePredictions();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_soruce_and_destination);
        thisActivity = this;
        // Initialize Elements
        txtDestination = findViewById(R.id.txtDestination);
        txtaddressSource = findViewById(R.id.txtaddressSource);
        mAutoCompleteList = findViewById(R.id.searchResultLV);
        backArrow = findViewById(R.id.backArrow);
        imgDestClose = findViewById(R.id.imgDestClose);
        imgSourceClose = findViewById(R.id.imgSourceClose);
        txtPickLocation = findViewById(R.id.txtPickLocation);
        // Selected From ListView Result Places Complete
        mAutoCompleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (isSource) {
                        PlaceAutoComplete place = new PlaceAutoComplete();
                        place.setPlaceDesc(mAutoCompleteAdapter.getItem(position).getPlaceDesc());
                        place.setPlaceID(mAutoCompleteAdapter.getItem(position).getPlaceID());
                        latLngSourse = getLocationFromAddress(thisActivity, place.getPlaceDesc());
                        txtaddressSource.setText(place.getPlaceDesc());
                        placePredictions.strSourceAddressar = getFullAddressAR(latLngSourse);
                        placePredictions.strSourceAddress = getFullAddressEN(latLngSourse);
                        placePredictions.strSourceLatitude = latLngSourse.latitude + "";
                        placePredictions.strSourceLongitude = latLngSourse.longitude + "";
                    }
                    if (isDistination) {
                        PlaceAutoComplete place = new PlaceAutoComplete();
                        place.setPlaceDesc(mAutoCompleteAdapter.getItem(position).getPlaceDesc());
                        place.setPlaceID(mAutoCompleteAdapter.getItem(position).getPlaceID());
                        latLngDistination = getLocationFromAddress(thisActivity, place.getPlaceDesc());
                        txtDestination.setText(place.getPlaceDesc());
                        placePredictions.strDestAddressar = getFullAddressAR(latLngDistination);
                        placePredictions.strDestAddress = getFullAddressEN(latLngDistination);
                        placePredictions.strDestLatitude = latLngDistination.latitude + "";
                        placePredictions.strDestLongitude = latLngDistination.longitude + "";
                    }
                    if (txtDestination.getText().toString().length() > 3 &&
                            txtaddressSource.getText().toString().length() > 3) {
                        setAddress();
                    }
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    //0  LayoutInflater inflater = (LayoutInflater) thisActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    builder.setMessage("Please choose pickup location")
                            .setTitle(thisActivity.getString(R.string.app_name))
                            .setCancelable(true)
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    txtaddressSource.requestFocus(); // Focus EdiText Source Address
                                    txtDestination.setText(""); // Set Empty Definition EdiText
                                    imgDestClose.setVisibility(View.GONE); // InViable Closing Image
                                    mAutoCompleteList.setVisibility(View.GONE); // InViable ListView Result Places Complete
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    e.printStackTrace();
                }
            }
        });

        // Set Intent In Vars
        String cursor = getIntent().getExtras().getString("cursor"); // Selected Source Or Destination
        String s_address = getIntent().getExtras().getString("s_address");
        String d_address = getIntent().getExtras().getString("d_address");
        // SetText In Text Address Source
        txtaddressSource.setText(s_address);
        latLngSourse = getLocationFromAddress(thisActivity, s_address);
        if (placePredictions!=null)
        {
            if (latLngSourse!=null)
            {
                placePredictions.strSourceLatitude = latLngSourse.latitude + "";
                placePredictions.strSourceLongitude = latLngSourse.longitude + "";
                placePredictions.strSourceAddress = s_address;
                placePredictions.strSourceAddressar = getFullAddressAR(getLocationFromAddress(thisActivity, s_address));


            }
        }

        if (d_address != null && !d_address.equalsIgnoreCase("")) {
            txtDestination.setText(d_address);
        }
        // Check Selected Source
        if (cursor.equalsIgnoreCase("source")) {
            strSelected = "source"; // Selected
            txtaddressSource.requestFocus(); // Focus Field
            imgSourceClose.setVisibility(View.VISIBLE); // Visible Image Closing Text Source Address
            imgDestClose.setVisibility(View.GONE); // InViable Image Closing Text Destination Address
        } else { // Selected Destination
            txtDestination.requestFocus();
            strSelected = "destination"; // Selected
            imgDestClose.setVisibility(View.VISIBLE); // Visible Image Closing Text Destination Address
            imgSourceClose.setVisibility(View.GONE); // InViable Image Closing Text Source Address
        }

        // Clicked EditText Source Address
        txtaddressSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strSelected = "source";
                    imgSourceClose.setVisibility(View.VISIBLE);
                } else {
                    imgSourceClose.setVisibility(View.GONE);
                }
            }
        });

        // Clicked EditText Destination Address
        txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    strSelected = "destination";
                    imgDestClose.setVisibility(View.VISIBLE);
                } else {
                    imgDestClose.setVisibility(View.GONE);
                }
            }
        });

        // Clicked Image Closing Text Source Address
        imgSourceClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtaddressSource.setText("");
                mAutoCompleteList.setVisibility(View.GONE);
                imgSourceClose.setVisibility(View.GONE);
                txtaddressSource.requestFocus();
                isSource = true;
            }
        });

        // Clicked Image Closing Text Destination Address
        imgDestClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDestination.setText("");
                mAutoCompleteList.setVisibility(View.GONE);
                imgDestClose.setVisibility(View.GONE);
                txtDestination.requestFocus();
                isDistination = true;
            }
        });

        // Clicked Image Pick Destination Address
        txtPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.hideKeypad(thisActivity, thisActivity.getCurrentFocus());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("pick_location", "yes");
                        intent.putExtra("type", strSelected);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 500);
            }
        });

        // Get Permission For Android M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            fetchLocation(); // Invoke Get Location By Building Google API
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOC);
            } else {
                fetchLocation();  // Invoke Get Location By Building Google API
            }
        }

        // Add a text change listener to implement autocomplete functionality
        txtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgDestClose.setVisibility(View.VISIBLE);
                isDistination = true;
                isSource = false;
            }

            // When Text Address Changed
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after rafiq has typed minimum 3 chars

                imgDestClose.setVisibility(View.VISIBLE);
                strSelected = "destination";
                if (txtDestination.getText().length() > 0) {
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgDestClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                            App.getInstance().cancelRequestInQueue(GETPLACESHIT);
                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtDestination.getText().toString()), object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Gson gson = new Gson(); // Get Places Result Formatted Json Code
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (mAutoCompleteAdapter == null) { // Check
                                        // Set Adapter To Result It In ListView
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(CustomGooglePlacesSearch.this, predictions.getPlaces(), CustomGooglePlacesSearch.this);
                                        mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                    } else {
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();
                                        mAutoCompleteList.invalidate();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            App.getInstance().addToRequestQueue(jsonObjectRequest);
                        }

                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgDestClose.setVisibility(View.VISIBLE);
            }
        });
        // Add a text change listener to implement autocomplete functionality
        txtaddressSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgSourceClose.setVisibility(View.VISIBLE);
                isSource = true;
                isDistination = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after rafiq has typed minimum 3 chars
                strSelected = "source";
                if (txtaddressSource.getText().length() > 0) {
                    txtPickLocation.setVisibility(View.VISIBLE);
                    imgSourceClose.setVisibility(View.VISIBLE);
                    txtPickLocation.setText(getString(R.string.pin_location));
                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);
                    Runnable run = new Runnable() {

                        @Override
                        public void run() {
                            // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
                            App.getInstance().cancelRequestInQueue(GETPLACESHIT);

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtaddressSource.getText().toString()),
                                    object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(CustomGooglePlacesSearch.this, predictions.getPlaces(), CustomGooglePlacesSearch.this);
                                        mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                    } else {
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();
                                        mAutoCompleteList.invalidate();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            App.getInstance().addToRequestQueue(jsonObjectRequest);

                        }

                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }
        });
        // Set Selection In EditText Destination Address
        txtDestination.setSelection(txtDestination.getText().length());
        // Clicked BackArrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getFullAddressEN(LatLng latLng) {
        Address address;
            Locale locale = new Locale.Builder()
                    .setLanguage("en").build();
            Geocoder geocoder = new Geocoder(this, locale);
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                address = addresses.get(0);
                return address.getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getFullAddressAR(LatLng latLng) {
        Address address;
        Locale locale = new Locale.Builder()
                .setLanguage("ar").build();
        Geocoder geocoder = new Geocoder(this, locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0);
            return address.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 1);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    // Method Get Places Result In ListView
    public String getPlaceAutoCompleteUrl(String input) {

        // Set Link To Get Result Places Json
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");

        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=" + Utilities.getDeviceLocal());
        urlString.append("&key=" + getResources().getString(R.string.google_map_api));

        return urlString.toString();
    }

    // Building Google API
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // Connection Mapping
    @Override
    public void onConnected(Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    // Method Get Location By Building Google API
    public void fetchLocation() {
        //Build google API client to use fused location
        buildGoogleApiClient();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    // Set & Check Permission Mapping
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    fetchLocation(); // Invoke Get Location By Building Google API
                } else {
                    // permission denied!
                    Toast.makeText(this, "Please grant permission for using this app!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // Method OnBackPressed
    @Override
    public void onBackPressed() {
        setAddress();
        super.onBackPressed();
    }

    // Method To Set Address Default
    void setAddress() {
        utils.hideKeypad(thisActivity, getCurrentFocus());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                if (placePredictions != null) {
                    intent.putExtra("Location Address", placePredictions);
                    intent.putExtra("pick_location", "no");
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED, intent);
                }
                finish();
            }
        }, 500);
    }

    // Check Connecting Mapping Or Not
    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    // Check Stopped
    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
}
