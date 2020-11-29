package com.user.albaz.rafiq.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.Listeners.ShowMap;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.Services.SocketService;
import com.user.albaz.rafiq.fragments.Coupon;
import com.user.albaz.rafiq.fragments.Help;
import com.user.albaz.rafiq.fragments.HomeFragment;
import com.user.albaz.rafiq.fragments.Wallet;
import com.user.albaz.rafiq.fragments.YourTrips;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.utils.CustomTypefaceSpan;
import com.user.albaz.rafiq.utils.MyTextView;
import com.user.albaz.rafiq.utils.ResponseListener;
import com.user.albaz.rafiq.utils.Utilities;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.user.albaz.rafiq.App.trimMessage;

public class MainActivity extends AppCompatActivity implements HomeFragment.HomeFragmentListener
        , ResponseListener, ShowMap {

    public static String TAG = "MAINACTIVITY";

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PAYMENT = "payments";
    private static final String TAG_YOURTRIPS = "yourtrips";
    private static final String TAG_COUPON = "coupon";
    private static final String TAG_WALLET = "wallet";
    private static final String TAG_HELP = "help";
    private static final String TAG_SHARE = "share";
    private static final String TAG_LOGOUT = "logout";
    public Context context = MainActivity.this;
    public Activity activity = MainActivity.this;
    AlertDialog alert;

    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    private NavigationView navigationView;
    public static DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtWebsite;
    private MyTextView txtName;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private Boolean want_exit = false;

    LoadingDialog loadingDialog;
    Dialog dialog;


    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private String notificationMsg;

    private static final int REQUEST_LOCATION = 1450;
    GoogleApiClient mGoogleApiClient;

    public static boolean IsActivityAlive = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 12231);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IsActivityAlive = true;

        // Check Login With "Facebook" To initialized on Application start.
        if (SharedHelper.getKey(context, "login_by").equals("facebook")) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }

        // Layout In Activity
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            notificationMsg = intent.getExtras().getString("Notification");


        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.usernameTxt);
        txtWebsite = navHeader.findViewById(R.id.status_txt);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, EditProfile.class));

            }
        });

        // load nav Header data
        loadNavHeader(); // Invoke Navigation Header

        // initializing Navigation
        setUpNavigationView();

        // Check Layout Empty
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME; // Set Tag
            loadHomeFragment(); // Invoke Method To Load Home Fragment
        }

        lateUpdate();
    }

    // Method Loading Nav Header
    private void loadNavHeader() {

        /***
         * Load navigation menu header information
         * like background image, profile image
         * name, website, notifications action view (dot)
         */

        // name, website
        txtName.setText(SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name"));
        txtWebsite.setText("");

        // Loading Profile Image
        if (!SharedHelper.getKey(context, "picture").equalsIgnoreCase("") && !SharedHelper.getKey(context, "picture").equalsIgnoreCase(null)) {
//            Glide.with(context).load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProfile);
            Picasso.with(context).load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        } else {
            Picasso.with(context).load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(imgProfile);
        }

    }

    // Method To Loading Home Fragment
    public void loadHomeFragment() {

        /***
         * Returns Respected fragment that user
         * selected from navigation menu
         */

        SharedHelper.putKey(context, "current_status", "");
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            // show or hide the fab button
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        //Closing drawer on item click
        drawer.closeDrawers();
        // refresh toolbar menu
        invalidateOptionsMenu();

    }

    // Method Get Any Fragment When Selected Menu Navigation
    private Fragment getHomeFragment() {

        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = HomeFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("Notification", notificationMsg);
                homeFragment.setArguments(bundle);
                return homeFragment;

            case 1:
                // Your Trips
                YourTrips yourTripsFragment = new YourTrips();
                return yourTripsFragment;
            case 2:
                // Coupon
                Coupon couponFragment = new Coupon();
                return couponFragment;

            case 3:
//                // wallet fragment
                Wallet walletletFragment = new Wallet();
                return walletletFragment;
            case 4:
                // Help fragment
                Help helpFragment = new Help();
                return helpFragment;

            default:
                return new HomeFragment();

        }

    }

    // Method Setup Navigation & Get Any Fragment When Clicked Menu Navigation
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.nav_yourtrips:
                        if (Utilities.isNetworkAvailable(MainActivity.this)) {
                            drawer.closeDrawers();

                            SharedHelper.putKey(context, "current_status", "");
                            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                            intent.putExtra("tag", "past");
                            startActivity(intent);
                        } else {
                            showNetWorkDialog();
                        }
                        return true;
                    // break;`
                    case R.id.nav_coupon:
                        if (Utilities.isNetworkAvailable(MainActivity.this)) {
                            drawer.closeDrawers();
                       /* navItemIndex = 3;
                        CURRENT_TAG = TAG_COUPON;
                        break;*/
                            SharedHelper.putKey(context, "current_status", "");
                            startActivity(new Intent(MainActivity.this, CouponActivity.class));
                        } else {
                            showNetWorkDialog();
                        }
                        return true;
                    case R.id.nav_help:
                        if (Utilities.isNetworkAvailable(MainActivity.this)) {
                            drawer.closeDrawers();
                       /* navItemIndex = 5;
                        CURRENT_TAG = TAG_HELP;*/
                            SharedHelper.putKey(context, "current_status", "");
                            startActivity(new Intent(MainActivity.this, ActivityHelp.class));
                        } else {
                            showNetWorkDialog();
                        }
                        break;
                    case R.id.nav_share:
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        if (Utilities.isNetworkAvailable(MainActivity.this)) {
                            navigateToShareScreen(URLHelper.APP_URL);
                            drawer.closeDrawers();
                        } else {
                            showNetWorkDialog();
                        }

                        return true;
                    case R.id.nav_wallet:
                        drawer.closeDrawers();
                        /*navItemIndex = 4;
                        CURRENT_TAG = TAG_WALLET;*/
                        SharedHelper.putKey(context, "current_status", "");
                        startActivity(new Intent(MainActivity.this, ActivityWallet.class));
                        return true;
                    case R.id.nav_logout:
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        if (Utilities.isNetworkAvailable(MainActivity.this)) {
                            showLogoutDialog();
                        } else {
                            showNetWorkDialog();
                        }
                        return true;

                    default:
                        navItemIndex = 0;
                }

                loadHomeFragment(); // Invoke Load Home Fragment
                return true;
            }
        });

        // Change Text Font
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem); // Invoke Apply Text Font
        }

        // Set Toggle Drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    // Method SignOut Account Google API
    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });
    }

    // Method SignOut Profile Account
    public void logout() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(this, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                drawer.closeDrawers();

                if (SharedHelper.getKey(context, "login_by").equals("facebook"))
                    LoginManager.getInstance().logOut();
                if (SharedHelper.getKey(context, "login_by").equals("google"))
                    signOut();
                if (!SharedHelper.getKey(MainActivity.this, "account_kit_token").equalsIgnoreCase("")) {
                    FirebaseAuth.getInstance().signOut();
                    SharedHelper.putKey(MainActivity.this, "account_kit_token", "");
                }
                SharedHelper.putKey(context, "current_status", "");
                SharedHelper.putKey(context, "access_token", "");
                SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
                SharedHelper.putKey(context, "email", "");
                SharedHelper.putKey(context, "login_by", "");
                Intent goToLogin = new Intent(activity, BeginScreen.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                startActivity(goToLogin);
                finish();
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
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken();
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
                        logout();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Method Show SnackBar Message
    public void displayMessage(String toastString) {
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    // Method To Refresh Access Token
    private void refreshAccessToken() {

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

                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                logout();


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

    // Method GoToBeginActivity
    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(activity, BeginScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    // Method Show Dialog Logout
    private void showLogoutDialog() {
        if (!isFinishing()) {
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_edit);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            LinearLayout yes = dialog.findViewById(R.id.yes);
            LinearLayout no = dialog.findViewById(R.id.no);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    destroy_App(); // Destroy Lat & Lng in DB
                    logout();
                    SharedHelper.clearSharedPreferences(context);
                    Intent intent = new Intent(getApplicationContext(), SocketService.class);
                    intent.setAction(SocketService.ACTION_STOP_FOREGROUND_SERVICE);
                    startService(intent);

                    dialog.dismiss();
                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            dialog.show();

        }
    }

    // Method Set Font & Changed Text Font
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ClanPro-NarrNews.otf"); // Set Font
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    // Method OnBackPressed
    @Override
    public void onBackPressed() {
        if (want_exit) {
            super.onBackPressed();
        } else {
            if (!want_exit) {
                Toast.makeText(context, "press again to exit!", Toast.LENGTH_SHORT).show();
                want_exit = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            want_exit = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }


//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawers();
//            return;
//        }
//
//        else
//        {
//            // This code loads home fragment when back key is pressed
//            // when user is in other fragment than home
//            if (shouldLoadHomeFragOnBackPress) {
//                // checking if user is on other navigation menu
//                // rather than home
//                if (navItemIndex != 0) {
//                    navItemIndex = 0;
//                    CURRENT_TAG = TAG_HOME;
//                    SharedHelper.putKey(context, "current_status", "");
//                    loadHomeFragment();
//                    return;
//                }
//                else {
//                    SharedHelper.putKey(context, "current_status", "");
//
//
//
//
//                }
//            }
//
//        }
//        if (HomeFragment.lnrWaitingForProviders.getVisibility()==View.VISIBLE
//        || HomeFragment.ScheduleLayout.getVisibility()==View.VISIBLE
//        ||HomeFragment.lnrApproximate.getVisibility()==View.VISIBLE)
//        {
//
//        }
//        else
//        {
//            super.onBackPressed();
//        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notification, menu);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        IsActivityAlive = true;

        askForSystemOverlayPermission();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Logout rafiq!", Toast.LENGTH_LONG).show();
            return true;
        }

        // app user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // rafiq is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        IsActivityAlive = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        IsActivityAlive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IsActivityAlive = true;
        alert = null;

    }

    // Method Clicked Sharead App
    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray arrayResponse) {

    }

    // Destroy Session When User Offline
    void destroy_App() {
        JSONObject object = new JSONObject();

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.destroyApp, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(postRequest);

    }


    @Override
    public void showMap() {

        loadHomeFragment();
    }

    private void showNetWorkDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_internet), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        alert.dismiss();
                        alert = null;

                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.setCancelable(true);
            alert.show();

        }
    }

    private void lateUpdate() {
        final Handler handler = new Handler();
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent in = new Intent(MainActivity.this, SocketService.class);
                    in.setAction(SocketService.ACTION_START_FOREGROUND_SERVICE);
                    startService(in);
                }
            }, 7000);
//        handler.

        } catch (Exception e) {
        }
    }

}