package com.user.albaz.rafiq.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.utils.MyTextView;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.user.albaz.rafiq.App.trimMessage;

public class MainServicesActivity extends AppCompatActivity {

    private int currentPostion = 0;
    private Utilities utils;
    private LinearLayout lnrHidePopup;
    private LoadingDialog loadingDialog;
    private RecyclerView rcvServiceTypes;
    private double height;
    private double width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_services);

        utils = new Utilities();

        lnrHidePopup = findViewById(R.id.lnrHidePopup);
        rcvServiceTypes = findViewById(R.id.rcvServiceTypes);

        getServiceList();
    }

    // Class Get Services Car in App
    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {

        JSONArray jsonArray;
        private SparseBooleanArray selectedItems;
        int selectedPosition;

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.service_type_list_item, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            utils.print("Title: ", "" + jsonArray.optJSONObject(position).optString("name") + " Image: " + jsonArray.optJSONObject(position).optString("image") + " Grey_Image:" + jsonArray.optJSONObject(position).optString("grey_image"));

            holder.serviceTitle.setText(jsonArray.optJSONObject(position).optString("name"));
            if (position == currentPostion) {
                SharedHelper.putKey(getApplicationContext(), "service_type", "" + jsonArray.optJSONObject(position).optString("id"));
                Picasso.with(MainServicesActivity.this).load(jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.car_select).error(R.drawable.car_select).into(holder.serviceImg);
                holder.selector_background.setBackgroundResource(R.drawable.full_rounded_button);
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.text_color_white));
            } else {
                Picasso.with(MainServicesActivity.this).load(jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.car_select).error(R.drawable.car_select).into(holder.serviceImg);
                holder.selector_background.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.black_text_color));
            }

            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == currentPostion) {
                        lnrHidePopup.setVisibility(View.VISIBLE);
                    }
                    currentPostion = Integer.parseInt(view.getTag().toString());
                    SharedHelper.putKey(getApplicationContext(), "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                    SharedHelper.putKey(getApplicationContext(), "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                    notifyDataSetChanged();
                    utils.print("service_type", "" + SharedHelper.getKey(getApplicationContext(), "service_type"));
                    utils.print("Service name", "" + SharedHelper.getKey(getApplicationContext(), "name"));
                }
            });
        }

        // Get Count Services
        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        // Model Services Car app
        public class MyViewHolder extends RecyclerView.ViewHolder {

            MyTextView serviceTitle;
            ImageView serviceImg;
            LinearLayout linearLayoutOfList;
            FrameLayout selector_background;

            public MyViewHolder(View itemView) {
                super(itemView);
                serviceTitle = (MyTextView) itemView.findViewById(R.id.serviceItem);
                serviceImg = (ImageView) itemView.findViewById(R.id.serviceImg);
                linearLayoutOfList = (LinearLayout) itemView.findViewById(R.id.LinearLayoutOfList);
                selector_background = (FrameLayout) itemView.findViewById(R.id.selector_background);
                height = itemView.getHeight();
                width = itemView.getWidth();
            }
        }
    }

    // The Method Get All Services in App
    public void getServiceList() {
        loadingDialog = new LoadingDialog(MainServicesActivity.this); // Create Loading Dialog
        loadingDialog.setCancelable(false); // Close Any Dialog

        if (loadingDialog != null)
            loadingDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API + "?advice_mac=" + Utilities.getMacAddr()
                + "&lang=" + Utilities.getDeviceLocal(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Toast.makeText(MainServicesActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                utils.print("GetServices", response.toString());
                if (SharedHelper.getKey(MainServicesActivity.this, "service_type").equalsIgnoreCase("")) {
                    SharedHelper.putKey(MainServicesActivity.this, "service_type", "" + response.optJSONObject(0).optString("id"));
                }
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                if (response.length() > 0) {
                    currentPostion = 0;
                    ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                    rcvServiceTypes.setLayoutManager(new LinearLayoutManager(MainServicesActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    rcvServiceTypes.setAdapter(serviceListAdapter);
                } else {
                    Toast.makeText(MainServicesActivity.this, getString(R.string.no_service), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                               // Toast.makeText(MainServicesActivity.this, errorObj.optString("message"), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(MainServicesActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }

                        } else if (response.statusCode == 401) {
                            Utilities.goToLogin(MainServicesActivity.this);
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                               // Toast.makeText(MainServicesActivity.this, json, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainServicesActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.statusCode == 503) {
                            Toast.makeText(MainServicesActivity.this, getString(R.string.server_down), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(MainServicesActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(MainServicesActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MainServicesActivity.this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(MainServicesActivity.this, "token_type") + " " + SharedHelper.getKey(MainServicesActivity.this, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonArrayRequest);
    }

}
