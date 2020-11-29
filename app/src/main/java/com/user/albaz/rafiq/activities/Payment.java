package com.user.albaz.rafiq.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.Constants.NewPaymentListAdapter;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.base.ServiceActivity;
import com.user.albaz.rafiq.helper.ConnectionHelper;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.models.CardDetails;
import com.user.albaz.rafiq.models.CardInfo;
import com.user.albaz.rafiq.utils.MyBoldTextView;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.user.albaz.rafiq.App.trimMessage;

public class Payment extends ServiceActivity {

    private final int ADD_CARD_CODE = 435;
    Activity activity;
    Context context;
    LoadingDialog loadingDialog;
    ImageView backArrow;
    MyBoldTextView addCard;
    ListView payment_list_view;
    ArrayList<JSONObject> listItems;
    NewPaymentListAdapter paymentAdapter;
    MyBoldTextView empty_text;
    Utilities utils = new Utilities();
    JSONObject deleteCard = new JSONObject();

    LinearLayout cashLayout;

    //Internet
    ConnectionHelper helper;
    Boolean isInternet;

    private ArrayList<CardDetails> cardArrayList;

    public Payment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        context = Payment.this;
        activity = Payment.this;

        findViewByIdAndInitialize(); // Initialize Elements
        getCardList(); // Invoke Method Get Card List

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Clicked AddCard
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToAddCard(); // Invoke Method Go Add Card Activity
            }
        });

        // Clicked CashLayout
        cashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardInfo cardInfo=new CardInfo();
                cardInfo.setLastFour("CASH");
                Intent intent=new Intent();
                intent.putExtra("card_info",cardInfo);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        // Clicked in Item PaymentListView
        payment_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject object = new JSONObject(listItems.get(position).toString());
                    utils.print("MyTest", "" + listItems.get(position));

                    // Getting Data With Selected Any Item ListView
                    CardInfo cardInfo = new CardInfo(); // Instance VisaCard
                    cardInfo.setLastFour(object.optString("last_four"));
                    cardInfo.setCardId(object.optString("card_id"));
                    cardInfo.setCardType(object.optString("brand"));
                    Intent intent=new Intent();
                    intent.putExtra("card_info",cardInfo);
                    setResult(RESULT_OK,intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Long Clicked Item ListView
        payment_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject object = new JSONObject(paymentAdapter.getItem(i).toString());
                    DeleteCardDailog(object); // Invoke Method Getting Dialog Popup
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    // Method To Get Popup Message Dailog Delete Card Or Not
    private void DeleteCardDailog(final JSONObject object) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.are_you_sure))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCard = object;
                        deleteCard(); // Invoke Method Delete
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Delete Card Payment
    public void deleteCard() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("card_id", deleteCard.optString("card_id"));
            object.put("_method", "DELETE");

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.DELETE_CARD_FROM_ACCOUNT_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SendRequestResponse", response.toString());
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                getCardList();
                deleteCard = new JSONObject();
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
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(errorObj.optString("error"));
                                //displayMessage(getString(R.string.something_went_wrong));
                            }
                            utils.print("MyTest", "" + errorObj.toString());
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("DELETE_CARD");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        deleteCard();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Method Get All Cards From Server
    public void getCardList() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.CARD_PAYMENT_LIST, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                utils.print("GetPaymentList", response.toString());
                if (response != null && response.length() > 0) {
                    listItems = getArrayListFromJSONArray(response); // Invoke Method To Add All Card In List Array
                    if (listItems.isEmpty()) {
                        //empty_text.setVisibility(View.VISIBLE);
                        payment_list_view.setVisibility(View.GONE);
                    } else {
                        //empty_text.setVisibility(View.GONE);
                        payment_list_view.setVisibility(View.VISIBLE);
                    }

                    for (JSONObject jsonObject : listItems) {
                        Gson gson = new Gson();
                        CardDetails card = gson.fromJson(jsonObject.toString(), CardDetails.class);
                        card.setSelected("false");
                        card.setSelected("true");
                        cardArrayList = new ArrayList<>();
                        cardArrayList.add(card);
                    }

                    paymentAdapter = new NewPaymentListAdapter(context, cardArrayList, activity);
                    payment_list_view.setAdapter(paymentAdapter);
                } else {
                    //empty_text.setVisibility(View.VISIBLE);
                    payment_list_view.setVisibility(View.GONE);
                }
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
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
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAYMENT_LIST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));

                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getCardList();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    // RefreshAccessToken When Not found its
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                utils.print("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                if (tag.equalsIgnoreCase("PAYMENT_LIST")) {
                    getCardList();
                } else {
                    deleteCard();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken(tag);
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

    // Method To Add All Cards In ArrayList
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

    // Initialization Elements
    public void findViewByIdAndInitialize() {
        backArrow = (ImageView) findViewById(R.id.backArrow);
        addCard = (MyBoldTextView) findViewById(R.id.addCard);
        payment_list_view = (ListView) findViewById(R.id.payment_list_view);
        empty_text = (MyBoldTextView) findViewById(R.id.empty_text);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        cashLayout = (LinearLayout) findViewById(R.id.cash_layout);
    }

    // Display Snackbar Message
    public void displayMessage(String toastString) {
        try {
            if (getCurrentFocus() != null)
                Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Go To Begin Activity
    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    // Go To Adding Card Activity
    public void GoToAddCard() {
        Intent mainIntent = new Intent(activity, AddCard.class);
        startActivityForResult(mainIntent, ADD_CARD_CODE);
    }

    // Result Activity To Get Cards Or Not
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCardList();
                }
            }
        }
    }
}
