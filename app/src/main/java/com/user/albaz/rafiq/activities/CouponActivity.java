package com.user.albaz.rafiq.activities;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.Constants.CouponListAdapter;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class CouponActivity extends AppCompatActivity {

    public static String TAG = "COUPONACTIVITY";
    private EditText coupon_et;
    private Button apply_button;
    private String session_token;
    Context context;
    LinearLayout couponListCardView;
    ListView coupon_list_view;
    ArrayList<JSONObject> listItems;
    ListAdapter couponAdapter;
    LoadingDialog loadingDialog;
    Utilities utils = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set BackGround
        getWindow().setBackgroundDrawableResource(R.drawable.coupon_bg);
        setContentView(R.layout.activity_coupon);

        // Set ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Activity
        context = CouponActivity.this;

        // Set Token In Var SessionToken
        session_token = SharedHelper.getKey(this, "access_token");

        // Initialize Elements
        couponListCardView = findViewById(R.id.cardListViewLayout);
        coupon_list_view = findViewById(R.id.coupon_list_view);
        coupon_et = findViewById(R.id.coupon_et);
        apply_button = findViewById(R.id.apply_button);

        // Clicked BTN Apply
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (coupon_et.getText().toString().isEmpty()) {
                    Toast.makeText(CouponActivity.this, "Enter A Coupon", Toast.LENGTH_SHORT).show();
                } else {
                    sendToServer(); // Invoke Method To Check Coupon In Server
                }
            }
        });

        getCoupon(); // Invoke Method Get Det
        // ails Coupon
    }

    // Method To Check Coupon In Server
    private void sendToServer() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        JsonObject json = new JsonObject();
        json.addProperty("promocode", coupon_et.getText().toString());

        Ion.with(this)
                .load(URLHelper.ADD_COUPON_API)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .setJsonObjectBody(json)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        try {
                            if ((loadingDialog != null) && (loadingDialog.isShowing()))
                                loadingDialog.dismiss();
                            // response contains both the headers and the string result
                            if (e != null) {
                                if (e instanceof NetworkErrorException) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                }
                                if (e instanceof TimeoutException) {
                                    sendToServer(); // If Time Out Server Invoke Again
                                }
                                return;
                            }
                            if (response.getHeaders().code() == 200) { // Response Success

                                utils.print("AddCouponRes", "" + response.getResult());
                                try {
                                    JSONObject jsonObject = new JSONObject(response.getResult());
                                    if (jsonObject.optString("code").equals("promocode_applied")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.coupon_added), Toast.LENGTH_SHORT).show();
                                        couponListCardView.setVisibility(View.GONE); // Visible Linear List Of Coupon
                                        getCoupon(); //Invoke Method Get Details Coupon
                                    } else if (jsonObject.optString("code").equals("promocode_expired")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.expired_coupon), Toast.LENGTH_SHORT).show();
                                    } else if (jsonObject.optString("code").equals("promocode_already_in_use")) {
                                        Toast.makeText(CouponActivity.this, getString(R.string.already_in_use_coupon), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                                    loadingDialog.dismiss();
                                utils.print("AddCouponErr", "" + response.getResult());
                                if (response.getHeaders().code() == 401) {
                                    Utilities.goToLogin(CouponActivity.this);
                                } else
                                    Toast.makeText(CouponActivity.this, getString(R.string.not_vaild_coupon), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }

    // Method To Refresh Access Token
    private void refreshAccessToken(final String tag) {

        JSONObject object = new JSONObject();
        try {
            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("SEND_TO_SERVER")) {
                    sendToServer(); // Invoke Method To Check Coupon In Server
                } else {
                    getCoupon(); // Invoke Method Get Details Coupon
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    utils.GoToBeginActivity(CouponActivity.this);
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        Utilities.goToLogin(CouponActivity.this);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Method Get Details Coupon
    private void getCoupon() {
        couponListCardView.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        Ion.with(this)
                .load(URLHelper.COUPON_LIST_API + "?device_mac=" + Utilities.getMacAddr()+"&lang="+Utilities.getDeviceLocal())
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(CouponActivity.this, "token_type") + " " + session_token)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        // response contains both the headers and the string result
                        if ((loadingDialog != null) && (loadingDialog.isShowing()))
                            loadingDialog.dismiss();
                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            }
                            if (e instanceof TimeoutException) {
                                getCoupon(); // Invoke Method Get Details Coupon
                            }
                        } else {
                            if (response != null) {
                                if (response.getHeaders().code() == 200) { //  Response Success
                                    try {
                                        JSONArray jsonArray = new JSONArray(response.getResult());
                                        if (jsonArray != null && jsonArray.length() > 0) {
                                            utils.print("CouponActivity", "" + jsonArray.toString());
                                            listItems = getArrayListFromJSONArray(jsonArray); // Get All Coupon in ArrayList
                                            // Set Adapter Coupon List
                                            couponAdapter = new CouponListAdapter(context, R.layout.coupon_list_item, listItems);
                                            coupon_list_view.setAdapter(couponAdapter); // Set Adapter
                                            couponListCardView.setVisibility(View.VISIBLE); // Visible ListView Coupon
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    if ((loadingDialog != null) && (loadingDialog.isShowing()))
                                        loadingDialog.dismiss();
                                    if (response.getHeaders().code() == 401) {
                                        Utilities.goToLogin(CouponActivity.this);
                                    }
                                }
                            } else {
                                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                                    loadingDialog.dismiss();
                            }
                        }
                    }
                });
    }

    // Get All Coupon In ArrayList
    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {

        ArrayList<JSONObject> aList = new ArrayList<JSONObject>();

        try {
            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    aList.add(jsonArray.getJSONObject(i));

                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return aList;

    }

    // Display SnackBar Message
    public void displayMessage(String toastString) {
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    //Selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
