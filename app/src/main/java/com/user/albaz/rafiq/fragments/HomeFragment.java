package com.user.albaz.rafiq.fragments;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;
import com.user.albaz.rafiq.App;
import com.user.albaz.rafiq.Listeners.OnReceiveSocketListener;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.Services.SchedualTripService;
import com.user.albaz.rafiq.Services.SocketService;
import com.user.albaz.rafiq.activities.CongratulationActivity;
import com.user.albaz.rafiq.activities.CustomGooglePlacesSearch;
import com.user.albaz.rafiq.activities.MainActivity;
import com.user.albaz.rafiq.activities.ShowProfile;
import com.user.albaz.rafiq.helper.ConnectionHelper;
import com.user.albaz.rafiq.helper.DataParser;
import com.user.albaz.rafiq.helper.Invoice;
import com.user.albaz.rafiq.helper.LoadingDialog;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.helper.URLHelper;
import com.user.albaz.rafiq.models.Car;
import com.user.albaz.rafiq.models.CardInfo;
import com.user.albaz.rafiq.models.Driver;
import com.user.albaz.rafiq.models.PlacePredictions;
import com.user.albaz.rafiq.models.Provider;
import com.user.albaz.rafiq.models.TripModel;
import com.user.albaz.rafiq.utils.MapAnimator;
import com.user.albaz.rafiq.utils.MapRipple;
import com.user.albaz.rafiq.utils.MyBoldTextView;
import com.user.albaz.rafiq.utils.MyButton;
import com.user.albaz.rafiq.utils.MyTextView;
import com.user.albaz.rafiq.utils.ResponseListener;
import com.user.albaz.rafiq.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;
import static com.user.albaz.rafiq.App.trimMessage;


public class HomeFragment extends Fragment implements OnMapReadyCallback,
        LocationListener,
        RoutingListener,
        GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResponseListener, GoogleMap.OnCameraMoveListener {


    // SendRequest
    JSONObject object;

    //    private SharedPreferences states;
//    private SharedPreferences.Editor statesEd;
    // OnChaneLocation
    double oldlat = 0;
    double oldlong = 0;
    private static String stateString = "";
    private static final String TAG = "HomeFragment";
    private TextView txt04TimeWait;
    private Activity activity;
    private Context context;
    private View rootView, myLocationButton;
    private HomeFragmentListener listener;
    double wallet_balance;
    private long timeDelay = 10000;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private boolean CancelTripButton = false;  // if true set Text of confirm Button "Confirm" else set Text " Cancel Trip
    private LinearLayout destinationLayer, fragment_home_li_2_cancel_event, fragment_home_li_3_cancel_event;

    private Socket socket;
    private RelativeLayout r_1;

    private AlertDialog dialog;

    private ViewGroup viewContainer;

    private String ONE_TIME_EXECUTE_FILE = "ONCE_EXECUTE";
    // if false the button not clicked
    // if true the button already clicked
    private String  ONE_TIME_EXECUTE_VALUE = "IS_CLICKED";

    String source_addressEN_clean ;
    String dest_address_clean ;

    // Invoking Constructor
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    // Zooming Camera GPS
    @Override
    public void onCameraMove() {

        utils.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);

        cmPosition = mMap.getCameraPosition();
        if (marker != null) {

            if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(marker.getPosition())) {
                utils.print("Current marker", "Current Marker is not visible");
                if (mapfocus.getVisibility() == View.INVISIBLE) {

                    mapfocus.setVisibility(VISIBLE);
                }
            } else {
                utils.print("Current marker", "Current Marker is visible");
                if (mapfocus.getVisibility() == VISIBLE) {
                    mapfocus.setVisibility(View.INVISIBLE);
                }
                if (mMap.getCameraPosition().zoom < 15.3f) {
                    if (mapfocus.getVisibility() == View.INVISIBLE) {
                        mapfocus.setVisibility(VISIBLE);
                    }
                }
            }
        }
    }

    ///// Start Section Drow PolyLine

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    // The If Drawing Error
    @Override
    public void onRoutingFailure(RouteException e) {

        if (e != null) {
            //Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    // Draw Line In Map
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shourtestRouteIndex) {

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            polyOptions.zIndex(9999999f);
            polyOptions.startCap(new SquareCap());
            polyOptions.endCap(new SquareCap());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }

    @Override
    public void onRoutingCancelled() {
    }

    private void getRouteToMarker(double latSource, double lngSource, double latDest, double lngDest) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(latSource, lngSource), new LatLng(latDest, lngDest))
                .key(getString(R.string.google_map_api))
                .build();
        routing.execute();

    }

    // Remove PolyLine
    private void eraesPolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();

    }

    private SharedPreferences state2;
    ///// End Section Drow PolyLine

    public interface HomeFragmentListener {
    }

    GoogleMap mGoogleMap;
    String isPaid = "", paymentMode = "";
    Utilities utils = new Utilities();
    static int flowValue = 0;
    DrawerLayout drawer;
    int NAV_DRAWER = 0;
    String reqStatus = "";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST = 18945;
    private final int ADD_CARD_CODE = 435;
    private static final int REQUEST_LOCATION = 1450;
    String feedBackRating;
    private ArrayList<CardInfo> cardInfoArrayList = new ArrayList<>();
    double height;
    double width;
    public String PreviousStatus = "";
    public String CurrentStatus = "";
    Handler handleCheckStatus;
    String strPickLocation = "", strTag = "", strPickType = "", statusProviders = "";
    boolean once = true;
    int click = 1;
    boolean afterToday = false;
    boolean pick_first = true;
    Driver driver;
    static TripModel MyTrip;
    // Current Diriction Car
    String Lat_Current_Car = "", Lng_Current_Car = "";

    //  <!-- Map frame -->
    LinearLayout mapLayout;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    int value;
    Marker marker;
    Double latitude, longitude;
    String currentAddressEn, currentAddressAr;

    public static GoogleApiClient mGoogleApiClient;

    //   <!-- Source and Destination Layout-->
    LinearLayout sourceAndDestinationLayout;
    FrameLayout frmDestination;
    MyBoldTextView destination;
    ImageView imgMenu, mapfocus, imgBack, shadowBack;
    View tripLine;
    ImageView destinationBorderImg;
    TextView frmSource, frmDest;
    CardView srcDestLayout;


//     <!--1. Request to providers -->

    LinearLayout lnrRequestProviders;
    RecyclerView rcvServiceTypes;
    ImageView imgPaymentType;
    ImageView imgSos;
    ImageView imgShareRide;
    MyBoldTextView lblPaymentType, lblPaymentChange, booking_id;
    MyButton btnRequestRides;
    String scheduledDate = "";
    String scheduledTime = "";
    String cancalReason = "";

    //      <!--1. Driver Details-->
    static public LinearLayout lnrProviderPopup;
    static public LinearLayout lnrHidePopup, lnrPriceBase, lnrPricemin, lnrPricekm, fragment_home_cancle;
    RelativeLayout lnrSearchAnimation;

    ImageView imgProviderPopup;
    MyBoldTextView lblPriceMin, lblBasePricePopup, lblCapacity, lblServiceName, lblPriceKm, lblCalculationType, lblProviderDesc;
    MyButton btnDonePopup;

//      <!--2. Approximate Rate ...-->

    LinearLayout lnrApproximate, waiting;
    MyButton btnRequestRideConfirm;
    MyButton imgSchedule;
    CheckBox chkWallet, walletCheckBox;
    MyBoldTextView lblEta;
    MyBoldTextView lblType;
    MyBoldTextView lblApproxAmount, lblDistance, surgeDiscount, surgeTxt;
    View lineView;

    public static LinearLayout ScheduleLayout;
    MyBoldTextView scheduleDate;
    MyBoldTextView scheduleTime;
    MyButton scheduleBtn;
    DatePickerDialog datePickerDialog;

    LocationRequest mLocationRequest;

//      <!--3. Waiting For Providers ...-->

    public static RelativeLayout lnrWaitingForProviders;
    MyBoldTextView lblNoMatch;
    MyButton btnCancelRide;
    private boolean mIsShowing;
    private boolean mIsHiding;
    RippleBackground rippleBackground;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

//       <!--4. Driver Accepted ...-->

    LinearLayout lnrProviderAccepted, lnrAfterAcceptedStatus, AfterAcceptButtonLayout;
    ImageView imgProvider, imgServiceRequested;
    MyBoldTextView lblProvider, lblStatus, lblServiceRequested, lblModelNumber, lblSurgePrice;
    RatingBar ratingProvider;
    MyButton btnCall, btnCancelTrip, fragment_home_btn_cancle;

//       <!--5. Invoice Layout ...-->

    LinearLayout lnrInvoice;
    TextView txt04BasePrice;
    TextView txt04Distance;
    TextView txt04Distance_amount;
    TextView txt04Waiting;
    TextView txt04Time;
    TextView txt04Waiting_amount;
    TextView txt04Time_amount;
    TextView txt04Tax;
    MyBoldTextView lblBasePrice, lblExtraPrice, lblDistancePrice, lblTaxPrice, lblTotalPrice, lblPaymentTypeInvoice;
    ImageView imgPaymentTypeInvoice;
    MyButton btnPayNow;

//       <!--6. Rate provider Layout ...-->

    LinearLayout lnrRateProvider;
    MyBoldTextView lblProviderNameRate;
    ImageView imgProviderRate;
    RatingBar ratingProviderRate;
    EditText txtCommentsRate;
    Button btnSubmitReview;

//       <!-- Static marker-->

    RelativeLayout rtlStaticMarker;
    ImageView imgDestination;
    MyButton btnDone;
    CameraPosition cmPosition;


    String current_lat = "", current_lng = "",
            current_address = "", current_addressAr = "",
            source_lat = "", source_lng = "",
            source_addressEN = "", source_addressAr = "",
            dest_lat = "", dest_lng = "",
            dest_address = "", dest_addressAR = "";

    // Internet
    ConnectionHelper helper;
    Boolean isInternet;
    //RecylerView
    int currentPostion = 0;
    LoadingDialog loadingDialog;

    //Markers
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private Marker sourceMarker;
    private Marker destinationMarker;
    private Marker providerMarker;
    private Marker destPickMarker;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    ArrayList<Marker> lstProviderMarkers = new ArrayList<Marker>();
    AlertDialog alert;
    //Animation
    Animation slide_down, slide_up, slide_up_top, slide_up_down;

    ParserTask parserTask;
    String notificationTxt;
    boolean scheduleTrip = false;

    MapRipple mapRipple;

    public static boolean IsMapInView = false;

    public static OnReceiveSocketListener onReceiveSocketListener;
    Provider provider;
    Car car;
    static Invoice _CurrentInvoice;

    public enum checkStatusFlow {
        SEARCHING,
        Found,
        STARTED,
        Arrived,
        pickedUp,
        dropped,
        user_cancel_trip_after_waiting,
        provider_cancel_trip_after_waiting,
        provider_cancel_your_trip_is_searching,
        your_trip_is_canceld_from_providers,
        Paid,
        Rated,
        confirmPaid,
        bill,
        not
    }

    public static checkStatusFlow flow = checkStatusFlow.not;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble("oldlat", oldlat);
        outState.putDouble("oldlong", oldlong);
        outState.putInt("flowValue", flowValue);
        outState.putString("isPaid", isPaid);
        outState.putString("paymentMode", paymentMode);
        outState.putString("current_lat", current_lat);
        outState.putString("current_lng", current_lng);
        outState.putString("current_address_en", current_address);
        outState.putString("current_address_ar", current_addressAr);
        outState.putString("source_lat", source_lat);
        outState.putString("source_lng", source_lng);
        outState.putString("source_address_en", source_addressEN);
        outState.putString("source_address_ar", source_addressAr);
        outState.putString("dest_lat", dest_lat);
        outState.putString("dest_lng", dest_lng);
        outState.putString("dest_address", dest_address);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create ArrayList To Add Points Direction Places

        if (getActivity()!=null){
            getActivity().getSharedPreferences("Cancel_trip_btn",MODE_PRIVATE)
                    .edit()
                    .putBoolean("Cancel_trip_btn_value",false)
                    .apply();
        }


        polylines = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            notificationTxt = bundle.getString("Notification");
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        }

        viewContainer = container;

        SharedPreferences.Editor editor2 = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE).edit();
        editor2.putBoolean(ONE_TIME_EXECUTE_VALUE , false);
        editor2.apply();

//        states=rootView.getContext().getSharedPreferences("ss",Context.MODE_PRIVATE);
//        statesEd=states.edit();
//        statesEd.putInt("flow",0);
//        statesEd.commit();
        //Log.v("onCreateView", "fragment_home");
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Check Permission Or Initialize Map in One Time After 5ms
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                init(rootView);
                //permission to access location
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Android M Permission check
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    initMap(); // Create Map
                    MapsInitializer.initialize(getActivity());
                }
            }
        });

        // Get Request Status
        reqStatus = SharedHelper.getKey(context, "req_status");
        if (reqStatus != null && !reqStatus.equalsIgnoreCase("null") && reqStatus.length() > 0) {
            if (reqStatus.equalsIgnoreCase("SEARCHING")) {
                Toast.makeText(context, "You have already requested to a ride", Toast.LENGTH_SHORT).show();
            }
        }

        return rootView; // View Layout
    }

    // Get Context Activity To Get All Item Fragment
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        this.activity = activity;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        IsMapInView = true;
//        checkUserTrips();
//        init(rootView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try {
            listener = (HomeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        IsMapInView = false;
        super.onDetach();
        listener = null;
        checkViewVisipilty("on deattach");
    }

    @Override
    public void onStart() {
        super.onStart();


        IsMapInView = true;
        checkUserTrips();
        init(rootView);

        //Log.d("szzzzzzzzzz", flowValue + " at start");
        onReceiveSocketListener = new OnReceiveSocketListener() {
            @Override
            public void doAfterReceiveSocketData(SocketService.SocketMassageType massageType
                    , JSONObject jsonObject
                    , boolean IsMapInView) {
                stateString = massageType.name();
                //Log.d("soooooooo", massageType.name());
                //Log.d("soooooooo", "flow valu " + flowValue);

                if (massageType == SocketService.SocketMassageType.found) {
                    try {
                        SharedHelper.putKey(context, "request_id", String.valueOf(jsonObject.getInt("Trip_id")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    flow = checkStatusFlow.Found;
                    flowValue = 4;
                    layoutChanges();
                    btnRequestRideConfirm.setEnabled(true);
                    scheduleBtn.setEnabled(true);
                    provider = new Provider(jsonObject);
                    setValueForLnrProviderAccepted(provider);

                } else if (massageType == SocketService.SocketMassageType.arrived) {

                    if (!IsMapInView) {
                        flow = checkStatusFlow.Arrived;
                        provider = new Provider(jsonObject);
                        setValueForLnrProviderAccepted(provider);
                    }
                    //Ali
                    btnCancelTrip.setVisibility(VISIBLE);
                    lblStatus.setText(getString(R.string.arrived));

                } else if (massageType == SocketService.SocketMassageType.pickedUp) {
                    if (!IsMapInView) {
                        flow = checkStatusFlow.pickedUp;
                        flowValue = 4;
                        layoutChanges();
                        provider = new Provider(jsonObject);
                        setValueForLnrProviderAccepted(provider);
                    }
                    //Ali
                    lblStatus.setText(getString(R.string.picked_up));
                    btnCancelTrip.setVisibility(View.GONE);
                    lblStatus.setBackgroundColor(Color.blue(2));
                } else if (massageType == SocketService.SocketMassageType.provider_cancel_your_trip_is_searching) {
                    flow = checkStatusFlow.provider_cancel_your_trip_is_searching;
                    flowValue = 70;
                    layoutChanges();
                    provider = new Provider(jsonObject);
                    setValueForLnrProviderAccepted(provider);
                    AfterAcceptButtonLayout.setVisibility(View.GONE);
                } else if (massageType == SocketService.SocketMassageType.provider_cancel_trip_after_waiting) {
                    flow = checkStatusFlow.provider_cancel_trip_after_waiting;
                    flowValue = 880;
                    layoutChanges();
                    provider = new Provider(jsonObject);
                    setValueForLnrProviderAccepted(provider);

                } else if (massageType == SocketService.SocketMassageType.user_cancel_trip_after_waiting) {
                    flow = checkStatusFlow.user_cancel_trip_after_waiting;
                    flowValue = 980;
                    layoutChanges();
                    provider = new Provider(jsonObject);
                    setValueForLnrProviderAccepted(provider);
                }
                //your_trip_is_canceld_from_providers
                else if (massageType == SocketService.SocketMassageType.your_trip_is_canceld_from_providers) {
                    flow = checkStatusFlow.your_trip_is_canceld_from_providers;
                    flowValue = 710;
                    layoutChanges();
                    provider = new Provider(jsonObject);
                    setValueForLnrProviderAccepted(provider);
                } else if (massageType == SocketService.SocketMassageType.dropped) {
                    _CurrentInvoice = new Invoice(jsonObject);
                    //Log.d("mmmmmm33", "setValueForLnrInvoice: " + jsonObject.toString());
                    flow = checkStatusFlow.dropped;
                    flowValue = 5;
                    lblStatus.setText(getString(R.string.finish));
                    layoutChanges();
                } else if (massageType == SocketService.SocketMassageType.bill) {

                    //Log.d("currentinvoiceee2", "doAfterReceiveSocketData: "+jsonObject.toString());
                    _CurrentInvoice = new Invoice(jsonObject);
                    flow = checkStatusFlow.bill;
                    flowValue = 5;
                    if (getString(R.string.finish) != null && lblStatus != null)
                        lblStatus.setText(getString(R.string.finish));
                    layoutChanges();
                    // flowValue = 51;
                } else if (massageType == SocketService.SocketMassageType.ComfirmPaid) {


                    flow = checkStatusFlow.confirmPaid;
                    flowValue = 6;
                    layoutChanges();

                    lblStatus.setText("Confirm Paid ");
                } else if (massageType == SocketService.SocketMassageType.TimeOut) {
                    btnRequestRideConfirm.setEnabled(true);
                    flow = checkStatusFlow.not;
                    waiting.setVisibility(View.GONE);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setMessage(R.string.request_timeout_massage);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog dialog = alertDialog.create();
                    if (Utilities.Trip_Type == 1) {
                        dialog.show();
                        Intent intent = new Intent(getActivity(), SocketService.class);
                        intent.setAction(SocketService.ACTION_STOP_FOREGROUND_SERVICE);
                        getActivity().startService(intent);
                        btnRequestRideConfirm.setText(R.string.request_now);
                    }
                    if (Utilities.Trip_Type == 0) {
                        SocketService.socket.connect();
                        Utilities.Trip_Type = 1;
                    }
                } else if (massageType == SocketService.SocketMassageType.Paid) {
                    flow = checkStatusFlow.not;
                    provider = new Provider(jsonObject);
                    flowValue = 6;
                    layoutChanges();
                }
            }
        };

        checkViewVisipilty("on start");
    }

    // Method To Initialize in Activity
    private void init(View rootView) {
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
//        <!-- Map frame -->
        mapLayout = rootView.findViewById(R.id.mapLayout); // Initalize Map In Home
        drawer = rootView.findViewById(R.id.drawer_layout); // Initalize Drowar
        drawer = activity.findViewById(R.id.drawer_layout); // View Drawer In Menu Btn in main
//        <!-- Source and Destination Layout-->
        sourceAndDestinationLayout = rootView.findViewById(R.id.sourceAndDestinationLayout);
        srcDestLayout = rootView.findViewById(R.id.sourceDestLayout);
        frmSource = rootView.findViewById(R.id.frmSource);
        frmDest = rootView.findViewById(R.id.frmDest);
        frmDestination = rootView.findViewById(R.id.frmDestination);
        destination = rootView.findViewById(R.id.destination);
        imgMenu = rootView.findViewById(R.id.imgMenu);
        imgSos = rootView.findViewById(R.id.imgSos);
        imgShareRide = rootView.findViewById(R.id.imgShareRide);
        mapfocus = rootView.findViewById(R.id.mapfocus);
        imgBack = rootView.findViewById(R.id.imgBack);
        shadowBack = rootView.findViewById(R.id.shadowBack);
        tripLine = rootView.findViewById(R.id.trip_line);
        destinationBorderImg = rootView.findViewById(R.id.dest_border_img);
//        <!-- Request to providers-->
        r_1 = rootView.findViewById(R.id.r_1);
        fragment_home_li_2_cancel_event = rootView.findViewById(R.id.fragment_home_li_2_cancel_event);
        fragment_home_li_3_cancel_event = rootView.findViewById(R.id.fragment_home_li_3_cancel_event);
        destinationLayer = rootView.findViewById(R.id.destinationLayer);
        lnrRequestProviders = rootView.findViewById(R.id.lnrRequestProviders);
        rcvServiceTypes = rootView.findViewById(R.id.rcvServiceTypes);
        imgPaymentType = rootView.findViewById(R.id.imgPaymentType);
        lblPaymentType = rootView.findViewById(R.id.lblPaymentType);
        lblPaymentChange = rootView.findViewById(R.id.lblPaymentChange);
        booking_id = rootView.findViewById(R.id.booking_id);
        btnRequestRides = rootView.findViewById(R.id.btnRequestRides);
        lnrSearchAnimation = rootView.findViewById(R.id.lnrSearch);
        lnrProviderPopup = rootView.findViewById(R.id.lnrProviderPopup);
        lnrPriceBase = rootView.findViewById(R.id.lnrPriceBase);
        lnrPricekm = rootView.findViewById(R.id.lnrPricekm);
        lnrPricemin = rootView.findViewById(R.id.lnrPricemin);
        lnrHidePopup = rootView.findViewById(R.id.lnrHidePopup);
        imgProviderPopup = rootView.findViewById(R.id.imgProviderPopup);
        lblServiceName = rootView.findViewById(R.id.lblServiceName);
        lblCapacity = rootView.findViewById(R.id.lblCapacity);
        lblPriceKm = rootView.findViewById(R.id.lblPriceKm);
        lblPriceMin = rootView.findViewById(R.id.lblPriceMin);
        lblCalculationType = rootView.findViewById(R.id.lblCalculationType);
        lblBasePricePopup = rootView.findViewById(R.id.lblBasePricePopup);
        btnDonePopup = rootView.findViewById(R.id.btnDonePopup);
//         <!--2. Approximate Rate ...-->
        waiting = rootView.findViewById(R.id.waiting);
        lnrApproximate = rootView.findViewById(R.id.lnrApproximate);
        imgSchedule = rootView.findViewById(R.id.imgSchedule);
        walletCheckBox = rootView.findViewById(R.id.chkWallet2);
        lblEta = rootView.findViewById(R.id.lblEta);
        lblDistance = rootView.findViewById(R.id.lblDistance);
        lblType = rootView.findViewById(R.id.lblType);
        lblApproxAmount = rootView.findViewById(R.id.lblApproxAmount);
        surgeDiscount = rootView.findViewById(R.id.surgeDiscount);
        surgeTxt = rootView.findViewById(R.id.surge_txt);
        btnRequestRideConfirm = rootView.findViewById(R.id.btnRequestRideConfirm);
        lineView = rootView.findViewById(R.id.lineView);

        //Schedule Layout
        ScheduleLayout = rootView.findViewById(R.id.ScheduleLayout);
        scheduleDate = rootView.findViewById(R.id.scheduleDate);
        scheduleTime = rootView.findViewById(R.id.scheduleTime);
        scheduleBtn = rootView.findViewById(R.id.scheduleBtn);

//         <!--3. Waiting For Providers ...-->

        lnrWaitingForProviders = rootView.findViewById(R.id.lnrWaitingForProviders);
        lblNoMatch = rootView.findViewById(R.id.lblNoMatch);
        btnCancelRide = rootView.findViewById(R.id.btnCancelRide);
        rippleBackground = rootView.findViewById(R.id.content);

//          <!--4. Driver Accepted ...-->
        lnrProviderAccepted = rootView.findViewById(R.id.lnrProviderAccepted);
        lnrAfterAcceptedStatus = rootView.findViewById(R.id.lnrAfterAcceptedStatus);
        AfterAcceptButtonLayout = rootView.findViewById(R.id.AfterAcceptButtonLayout);
        imgProvider = rootView.findViewById(R.id.imgProvider);
        imgServiceRequested = rootView.findViewById(R.id.imgServiceRequested);
        lblProvider = rootView.findViewById(R.id.lblProvider);
        lblStatus = rootView.findViewById(R.id.lblStatus);
        lblSurgePrice = rootView.findViewById(R.id.lblSurgePrice);
        lblServiceRequested = rootView.findViewById(R.id.lblServiceRequested);
        lblModelNumber = rootView.findViewById(R.id.lblModelNumber);
        ratingProvider = rootView.findViewById(R.id.ratingProvider);
        btnCall = rootView.findViewById(R.id.btnEdit);
        btnCancelTrip = rootView.findViewById(R.id.btnCancelTrip);
        fragment_home_btn_cancle = rootView.findViewById(R.id.fragment_home_btn_cancle);


//         <!--5. Invoice Layout ...-->
        lnrInvoice = rootView.findViewById(R.id.lnrInvoice);
        lblExtraPrice = rootView.findViewById(R.id.lblExtraPrice);
        lblTotalPrice = rootView.findViewById(R.id.lblTotalPrice);

        txt04BasePrice = rootView.findViewById(R.id.txt04BasePrice);
        txt04Distance = rootView.findViewById(R.id.txt04Distance);
        txt04Distance_amount = rootView.findViewById(R.id.txt04Distance_amount);
        txt04Waiting = rootView.findViewById(R.id.txt04Waiting);
        txt04Time = rootView.findViewById(R.id.txt04Time);
        txt04Waiting_amount = rootView.findViewById(R.id.txt04Waiting_amount);
        txt04Time_amount = rootView.findViewById(R.id.txt04Time_amount);
        txt04Tax = rootView.findViewById(R.id.txt04Tax);

        lblPaymentTypeInvoice = rootView.findViewById(R.id.lblPaymentTypeInvoice);
        imgPaymentTypeInvoice = rootView.findViewById(R.id.imgPaymentTypeInvoice);
        btnPayNow = rootView.findViewById(R.id.btnPayNow);

//          <!--6. Rate provider Layout ...-->

        lnrRateProvider = rootView.findViewById(R.id.lnrRateProvider);
        lblProviderNameRate = rootView.findViewById(R.id.lblProviderName);
        imgProviderRate = rootView.findViewById(R.id.imgProviderRate);
        txtCommentsRate = rootView.findViewById(R.id.txtComments);
        ratingProviderRate = rootView.findViewById(R.id.ratingProviderRate);
        btnSubmitReview = (MyButton) rootView.findViewById(R.id.btnSubmitReview);

//          <!--Static marker-->

        rtlStaticMarker = rootView.findViewById(R.id.rtlStaticMarker);
        imgDestination = rootView.findViewById(R.id.imgDestination);
        btnDone = rootView.findViewById(R.id.btnDone);

        // Invoke Method Get Info Payment Card
        getCards();

        // Invoke Method Status GPS
        statusCheck();

        handleCheckStatus = new Handler();


        HomeFragmentInitOnClickListener();

        layoutChanges();

        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slide_up_top = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_top);
        slide_up_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_down);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (fragment_home_li_2_cancel_event.getVisibility() == VISIBLE
                            || fragment_home_li_3_cancel_event.getVisibility() == VISIBLE
                            || lnrRequestProviders.getVisibility() == VISIBLE
                            || lnrProviderPopup.getVisibility() == VISIBLE
                            || lnrHidePopup.getVisibility() == VISIBLE
                            || lnrPriceBase.getVisibility() == VISIBLE
                            || lnrPricemin.getVisibility() == VISIBLE
                            || lnrPricekm.getVisibility() == VISIBLE
                            || lnrApproximate.getVisibility() == VISIBLE
                            || waiting.getVisibility() == VISIBLE
                            || ScheduleLayout.getVisibility() == VISIBLE
                            || lnrWaitingForProviders.getVisibility() == VISIBLE
                            || lnrProviderAccepted.getVisibility() == VISIBLE
                            || lnrInvoice.getVisibility() == VISIBLE
                            || rtlStaticMarker.getVisibility() == VISIBLE
                            || lnrRateProvider.getVisibility() == VISIBLE) {

                    } else {
                        if (!reqStatus.equalsIgnoreCase("SEARCHING")) {
                            utils.print("", "Back key pressed!");
                            if (lnrRequestProviders.getVisibility() == VISIBLE) {
                                flowValue = 0;
                                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(15.3f).build();
                                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }

                            } else if (flowValue == 4) {

                            } else if (lnrProviderPopup.getVisibility() == VISIBLE) {
                                flowValue = 1;
                            } else if (lnrApproximate.getVisibility() == VISIBLE) {
                                flowValue = 1;
                            } else if (lnrWaitingForProviders.getVisibility() == VISIBLE) {
                                flowValue = 2;
                            } else if (ScheduleLayout.getVisibility() == VISIBLE) {
                                flowValue = 2;
                            } else if (rtlStaticMarker.getVisibility() == VISIBLE) {
                                flowValue = 10;
                            } else {

                                if (MainActivity.drawer.isDrawerOpen(GravityCompat.START)) {
                                    MainActivity.drawer.closeDrawers();
                                    return true;

                                } else if (fragment_home_li_2_cancel_event.getVisibility() == VISIBLE
                                        || fragment_home_li_3_cancel_event.getVisibility() == VISIBLE
                                        || lnrRequestProviders.getVisibility() == VISIBLE
                                        || lnrProviderPopup.getVisibility() == VISIBLE
                                        || lnrHidePopup.getVisibility() == VISIBLE
                                        || lnrPriceBase.getVisibility() == VISIBLE
                                        || lnrPricemin.getVisibility() == VISIBLE
                                        || lnrPricekm.getVisibility() == VISIBLE
                                        || lnrApproximate.getVisibility() == VISIBLE
                                        || waiting.getVisibility() == VISIBLE
                                        || ScheduleLayout.getVisibility() == VISIBLE
                                        || lnrWaitingForProviders.getVisibility() == VISIBLE
                                        || lnrProviderAccepted.getVisibility() == VISIBLE
                                        || lnrInvoice.getVisibility() == VISIBLE
                                        || rtlStaticMarker.getVisibility() == VISIBLE
                                        || lnrRateProvider.getVisibility() == VISIBLE) {


                                } else {
                                    getActivity().onBackPressed();
                                }
                                // getActivity().onBackPressed();
                                destroy_App(); // Destroy Lat & Lng in DB

                            }

                        }

                        //Log.d("sooookey", "onKey: " + flowValue);
                        layoutChanges();
                        return true;
                    }
                }
                return false;
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(String t, String d) throws ParseException {
        long time = getMillieSecondes(t, d);

        Intent alarmIntent = new Intent(getContext(), SchedualTripService.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);


        AlarmManager a = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        a.set(AlarmManager.RTC, System.currentTimeMillis() + time, pendingIntent);


    }

    private long getMillieSecondes(String t, String d) throws ParseException {
        String toParse = d + " " + t; // Results in "2-5-2012 20:43"
        SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm"); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = formatter.parse(toParse); // You will need try/catch around this
        long millis = date.getTime();

        return millis;
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getContext(), SchedualTripService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    // Method Initialize All Elements Clicked Listener
    private void HomeFragmentInitOnClickListener() {

        btnRequestRides.setOnClickListener(new OnClick());
        btnDonePopup.setOnClickListener(new OnClick());
        lnrHidePopup.setOnClickListener(new OnClick());
        btnRequestRideConfirm.setOnClickListener(new OnClick());
        btnCancelRide.setOnClickListener(new OnClick());
        btnCancelTrip.setOnClickListener(new OnClick());
        fragment_home_btn_cancle.setOnClickListener(new OnClick());
        btnCall.setOnClickListener(new OnClick());
        btnPayNow.setOnClickListener(new OnClick());
        btnSubmitReview.setOnClickListener(new OnClick());
        btnDone.setOnClickListener(new OnClick());
        frmDestination.setOnClickListener(new OnClick());
        frmDest.setOnClickListener(new OnClick());
        lblPaymentChange.setOnClickListener(new OnClick());
        frmSource.setOnClickListener(new OnClick());
        imgMenu.setOnClickListener(new OnClick());
        mapfocus.setOnClickListener(new OnClick());
        imgSchedule.setOnClickListener(new OnClick());
        imgBack.setOnClickListener(new OnClick());
        scheduleBtn.setOnClickListener(new OnClick());
        scheduleDate.setOnClickListener(new OnClick());
        scheduleTime.setOnClickListener(new OnClick());
        imgProvider.setOnClickListener(new OnClick());
        imgProviderRate.setOnClickListener(new OnClick());
        imgSos.setOnClickListener(new OnClick());
        imgShareRide.setOnClickListener(new OnClick());

        lnrRequestProviders.setOnClickListener(new OnClick());
        lnrProviderPopup.setOnClickListener(new OnClick());
        ScheduleLayout.setOnClickListener(new OnClick());
        lnrApproximate.setOnClickListener(new OnClick());
        lnrProviderAccepted.setOnClickListener(new OnClick());
        lnrInvoice.setOnClickListener(new OnClick());
        lnrRateProvider.setOnClickListener(new OnClick());
        lnrWaitingForProviders.setOnClickListener(new OnClick());

    }


    @SuppressLint("ResourceType")
    @SuppressWarnings("MissingPermission")
    void initMap() {

        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
            myLocationButton = mapFragment.getView().findViewById(0x2);
        }

        if (mMap != null) {
            setupMap();
        }

    }

    @SuppressWarnings("MissingPermission")
    void setupMap() {
        if (mMap != null) {

            mMap.getUiSettings().setCompassEnabled(false);
            mMap.setBuildingsEnabled(true);
            mMap.setMyLocationEnabled(false);
            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraMoveListener(this);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setTiltGesturesEnabled(false);

        }

    }

    // Method Get Current Location User
    void currentLocation() {

        // Sent A S-latitude & S-longitude To Server
        JSONObject js = new JSONObject();
        try {
            if (source_lat != "" && source_lng != "") {
                js.put("latitude", Double.parseDouble(source_lat));
                js.put("longitude", Double.parseDouble(source_lng));
                js.put("device_mac", Utilities.getMacAddr());
                js.put("lang", Utilities.getDeviceLocal());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.updatelocation, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

        App.getInstance().addToRequestQueue(postRequest); // Passing In Queue in Requests

    }

    double x(double newlat, double newlong, double oldlat, double oldlong) {

        double r = 6371;

        double dlat = deg2rad(oldlat - newlat);
        double dlon = deg2rad(oldlong - newlong);

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(deg2rad(newlat) / 2) * Math.cos(deg2rad(oldlat)) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }

    double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    // Method Orready Invoked When Change Location Device
    @Override
    public void onLocationChanged(Location location) {

        // Remove Current Marker
        if (marker != null) {
            marker.remove();
        }
        Utilities.mlocation = location;

        // Check KM Length
        if (Double.parseDouble(String.valueOf(location.getLatitude())) != oldlat &&
                Double.parseDouble(String.valueOf(location.getLongitude())) != oldlong) {

            double km = x(location.getLatitude(), location.getLongitude(), oldlat, oldlong);

            if (km * 1000 >= 50.0) {

                oldlat = Double.parseDouble(String.valueOf(location.getLatitude()));

                oldlong = Double.parseDouble(String.valueOf(location.getLongitude()));
            }

            getProvider();

        }

        // Adding A Marker Current Location
        if (location != null && location.getLatitude() != 0 && location.getLongitude() != 0) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.75f)
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location));
            marker = mMap.addMarker(markerOptions); // Adding

            // Invoke Method liveNavigation To Check Status Ride After 5ms
            // And Get All Cars Or One Car When Started Ride
            if (!!statusProviders.equals("ride_accepted") || !statusProviders.equals("ride_started") || !statusProviders.equals("ride_picked") || !statusProviders.equals("ride_arrived")) {
                if (statusProviders.equals("") || statusProviders.equals("ride_dropped")) {
                    liveNavigation("", "1", "1", SharedHelper.getKey(context, "service_type"));
                }
            }

            // Print Lat & Long In Log

            if (current_lat.equalsIgnoreCase("") && current_lng.equalsIgnoreCase("")) {

                // When Changed Current Location Set New Lat & Long in New Var
                current_lat = "" + location.getLatitude(); // Set Lat Current
                current_lng = "" + location.getLongitude(); // Set Long Current
            }

            // Check Lat & Long != null
            if (source_lat.equalsIgnoreCase("") || source_lat.length() < 0) {
                source_lat = current_lat;
            }
            if (source_lng.equalsIgnoreCase("") || source_lng.length() < 0) {
                source_lng = current_lng;
            }


            // Zooming In Camera in Current Location in The Map in One Time Only When Run App
            if (value == 0) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(15.3f).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setPadding(0, 0, 0, 0);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);

                latitude = location.getLatitude();
                longitude = location.getLongitude();
                currentAddressEn = utils.getCompleteAddressString(context, latitude, longitude);
                currentAddressAr = utils.getCompleteAddressStringAr(context, latitude, longitude);
                source_lat = "" + latitude;
                source_lng = "" + longitude;
                source_addressEN = currentAddressEn;
                source_addressAr = currentAddressAr;
                current_address = currentAddressEn;
                current_addressAr = currentAddressAr;
                if (Utilities.getDeviceLocal().equals("ar")) {
                    frmSource.setText(currentAddressAr);
                } else {
                    frmSource.setText(currentAddressEn);
                }

                value++;
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
            }


        }
    }

    // Created A OnClick Class To Handling Clickable Items
    class OnClick implements View.OnClickListener {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frmDestination: // If Click EditText Dest Address Search in Main Page
                    if (Utilities.isNetworkAvailable(getActivity())) {
                        SharedPreferences prefsFrmDestination = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE);
                        Boolean isClicked1 = prefsFrmDestination.getBoolean(ONE_TIME_EXECUTE_VALUE, false);

                        if (!isClicked1){
                            Intent intent2 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                            intent2.putExtra("cursor", "destination");
                            intent2.putExtra("s_address", frmSource.getText().toString());
                            intent2.putExtra("d_address", destination.getText().toString());
                            intent2.putExtra("d_address", frmDest.getText().toString());
                            startActivityForResult(intent2, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                        }
                        SharedPreferences.Editor editor1 = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE).edit();
                        editor1.putBoolean(ONE_TIME_EXECUTE_VALUE , true);
                        editor1.apply();
                    } else {
                        showNetWorkDialog();
                    }

                    break;
                case R.id.frmSource: // If Click EditText Source Address Search
                    SharedPreferences prefsFrmSource = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE);
                    Boolean isClicked2 = prefsFrmSource.getBoolean(ONE_TIME_EXECUTE_VALUE, false);

                    if (!isClicked2){
                        Intent intent = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                        intent.putExtra("cursor", "source");
                        intent.putExtra("s_address", frmSource.getText().toString());
                        intent.putExtra("d_address", destination.getText().toString());
                        intent.putExtra("d_address", frmDest.getText().toString());
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    }

                    SharedPreferences.Editor editor2 = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE).edit();
                    editor2.putBoolean(ONE_TIME_EXECUTE_VALUE , true);
                    editor2.apply();

                    break;
                case R.id.frmDest: // If Click EditText Dest Address Search
                    SharedPreferences prefs2FrmDest = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE);
                    Boolean isClicked3 = prefs2FrmDest.getBoolean(ONE_TIME_EXECUTE_VALUE, false);

                    if (!isClicked3){
                        Intent intent3 = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                        intent3.putExtra("cursor", "destination");
                        intent3.putExtra("s_address", frmSource.getText().toString());
                        intent3.putExtra("d_address", destination.getText().toString());
                        intent3.putExtra("d_address", frmDest.getText().toString());
                        startActivityForResult(intent3, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                    }


                    SharedPreferences.Editor editor3 = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE).edit();
                    editor3.putBoolean(ONE_TIME_EXECUTE_VALUE , true);
                    editor3.apply();

                    break;
                case R.id.lblPaymentChange: // Clicked Change Payment After Searching
                    showChooser(); //
                    break;
                case R.id.btnRequestRides:

                    scheduledDate = "";
                    scheduledTime = "";

                    if (!frmSource.getText().toString().equalsIgnoreCase("") && !dest_address.equals("")) {
                        getApproximateFare();
                        frmDest.setOnClickListener(null);
                        frmSource.setOnClickListener(null);
                        srcDestLayout.setOnClickListener(new OnClick());
                    } else {
                        Toast.makeText(context, (R.string.enter_both_sides), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnRequestRideConfirm:

                    if (btnRequestRideConfirm.getText().toString().equals(getString(R.string.cancel_ride))) {
                        if (getActivity()!=null){
                            getActivity().getSharedPreferences("Cancel_trip_btn",MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("Cancel_trip_btn_value",false)
                                    .apply();
                        }
                        showCancelRideDialog(true);
                    } else {
                        btnRequestRideConfirm.setEnabled(true);

                        SharedHelper.putKey(context, "name", "");
                        scheduledDate = "";
                        scheduledTime = "";
                        sendRequest();
                    }
                    break;
                case R.id.btnPayNow:
                    // you should uncomment this fun
                    payNow();

                    break;
                case R.id.btnSubmitReview:
                    flowValue = 0;
                    layoutChanges();
                    rateProvider();
                    break;
                case R.id.lnrHidePopup:
                case R.id.btnDonePopup:
                    lnrHidePopup.setVisibility(View.GONE);
                    flowValue = 1;
                    layoutChanges();
                    click = 1;
                    break;
                case R.id.btnCancelRide:
                    if (getActivity()!=null){
                        getActivity().getSharedPreferences("Cancel_trip_btn",MODE_PRIVATE)
                                .edit()
                                .putBoolean("Cancel_trip_btn_value",false)
                                .apply();
                    }
                    showCancelRideDialog(false);
                    break;

                case R.id.btnCancelTrip:
                    if (btnCancelTrip.getText().toString().equals(getString(R.string.cancel_trip))) {
                        if (getActivity()!=null){
                            getActivity().getSharedPreferences("Cancel_trip_btn",MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("Cancel_trip_btn_value",true)
                                    .apply();
                        }
                        showCancelRideDialog(false);
                    }
                    else {
                        String shareUrl = URLHelper.REDIRECT_SHARE_URL;
                        navigateToShareScreen(shareUrl);
                    }

                    break;

                case R.id.fragment_home_btn_cancle:
                    if (fragment_home_btn_cancle.getText().toString().equals(getString(R.string.cancel_trip))) {

                        if (getActivity()!=null){
                            getActivity().getSharedPreferences("Cancel_trip_btn",MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("Cancel_trip_btn_value",false)
                                    .apply();
                        }

                        showCancelRideDialog(false);
                        cancelRequest();

                    } else {
                        String shareUrl = URLHelper.REDIRECT_SHARE_URL;
                        navigateToShareScreen(shareUrl);
                    }
                    break;

                case R.id.imgSos:
                    showSosPopUp();
                    break;
                case R.id.imgShareRide:
                    String url = "http://maps.google.com/maps?q=loc:";
                    navigateToShareScreen(url);
                    break;
                case R.id.imgProvider:
                    Intent intent1 = new Intent(activity, ShowProfile.class);
                    intent1.putExtra("driver", driver);
                    startActivity(intent1);
                    break;
                case R.id.imgProviderRate:
                    Intent intent4 = new Intent(activity, ShowProfile.class);
                    intent4.putExtra("driver", driver);
                    startActivity(intent4);
                    break;
                case R.id.btnEdit: {
                    Intent intentCall = new Intent(Intent.ACTION_DIAL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intentCall);
                }
                break;
                case R.id.btnDone:
                    pick_first = true;

                    // Remove PolyLine
                    if (polylines != null) {
                        // Invoke Method To Remove Polyline Place
                        eraesPolylines();
                    }
                    try {

                        utils.print("centerLat", cmPosition.target.latitude + "");
                        utils.print("centerLong", cmPosition.target.longitude + "");

                        Geocoder geocoderAr = null, geocoderEn = null;
                        List<Address> addressesAr, addressesEn;


                        geocoderAr = new Geocoder(getActivity(), new Locale("ar"));
                        geocoderEn = new Geocoder(getActivity(), Locale.ENGLISH);

                        // Invoke Method To Drawing PolyLine by Pick Point :)
                        getRouteToMarker(Double.parseDouble(source_lat),
                                Double.parseDouble(source_lng),
                                cmPosition.target.latitude,
                                cmPosition.target.longitude);

                        String city = "", state = "", address = "";
                        String cityen = "", stateen = "", addressen = "";

                        try {
                            addressesAr = geocoderAr.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            addressesEn = geocoderEn.getFromLocation(cmPosition.target.latitude, cmPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addressesAr.get(0).getAddressLine(0);
                            city = addressesAr.get(0).getLocality();
                            state = addressesAr.get(0).getAdminArea();
                            addressen = addressesEn.get(0).getAddressLine(0);
                            cityen = addressesEn.get(0).getLocality();
                            stateen = addressesEn.get(0).getAdminArea();
                            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // If Select Pickup Source
                        if (strPickType.equalsIgnoreCase("source")) {
                            source_addressEN = "" + addressen + "," + cityen + "," + stateen;
                            source_addressAr = "" + address + "," + city + "," + state;
                            source_lat = "" + cmPosition.target.latitude;
                            source_lng = "" + cmPosition.target.longitude;
                            if (dest_lat.equalsIgnoreCase("")) {
                                Toast.makeText(context, "Select destination", Toast.LENGTH_SHORT).show();
                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                if (ViewCompat.getLayoutDirection(viewContainer) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                                    // Arabic
                                    intentDest.putExtra("s_address", source_addressAr);
                                }
                                else{
                                    // English
                                    intentDest.putExtra("s_address", source_addressEN);
                                }
                                //intentDest.putExtra("s_address", source_addressEN);
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {
                                source_lat = "" + cmPosition.target.latitude;
                                source_lng = "" + cmPosition.target.longitude;

                                mMap.clear();
                                flowValue = 1;
                                layoutChanges();
                                strPickLocation = "";
                                strPickType = "";
                                getServiceList();

                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(cmPosition.target.latitude,
                                        cmPosition.target.longitude));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15.3f);
                                mMap.moveCamera(center);
                                mMap.moveCamera(zoom);

                            } // End Else Check PickUp Is Empty
                        } else {
                            dest_lat = "" + cmPosition.target.latitude;
                            if (dest_lat.equalsIgnoreCase(source_lat)) {
                                Toast.makeText(context, "Both source and destination are same", Toast.LENGTH_SHORT).show();

                                Intent intentDest = new Intent(getActivity(), CustomGooglePlacesSearch.class);
                                intentDest.putExtra("cursor", "destination");
                                intentDest.putExtra("s_address", frmSource.getText().toString());
                                startActivityForResult(intentDest, PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST);
                            } else {

                                // Remove Marker Dest
                                if (destPickMarker != null)
                                    destPickMarker.remove();

                                dest_addressAR = "" + address + "," + city + "," + state;
                                dest_address = "" + addressen + "," + cityen + "," + stateen;
                                dest_lat = "" + cmPosition.target.latitude;
                                dest_lng = "" + cmPosition.target.longitude;

                                mMap.clear();
                                flowValue = 1;
                                layoutChanges();
                                strPickLocation = "";
                                strPickType = "";
                                getServiceList();


                                // Set Icon Destination Place In Map By PickPoint
                                LatLng location = new LatLng(cmPosition.target.latitude, cmPosition.target.longitude);
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .anchor(0.5f, 0.75f)
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                                destPickMarker = mMap.addMarker(markerOptions);


                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(marker.getPosition());
                                builder.include(location);
                                LatLngBounds bounds = builder.build();

                                int padding = 150; // offset from edges of the map in pixels
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.animateCamera(cu);

                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(11.0f);
                                mMap.moveCamera(cu);
                                mMap.moveCamera(zoom);

                            }
                        } // End Else Check PickUp Select Source
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Can't able to get the address!.Please try again", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.imgBack:
                    if (lnrRequestProviders.getVisibility() == VISIBLE) {
                        flowValue = 0;
                        srcDestLayout.setVisibility(View.GONE);
                        frmSource.setOnClickListener(new OnClick());
                        frmDest.setOnClickListener(new OnClick());
                        srcDestLayout.setOnClickListener(null);
                        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                            destinationBorderImg.setVisibility(VISIBLE);
                            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                            //camera zoom
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(25f).build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            srcDestLayout.setVisibility(View.GONE);
                        }
                    } else if (lnrApproximate.getVisibility() == VISIBLE) {
                        frmSource.setOnClickListener(new OnClick());
                        frmDest.setOnClickListener(new OnClick());
                        srcDestLayout.setOnClickListener(null);
                        flowValue = 1;
                    } else if (lnrWaitingForProviders.getVisibility() == VISIBLE) {
                        flowValue = 2;
                    } else if (ScheduleLayout.getVisibility() == VISIBLE) {
                        flowValue = 2;
                    }
                    layoutChanges();
                    break;
                case R.id.imgMenu:
                    if (NAV_DRAWER == 0) {
                        if (drawer != null)
                            drawer.openDrawer(GravityCompat.START);
                    } else {
                        NAV_DRAWER = 0;
                        if (drawer != null)
                            drawer.closeDrawers();
                    }
                    break;
                case R.id.mapfocus:
                    Double crtLat, crtLng;
                    if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                        crtLat = Double.parseDouble(current_lat);
                        crtLng = Double.parseDouble(current_lng);

                        if (crtLat != null && crtLng != null) {
                            LatLng loc = new LatLng(crtLat, crtLng);

                            CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(15.3f).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mapfocus.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case R.id.imgSchedule:
                    flowValue = 7;
                    layoutChanges();

                    break;
                case R.id.scheduleBtn:
                    SharedHelper.putKey(context, "name", "");
                    if (scheduledDate != "" && scheduledTime != "") {
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long milliseconds = date.getTime();
                        if (!DateUtils.isToday(milliseconds)) {
                            try {
                                startAlarm(scheduledTime, scheduledDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            sendSechedualRequest();
                        } else {
                            if (utils.checktimings(scheduledTime)) {

                                sendSechedualRequest();
                            } else {
                                Toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.scheduleDate:
                    // calender class's instance and get current date , month and year from calender
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); // current year
                    int mMonth = c.get(Calendar.MONTH); // current month
                    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                    // date picker dialog
                    datePickerDialog = new DatePickerDialog(activity,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    // set day of month , month and year value in the edit text
                                    String choosedMonth = "";
                                    String choosedDate = "";
                                    String choosedDateFormat = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                    scheduledDate = choosedDateFormat;
                                    try {
                                        choosedMonth = utils.getMonth(choosedDateFormat);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (dayOfMonth < 10) {
                                        choosedDate = "0" + dayOfMonth;
                                    } else {
                                        choosedDate = "" + dayOfMonth;
                                    }
                                    afterToday = utils.isAfterToday(year, monthOfYear, dayOfMonth);
                                    scheduleDate.setText(choosedDate + " " + choosedMonth + " " + year);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.getDatePicker().setMaxDate((System.currentTimeMillis() - 1000) + (1000 * 60 * 60 * 24 * 7));
                    datePickerDialog.show();
                    break;
                case R.id.scheduleTime:
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                        int callCount = 0;   //To track number of calls to onTimeSet()

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            if (callCount == 0) {
                                String choosedHour = "";
                                String choosedMinute = "";
                                String choosedTimeZone = "";
                                String choosedTime = "";

                                scheduledTime = selectedHour + ":" + selectedMinute;

                                if (selectedHour > 12) {
                                    choosedTimeZone = "PM";
                                    selectedHour = selectedHour - 12;
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                } else {
                                    choosedTimeZone = "AM";
                                    if (selectedHour < 10) {
                                        choosedHour = "0" + selectedHour;
                                    } else {
                                        choosedHour = "" + selectedHour;
                                    }
                                }

                                if (selectedMinute < 10) {
                                    choosedMinute = "0" + selectedMinute;
                                } else {
                                    choosedMinute = "" + selectedMinute;
                                }
                                choosedTime = choosedHour + ":" + choosedMinute + " " + choosedTimeZone;

                                if (scheduledDate != "" && scheduledTime != "") {
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(scheduledDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    long milliseconds = date.getTime();
                                    if (!DateUtils.isToday(milliseconds)) {
                                        scheduleTime.setText(choosedTime);
                                    } else {
                                        if (utils.checktimings(scheduledTime)) {
                                            scheduleTime.setText(choosedTime);
                                        } else {
                                            Toast toast = new Toast(activity);
                                            toast.makeText(activity, getString(R.string.different_time), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(activity, getString(R.string.choose_date_time), Toast.LENGTH_SHORT).show();
                                }
                            }
                            callCount++;
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    break;
            }
        } // End OnClick
    } // End Class OnClick

    private void rateProvider() {


        JSONObject parm = new JSONObject();
        try {
            String trip_id = SharedHelper.getKey(context, "request_id");

            parm.put("trip_id", trip_id);
            parm.put("rating", feedBackRating);
            parm.put("comment", txtCommentsRate.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.Comment, parm, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent i = new Intent(activity, CongratulationActivity.class);
                i.putExtra("trip", "con");
                getActivity().startActivity(i);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Cannot Rate the provider ", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getContext(), "access_token"));
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

    public void navigateToShareScreen(String shareUrl) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String name = SharedHelper.getKey(context, "first_name") + " " + SharedHelper.getKey(context, "last_name");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "TRANXIT-" + "Mr/Mrs." + name + " would like to share a ride with you at " +
                    shareUrl + current_lat + "," + current_lng);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSosPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.emaergeny_call))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                {
                    Intent intentCall = new Intent(Intent.ACTION_DIAL);
                    intentCall.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intentCall);
                }
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCancelRideDialog(final boolean b) {
//        loadingDialog.setContentView(R.layout.dialog_edit);
//        loadingDialog.setCancelable(false);
//        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        LinearLayout yes = loadingDialog.findViewById(R.id.yes);
//        LinearLayout no = loadingDialog.findViewById(R.id.no);
//        TextView msg = loadingDialog.findViewById(R.id.msg);
//        TextView details = loadingDialog.findViewById(R.id.details);
//        details.setVisibility(View.GONE);
//        msg.setText(R.string.cancel_ride_alert);
//
//        yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                loadingDialog.dismiss();
//                destroy_App(); // Destroy Lat & Lng in DB
//                showreasonDialog(b);
//
//
//            }
//        });
//        no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadingDialog.dismiss();
//            }
//        });
////        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
////            @Override
////            public void onCancel(DialogInterface dialog) {
////                dialog.dismiss();
////            }
////        });
//        loadingDialog.show();

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_edit, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        LinearLayout yes = view.findViewById(R.id.yes);
        LinearLayout no = view.findViewById(R.id.no);
        TextView msg = view.findViewById(R.id.msg);
        TextView details = view.findViewById(R.id.details);
        details.setVisibility(View.GONE);
        msg.setText(R.string.cancel_ride_alert);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroy_App(); // Destroy Lat & Lng in DB
                showreasonDialog(b);
                dialog.dismiss();

            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = alertDialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        builder.setTitle(context.getString(R.string.app_name))
//                .setIcon(R.mipmap.ic_launcher)
//                .setMessage(getString(R.string.cancel_ride_alert));
//        builder.setCancelable(false);
//        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                showreasonDialog(b);
//            }
//        });
//        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog alertDialog = builder.create();
    }


    private void showreasonDialog(final boolean b) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = (EditText) view.findViewById(R.id.reason_etxt);
        Button submitBtn = (Button) view.findViewById(R.id.submit_btn);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true);
        final AlertDialog dialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancalReason = reasonEtxt.getText().toString();
                JSONObject requestObj = new JSONObject();
                try {
                    btnRequestRideConfirm.setText(R.string.request_now);

                    requestObj.put("reason", cancalReason);
                    if (b) {
                        requestObj.put("provider_id", "0");
                        requestObj.put("Trip_id", "0");
                        requestObj.put("client_id", SharedHelper.getKey(getContext(), "id"));

                    } else {
                        requestObj.put("Trip_id", SharedHelper.getKey(getContext(), "request_id"));
                        requestObj.put("provider_id", provider.getId());
                        requestObj.put("client_id", SharedHelper.getKey(getContext(), "id"));
                        requestObj.put("first_name", SharedHelper.getKey(context, "first_name"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (SocketService.socket != null) {
                    SocketService.socket.emit("cancel_trip_user", requestObj);

                    Utilities.Trip_Type = 0;
                }

                SharedHelper.putKey(context, "request_id", "");
                //String fname = SharedHelper.getKey(context, "first_name");

                //TODO clear cashe data
                flowValue = 0;

                if (getActivity()!=null){

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Cancel_trip_btn",MODE_PRIVATE);
                    boolean is_canceled_trip_btn = sharedPreferences.getBoolean("Cancel_trip_btn_value",false);

                    //Toast.makeText(activity, is_canceled_trip_btn + "", Toast.LENGTH_SHORT).show();

                    if (is_canceled_trip_btn){
                        utils.displayMessage(getView(), getString(R.string.trip_is_canceled_successfully));
                        getActivity().startActivity(new Intent(getActivity(),MainActivity.class));
                    }else {
                        Intent intent = new Intent(getActivity(), CongratulationActivity.class);
                        intent.putExtra("trip", "cancel");
                        activity.finish();
                        activity.startActivity(intent);
                    }
                }

//                lnrProviderAccepted.setVisibility(View.GONE);
//                lnrAfterAcceptedStatus.setVisibility(View.GONE);
//                lnrProviderPopup.setVisibility(View.GONE);
//                lnrRequestProviders.setVisibility(View.GONE);

                //cancelRequest();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //Change Layout Wheen Click Set Pin Location
    void layoutChanges() {

        // try {
        utils.hideKeypad(getActivity(), getActivity().getCurrentFocus());
        if (lnrApproximate.getVisibility() == VISIBLE) {
//            lnrApproximate.startAnimation(slide_down);

        } else if (fragment_home_li_3_cancel_event != null) {
            if (fragment_home_li_3_cancel_event.getVisibility() == VISIBLE)
                fragment_home_li_3_cancel_event.startAnimation(slide_down);

        } else if (fragment_home_li_2_cancel_event.getVisibility() == VISIBLE) {
            fragment_home_li_2_cancel_event.startAnimation(slide_down);


        } else if (ScheduleLayout.getVisibility() == VISIBLE) {
            ScheduleLayout.startAnimation(slide_down);
        } else if (lnrRequestProviders.getVisibility() == VISIBLE) {
            lnrRequestProviders.startAnimation(slide_down);
        } else if (lnrProviderPopup.getVisibility() == VISIBLE) {
            lnrProviderPopup.startAnimation(slide_down);
            lnrSearchAnimation.startAnimation(slide_up_down);
            lnrSearchAnimation.setVisibility(VISIBLE);
        } else if (lnrInvoice.getVisibility() == VISIBLE) {
            lnrInvoice.startAnimation(slide_down);
        } else if (lnrRateProvider.getVisibility() == VISIBLE) {
            lnrRateProvider.startAnimation(slide_down);
        } else if (lnrInvoice.getVisibility() == VISIBLE) {
            lnrInvoice.startAnimation(slide_down);
        }

        lnrRequestProviders.setVisibility(View.GONE);
        lnrProviderPopup.setVisibility(View.GONE);
        lnrPriceBase.setVisibility(View.GONE);
        lnrPricekm.setVisibility(View.GONE);
        lnrPricemin.setVisibility(View.GONE);
        lnrHidePopup.setVisibility(View.GONE);
        lnrApproximate.setVisibility(View.GONE);
        lnrWaitingForProviders.setVisibility(View.GONE);
        lnrProviderAccepted.setVisibility(View.GONE);
        lnrInvoice.setVisibility(View.GONE);
        lnrRateProvider.setVisibility(View.GONE);
        ScheduleLayout.setVisibility(View.GONE);
        rtlStaticMarker.setVisibility(View.GONE);
        frmDestination.setVisibility(View.GONE);
        imgMenu.setVisibility(View.GONE);
        imgBack.setVisibility(View.GONE);
        shadowBack.setVisibility(View.GONE);
        txtCommentsRate.setText("");
        scheduleDate.setText("" + context.getString(R.string.sample_date));
        scheduleTime.setText("" + context.getString(R.string.sample_time));
        if (flowValue == 0) { // Main Mapping Without Attribute
            if (imgMenu.getVisibility() == View.GONE) {
                srcDestLayout.setVisibility(View.GONE);
                frmSource.setOnClickListener(new OnClick());
                frmDest.setOnClickListener(new OnClick());
                srcDestLayout.setOnClickListener(null);
                if (mMap != null) {
                    mMap.clear();
                    stopAnim();
                    setupMap();
                }
            }
            frmDestination.setVisibility(VISIBLE);
            imgMenu.setVisibility(VISIBLE);
            destination.setText("");
            frmDest.setText("");
            if (Utilities.getDeviceLocal().equals("ar"))
                frmSource.setText("" + current_addressAr);
            else
                frmSource.setText("" + current_address);
            dest_address = "";
            dest_lat = "";
            dest_lng = "";
            source_lat = "" + current_lat;
            source_lng = "" + current_lng;
            source_addressEN = "" + current_address;
            sourceAndDestinationLayout.setVisibility(VISIBLE);
        } else if (flowValue == 1) { // Request Btn Ride
            frmSource.setVisibility(VISIBLE);
            destinationBorderImg.setVisibility(View.GONE);
            frmDestination.setVisibility(VISIBLE);
            imgBack.setVisibility(VISIBLE);
//            lnrRequestProviders.startAnimation(slide_up);
            lnrRequestProviders.setVisibility(VISIBLE);
            if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                if (lineView != null && walletCheckBox != null) {
                    lineView.setVisibility(VISIBLE);
                }
            } else {
                if (lineView != null && walletCheckBox != null) {
                    lineView.setVisibility(View.GONE);
                }
            }

        } else if (flowValue == 2) { // Approximate Ride
            imgBack.setVisibility(VISIBLE);
//            lnrApproximate.startAnimation(slide_up);
            lnrApproximate.setVisibility(VISIBLE);
            if (sourceMarker != null && destinationMarker != null) {
                sourceMarker.setDraggable(false);
                destinationMarker.setDraggable(false);
            }
        } else if (flowValue == 3) { // Waiting For Providers
            imgBack.setVisibility(VISIBLE);
            lnrWaitingForProviders.setVisibility(VISIBLE);
            srcDestLayout.setVisibility(View.GONE);

        } else if (flowValue == 31) { // Waiting For Providers
            ScheduleLayout.setVisibility(View.GONE);
            imgBack.setVisibility(VISIBLE);
            reCreateMap();
            srcDestLayout.setVisibility(View.GONE);

        } else if (flowValue == 4) { // Layout Accepted Request Driver


                lnrProviderAccepted = rootView.findViewById(R.id.lnrProviderAccepted);
                //Log.d("aceeeeeeeeee", "before lnrProviderAccepted " + lnrProviderAccepted.getVisibility());
                //Log.d("aceeeeeeeeee", "before AfterAcceptButtonLayout " + AfterAcceptButtonLayout.getVisibility());
                //.d("aceeeeeeeeee", "before waiting " + waiting.getVisibility());
                //Log.d("aceeeeeeeeee", "before lnrInvoice " + lnrInvoice.getVisibility());
                //Log.d("aceeeeeeeeee", "before imgMenu " + imgMenu.getVisibility());
                //Log.d("aceeeeeeeeee", "before fragment_home_li_2_cancel_event " + fragment_home_li_2_cancel_event.getVisibility());
                //Log.d("soooooo222", stateString);

                AfterAcceptButtonLayout.setVisibility(VISIBLE);
                lnrProviderAccepted.setVisibility(VISIBLE);

            if (!stateString.equals("")) {
                    if (stateString.equals("pickedUp")) {

                        lblStatus.setText(getString(R.string.picked_up));
                        //Ali
                        btnCancelTrip.setVisibility(View.GONE);
                        lblStatus.setBackgroundColor(Color.blue(2));

                    }


                }


                if (lnrProviderAccepted != null) {
                    waiting.setVisibility(View.GONE);
                    lnrInvoice.setVisibility(View.GONE);
                    imgMenu.setVisibility(VISIBLE);

                    //  lnrProviderAccepted.startAnimation(slide_up);

                    //fragment_home_li_2_cancel_event.setVisibility(View.GONE);


                    //Log.d("aceeeeeeeeee", "after lnrProviderAccepted " + lnrProviderAccepted.getVisibility());
                    //Log.d("aceeeeeeeeee", "after AfterAcceptButtonLayout " + AfterAcceptButtonLayout.getVisibility());
                    //Log.d("aceeeeeeeeee", "after waiting " + waiting.getVisibility());
                    //Log.d("aceeeeeeeeee", "after lnrInvoice " + lnrInvoice.getVisibility());
                    //Log.d("aceeeeeeeeee", "after imgMenu " + imgMenu.getVisibility());
                    //Log.d("aceeeeeeeeee", "after fragment_home_li_2_cancel_event " + fragment_home_li_2_cancel_event.getVisibility());

                }

        } else if (flowValue == 70) {//Layout Cancel Request Driver

            AfterAcceptButtonLayout.setVisibility(VISIBLE);

            waiting.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            imgMenu.setVisibility(VISIBLE);
            //fragment_home_li_2_cancel_event.startAnimation(slide_up);
            //fragment_home_li_2_cancel_event.setVisibility(VISIBLE);


        } else if (flowValue == 880) {//provider_cancel_trip_after_waiting


            waiting.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            imgMenu.setVisibility(VISIBLE);
            fragment_home_li_2_cancel_event.startAnimation(slide_up);
            fragment_home_li_2_cancel_event.setVisibility(VISIBLE);
            Toast.makeText(activity, R.string.The_trip_was_canceled_by_the_companion_after_waiting_and_the_base_value_was_deducted, Toast.LENGTH_SHORT).show();

        } else if (flowValue == 980) {//user_cancel_trip_after_waiting
            waiting.setVisibility(View.GONE);
            lnrInvoice.setVisibility(View.GONE);
            imgMenu.setVisibility(VISIBLE);
            Toast.makeText(activity, R.string.The_trip_has_been_canceled_by_the_customer_and_the_basic_fare_will_be_added_to_your_account, Toast.LENGTH_SHORT).show();
            cancelRequest();

        } else if (flowValue == 710) {//Layout Cancel Request Driver
            Toast.makeText(activity, R.string.Your_order_has_been_canceled_Try_again, Toast.LENGTH_SHORT).show();
//            layoutChanges();
            fragment_home_li_2_cancel_event.setVisibility(View.GONE);
            onBack();


        } else if (flowValue == 5) { // Invoice Request Ride

            //Log.d("soooooo5", "layoutChanges: " + stateString);
            if (stateString.equals("ComfirmPaid")) {
                lnrInvoice.setVisibility(View.GONE);
                lnrRateProvider.setVisibility(VISIBLE);
            } else {
                lnrInvoice.setVisibility(VISIBLE);
                lnrRateProvider.setVisibility(View.GONE);
            }

            imgMenu.setVisibility(VISIBLE);
            // lnrInvoice.setVisibility(VISIBLE);
//           lnrInvoice.startAnimation(slide_up);

            setValueForLnrInvoice();
            // Remove Marker Dest
            if (destinationMarker != null)
                destinationMarker.remove();

        } else if (flowValue == 51) { // finish and rate Provider
//            lnrInvoice.startAnimation(slide_down);
            //Log.d("soooooo51", "layoutChanges: " + stateString);
            if (stateString.equals("bill")) {
                lnrInvoice.setVisibility(View.GONE);
                lnrRateProvider.setVisibility(VISIBLE);
            } else {
                lnrInvoice.setVisibility(VISIBLE);
                lnrRateProvider.setVisibility(View.GONE);
            }

//            lnrRateProvider.startAnimation(slide_up);

            // Remove Marker Dest
            if (destinationMarker != null)
                destinationMarker.remove();

        } else if (flowValue == 6) { // Rate Provider Request Ride
            lnrRateProvider.setVisibility(VISIBLE);
            Intent intent = new Intent(getActivity(), SocketService.class);
            intent.setAction(SocketService.ACTION_STOP_FOREGROUND_SERVICE);
            getActivity().startService(intent);
            if (provider != null) {

                lblProviderNameRate.setText(getString(R.string.rate_provider)
                        + " " + provider.getFirstName()
                        + " " + provider.getLastName());
                if (provider.getPicture().startsWith("http")){
                    //Toast.makeText(getActivity(),"http  : \n"+provider.getPicture() , Toast.LENGTH_SHORT).show();

//                    Picasso.with(context)
//                            .load(provider.getPicture())
//                            .placeholder(R.drawable.loading)
//                            .error(R.drawable.ic_dummy_user)
//                            .into(imgProviderRate);

                    Picasso.with(context)
                            .load(provider.getPicture())
                            .fit()
                            .centerCrop()
                            .into(imgProviderRate);
                } else{
                    //Toast.makeText(getActivity(),provider.getPicture() , Toast.LENGTH_SHORT).show();

//                    Picasso.with(context)
//                            .load(URLHelper.base_pic + provider.getPicture())
//                            .placeholder(R.drawable.loading)
//                            .error(R.drawable.ic_dummy_user)
//                            .into(imgProviderRate);

                    Picasso.with(context)
                            .load(URLHelper.base_pic + provider.getPicture())
                            .fit()
                            .centerCrop()
                            .into(imgProviderRate);
                }
            }


            imgMenu.setVisibility(VISIBLE);
            lnrRateProvider.startAnimation(slide_up);
            lnrRateProvider.setVisibility(VISIBLE);

            lnrRequestProviders.startAnimation(slide_down);
            lnrRequestProviders.setVisibility(View.GONE);

            LayerDrawable drawable = (LayerDrawable) ratingProviderRate.getProgressDrawable();
            drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
            drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
            drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
            ratingProviderRate.setRating(1.0f);
            feedBackRating = "1";
            ratingProviderRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {
                    if (rating < 1.0f) {
                        ratingProviderRate.setRating(1.0f);
                        feedBackRating = "1";
                    }
                    feedBackRating = String.valueOf((int) rating);
                }
            });
        } else if (flowValue == 7) { // Schedule Provider Ride Request
            imgBack.setVisibility(VISIBLE);
            ScheduleLayout.startAnimation(slide_up);
            ScheduleLayout.setVisibility(VISIBLE);
        } else if (flowValue == 8) {
            // clear all views
            shadowBack.setVisibility(View.GONE);
        } else if (flowValue == 9) { // PickdUp Destination Ride
            srcDestLayout.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(VISIBLE);
            shadowBack.setVisibility(View.GONE);
        } else if (flowValue == 10) { // When OnBackKey & Cancel Destination
            srcDestLayout.setVisibility(View.GONE);
            rtlStaticMarker.setVisibility(View.INVISIBLE);
            frmDestination.setVisibility(VISIBLE);
            imgMenu.setVisibility(VISIBLE);
        }
    }

    private void onBack() {
        getActivity().finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));

            if (!success) {
                utils.print("Map:Style", "Style parsing failed.");
            } else {
                utils.print("Map:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            utils.print("Map:Style", "Can't find style. Error: ");
        }

        mMap = googleMap;

        setupMap();

        // Check SDK & Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        AfterAcceptButtonLayout.setVisibility(View.GONE);
        lnrProviderAccepted.setVisibility(View.GONE);

        if (fragment_home_li_2_cancel_event.getVisibility() == VISIBLE
                || fragment_home_li_3_cancel_event.getVisibility() == VISIBLE
                || lnrRequestProviders.getVisibility() == VISIBLE
                || lnrProviderPopup.getVisibility() == VISIBLE
                || lnrHidePopup.getVisibility() == VISIBLE
                || lnrPriceBase.getVisibility() == VISIBLE
                || lnrPricemin.getVisibility() == VISIBLE
                || lnrPricekm.getVisibility() == VISIBLE
                || lnrApproximate.getVisibility() == VISIBLE
                || waiting.getVisibility() == VISIBLE
                || ScheduleLayout.getVisibility() == VISIBLE
                || lnrWaitingForProviders.getVisibility() == VISIBLE
                || lnrProviderAccepted.getVisibility() == VISIBLE
                || lnrInvoice.getVisibility() == VISIBLE
                || rtlStaticMarker.getVisibility() == VISIBLE) {

//            statesEd.putInt("flow",flowValue);
//            statesEd.commit();
        }

        checkViewVisipilty("on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        checkViewVisipilty("on stop");
        //Log.d("szzzzzz", flowValue + " flow");
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the rafiq *asynchronously* -- don't block
                // this thread waiting for the rafiq's response! After the rafiq
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the rafiq once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }

    //Build Api show Map
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        String title = "";
        if (marker != null && marker.getTitle() != null) {
            title = marker.getTitle();
            if (sourceMarker != null && title.equalsIgnoreCase("Source")) {
                LatLng markerLocation = sourceMarker.getPosition();
                Geocoder geocoderAr, geocoderEn;
                List<Address> addressesAr = null, addressesEn = null;
                geocoderAr = new Geocoder(getActivity(), new Locale("ar"));
                geocoderEn = new Geocoder(getActivity(), Locale.ENGLISH);

                source_lat = markerLocation.latitude + "";
                source_lng = markerLocation.longitude + "";

                try {
                    addressesAr = geocoderAr.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    addressesEn = geocoderEn.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addressesAr.size() > 0) {
                        String address = addressesAr.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addressesAr.get(0).getLocality();
                        String state = addressesAr.get(0).getAdminArea();
                        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "source", "" + address + "," + city + "," + state);
                        source_addressAr = "" + address + "," + city + "," + state;
                    }
                    if (addressesEn.size() > 0) {
                        String address = addressesEn.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addressesEn.get(0).getLocality();
                        String state = addressesEn.get(0).getAdminArea();
                        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "source", "" + address + "," + city + "," + state);
                        source_addressEN = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (destinationMarker != null && title.equalsIgnoreCase("Destination")) {
                LatLng markerLocation = destinationMarker.getPosition();
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());

                dest_lat = "" + markerLocation.latitude;
                dest_lng = "" + markerLocation.longitude;

                try {
                    addresses = geocoder.getFromLocation(markerLocation.latitude, markerLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        SharedHelper.putKey(context, "destination", "" + address + "," + city + "," + state);
                        dest_address = "" + address + "," + city + "," + state;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            mMap.clear();
            setValuesForSourceAndDestination();
        }
    }

    // Get Permission
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    initMap();
                    MapsInitializer.initialize(getActivity());
                }
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Show Dialog To Get Premission GPS
    private void showDialogForGPSIntent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builder.setTitle(context.getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("GPS is disabled in your device. Enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivity(callGPSSettingIntent);
                            }
                        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    // Get result when clicked any places in chose place
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DEST) {

            SharedPreferences.Editor editor = getActivity().getSharedPreferences(ONE_TIME_EXECUTE_FILE, MODE_PRIVATE).edit();
            editor.putBoolean(ONE_TIME_EXECUTE_VALUE , false);
            editor.apply();

            // Set Defult Current Location
            source_lat = current_lat;
            source_lng = current_lng;
            source_addressEN = current_address;
            //source_addressAr = current_address;
            source_addressAr = current_addressAr;

            if (parserTask != null) {
                parserTask = null;
            }
            //Remove Marker Source
            if (sourceMarker != null)
                sourceMarker.remove();
            // Remove Marker Dest
            if (destinationMarker != null)
                destinationMarker.remove();
            // Remove PolyLine
            if (polylines != null) {
                // Invoke Method To Remove Polyline Place
                eraesPolylines();
            }
            if (resultCode == Activity.RESULT_OK) {
                if (marker != null) {
                    //marker.remove();
                }
                PlacePredictions placePredictions;
                placePredictions = (PlacePredictions) data.getSerializableExtra("Location Address");
                strPickLocation = data.getExtras().getString("pick_location");
                strPickType = data.getExtras().getString("type");
                if (strPickLocation.equalsIgnoreCase("yes")) {
                    pick_first = true;
                    mMap.clear();
                    flowValue = 9;
                    layoutChanges();
                    float zoomLevel = 15.3f; //This goes up to 21
                    stopAnim();
                } else {
                    if (placePredictions != null) { // Selected Source & Destination New Places
                        if (!placePredictions.strSourceAddress.equalsIgnoreCase("")) { // Select New Source Location
                            source_lat = "" + placePredictions.strSourceLatitude;
                            source_lng = "" + placePredictions.strSourceLongitude;
                            source_addressEN = placePredictions.strSourceAddress;
                            source_addressAr = placePredictions.strSourceAddressar;
                            if (!placePredictions.strSourceLatitude.equalsIgnoreCase("")
                                    && !placePredictions.strSourceLongitude.equalsIgnoreCase("")) { // Checked Place Source Lan & Long Not Empty
                                double latitude = Double.parseDouble(placePredictions.strSourceLatitude);
                                double longitude = Double.parseDouble(placePredictions.strSourceLongitude);

                                // Location New User
                                LatLng location = new LatLng(latitude, longitude);

                                //mMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .anchor(0.5f, 0.75f)
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                                marker = mMap.addMarker(markerOptions);
                                sourceMarker = mMap.addMarker(markerOptions);
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(10.0f).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            }

                        }
                        if (!placePredictions.strDestAddress.equalsIgnoreCase("")) { // Checked Dest Location Not Empty
                            dest_lat = "" + placePredictions.strDestLatitude;
                            dest_lng = "" + placePredictions.strDestLongitude;
                            dest_address = placePredictions.strDestAddress;
                            dest_addressAR = placePredictions.strDestAddressar;
                            SharedHelper.putKey(context, "current_status", "2");
                            if (source_lat != null && source_lng != null && !source_lng.equalsIgnoreCase("")
                                    && !source_lat.equalsIgnoreCase("")) {
                                String url = getUrl(Double.parseDouble(source_lat), Double.parseDouble(source_lng)
                                        , Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                                FetchUrl fetchUrl = new FetchUrl();
                                fetchUrl.execute(url);
                                LatLng location = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                                if (source_addressEN.equals(current_address)) {
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .anchor(0.5f, 0.75f)
                                            .position(location).title(source_addressEN)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                                    marker = mMap.addMarker(markerOptions);
                                    sourceMarker = mMap.addMarker(markerOptions);
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(10.0f).build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                            }
                            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                                // Remove Marker Dest
                                if (destinationMarker != null)
                                    destinationMarker.remove();
                                MarkerOptions destMarker = new MarkerOptions()
                                        .position(destLatLng).title(dest_address)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                                destinationMarker = mMap.addMarker(destMarker);
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(sourceMarker.getPosition());
                                builder.include(destinationMarker.getPosition());
                                LatLngBounds bounds = builder.build();
                                int padding = 200; // offset from edges of the map in pixels
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.animateCamera(cu);
                            }
                        }
                        // Invoke Method To Drowing PolyLine :)
                        getRouteToMarker(Double.parseDouble(source_lat),
                                Double.parseDouble(source_lng),
                                Double.parseDouble(dest_lat),
                                Double.parseDouble(dest_lng));

                        // Getting Distance & Time Ride by Application
                        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                                source_addressEN + "&destinations=" + dest_address +
                                "&mode=driving&language=ar-AR&avoid=tolls&key=" + R.string.google_map_api;
                        getDistAndTime(url);
                        if (dest_address.equalsIgnoreCase("")) {
                            flowValue = 1;
                            frmSource.setText(source_addressEN);
                            getServiceList();
                        } else {
                            flowValue = 1;
                            if (cardInfoArrayList.size() > 0) {
                                getCardDetailsForPayment(cardInfoArrayList.get(0));
                            }
                            getServiceList();
                        }
                        layoutChanges();
                    } // End Checked placePredictions != 'null'
                } // End Else strPickLocation 'yes'
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The rafiq canceled the operation.
            }
        }
        if (requestCode == ADD_CARD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("isAdded", false);
                if (result) {
                    getCards();
                }
            }
        }
        if (requestCode == 5555) {
            if (resultCode == Activity.RESULT_OK) {
                CardInfo cardInfo = data.getParcelableExtra("card_info");
                getCardDetailsForPayment(cardInfo);
            }
        }
        if (requestCode == REQUEST_LOCATION) {

        }
    }
    // End onActivityResult


    // Show Details Services Providers
    void showProviderPopup(JSONObject jsonObject) {

        lnrSearchAnimation.startAnimation(slide_up_top);
        lnrSearchAnimation.setVisibility(View.GONE);
        lnrProviderPopup.setVisibility(VISIBLE);
        lnrRequestProviders.setVisibility(View.GONE);

        Picasso.with(activity).load(jsonObject.optString("image"))
                .placeholder(R.drawable.pickup_drop_icon)
                .error(R.drawable.pickup_drop_icon).into(imgProviderPopup);


        if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("HOUR")) {
            lnrPriceBase.setVisibility(VISIBLE);
            lnrPricemin.setVisibility(VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("MIN")) {
                lblCalculationType.setText("Minutes");
            } else {
                lblCalculationType.setText("Hours");
            }
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCE")) {
            lnrPriceBase.setVisibility(VISIBLE);
            lnrPricekm.setVisibility(VISIBLE);
            lblCalculationType.setText(jsonObject.optString("calculator"));
        } else if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")
                || jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEHOUR")) {
            lnrPriceBase.setVisibility(VISIBLE);
            lnrPricemin.setVisibility(VISIBLE);
            lnrPricekm.setVisibility(VISIBLE);
            if (jsonObject.optString("calculator").equalsIgnoreCase("DISTANCEMIN")) {
                lblCalculationType.setText("Distance and Minutes");
            } else {
                lblCalculationType.setText("Distance and Hours");
            }
        }
        if (!jsonObject.optString("capacity").equalsIgnoreCase("null")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lblCapacity.setText(jsonObject.optString("capacity"));
            } else {
                lblCapacity.setText(jsonObject.optString("capacity") + " peoples");
            }
        } else {
            lblCapacity.setVisibility(View.GONE);
        }
        lblServiceName.setText("" + jsonObject.optString("name"));
        lblBasePricePopup.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("fixed"));
        lblPriceKm.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("price"));
        lblPriceMin.setText(SharedHelper.getKey(context, "currency") + jsonObject.optString("minute"));
        if (jsonObject.optString("description").equalsIgnoreCase("null")) {
            lblCapacity.setVisibility(View.GONE);
        } else {
            lblCapacity.setVisibility(VISIBLE);
            lblCapacity.setText("" + jsonObject.optString("description"));
        }
    }

    // Show Deatils Approximated
    public void setValuesForApproximateLayout() {
        if (isInternet) {
            String surge = SharedHelper.getKey(context, "surge");
            if (surge.equalsIgnoreCase("1")) {
                surgeDiscount.setVisibility(VISIBLE);
                surgeTxt.setVisibility(VISIBLE);
                surgeDiscount.setText(SharedHelper.getKey(context, "surge_value"));
            } else {
                surgeDiscount.setVisibility(View.GONE);
                surgeTxt.setVisibility(View.GONE);
            }
            lblApproxAmount.setText(SharedHelper.getKey(context, "currency") + "  " + SharedHelper.getKey(context, "estimated_fare") + " : " + SharedHelper.getKey(context, "to_price_ride"));

            lblDistance.setText(formate_distance(SharedHelper.getKey(context, "distance")));

            lblEta.setText(SharedHelper.getKey(context, "eta_time"));
            if (!SharedHelper.getKey(context, "name").equalsIgnoreCase("")
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase(null)
                    && !SharedHelper.getKey(context, "name").equalsIgnoreCase("null")) {
                lblType.setText(SharedHelper.getKey(context, "name"));
            } else {
                lblType.setText("" + "Sedan");
            }

            if ((loadingDialog != null) && (loadingDialog.isShowing()))
                loadingDialog.dismiss();
        }
    }

    public void setValueForLnrInvoice() {

        //Log.d("currentinvoiceee", "setValueForLnrInvoice: "+_CurrentInvoice.toString());
        //Log.d("mmmmmm", "setValueForLnrInvoice: " + _CurrentInvoice.toString());
        if (txt04BasePrice != null) {
            txt04BasePrice.setText(_CurrentInvoice.getBase_price() + " " + _CurrentInvoice.getCurrency());
            txt04Distance.setText(_CurrentInvoice.getDistance_price() + " " + _CurrentInvoice.getCurrency());
            txt04Waiting.setText(_CurrentInvoice.getWaiting_price() + " " + _CurrentInvoice.getCurrency());
            txt04Time.setText(_CurrentInvoice.getTime_price() + " " + _CurrentInvoice.getCurrency());
            txt04Time_amount.setText(_CurrentInvoice.getTripTime() + " min");
            txt04Waiting_amount.setText(_CurrentInvoice.getWaitingTime() + " min");
            txt04Distance_amount.setText(_CurrentInvoice.getDistance_final() + " Km");
            lblTotalPrice.setText(_CurrentInvoice.getTotal_price() + " ");
            txt04Tax.setText(_CurrentInvoice.getTax() + " ");

        }

    }

    //
    private void getDistAndTime(String url) {

        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array_rows = response.getJSONArray("rows");
                            JSONObject object_rows = array_rows.getJSONObject(0);
                            JSONArray array_elements = object_rows.getJSONArray("elements");
                            JSONObject object_elements = array_elements.getJSONObject(0);
                            JSONObject object_duration = object_elements.getJSONObject("duration");
                            JSONObject object_distance = object_elements.getJSONObject("distance");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Add JsonObjectRequest to the RequestQueue
        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Method To Get Card Info
    private void getCards() {
        Ion.with(this)
                .load(URLHelper.CARD_PAYMENT_LIST)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Authorization", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"))
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<com.koushikdutta.ion.Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, com.koushikdutta.ion.Response<String> response) {
                        // response contains both the headers and the string result
                        try {
                            if (response.getHeaders().code() == 200) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.getResult());
                                    if (jsonArray.length() > 0) {
                                        CardInfo cardInfo = new CardInfo();
                                        cardInfo.setCardId("CASH");
                                        cardInfo.setCardType("CASH");
                                        cardInfo.setLastFour("CASH");
                                        cardInfoArrayList.add(cardInfo);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject cardObj = jsonArray.getJSONObject(i);
                                            cardInfo = new CardInfo();
                                            cardInfo.setCardId(cardObj.optString("card_id"));
                                            cardInfo.setCardType(cardObj.optString("brand"));
                                            cardInfo.setLastFour(cardObj.optString("last_four"));
                                            cardInfoArrayList.add(cardInfo);
                                        }
                                    }

                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            CardInfo cardInfo = new CardInfo();
                            cardInfo.setCardId("CASH");
                            cardInfo.setCardType("CASH");
                            cardInfo.setLastFour("CASH");
                            cardInfoArrayList.add(cardInfo);
                        }
                    }
                });
    }

    // The Method Get All Services in App
    public void getServiceList() {
        loadingDialog = new LoadingDialog(context); // Create Loading Dialog
        loadingDialog.setCancelable(false); // Close Any Dialog
        if (loadingDialog != null)
            loadingDialog.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URLHelper.GET_SERVICE_LIST_API + "?device_mac=" + Utilities.getMacAddr()
                + "&lang=" + Utilities.getDeviceLocal(), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                utils.print("GetServices", response.toString());
                if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                    SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
                }
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                if (response.length() > 0) {
                    currentPostion = 0;
                    ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                    rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    rcvServiceTypes.setAdapter(serviceListAdapter);
                } else {
                    utils.displayMessage(getView(), getString(R.string.no_service));
                }
                setValuesForSourceAndDestination();
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
                                utils.displayMessage(getView(), errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                            flowValue = 1;
                            layoutChanges();
                        } else if (response.statusCode == 401) {
                            Utilities.goToLogin(getActivity());
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), json);
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                            flowValue = 1;
                            layoutChanges();
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), getString(R.string.server_down));
                            flowValue = 1;
                            layoutChanges();
                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
                            flowValue = 1;
                            layoutChanges();
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                        flowValue = 1;
                        layoutChanges();
                    }

                } else {
                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                    flowValue = 1;
                    layoutChanges();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
                        + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    // Method Get Approximate Fare To The Journey
    public void getApproximateFare() {
        String v = SharedHelper.getKey(context, "service_type");
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        JSONObject object = new JSONObject();
        final String constructedURL = URLHelper.ESTIMATED_FARE_DETAILS_API + "" +
                "?s_latitude=" + source_lat
                + "&s_longitude=" + source_lng
                + "&d_latitude=" + dest_lat
                + "&d_longitude=" + dest_lng
                + "&lang=" + Utilities.getDeviceLocal()
                + "&device_mac=" + Utilities.getMacAddr()
                + "&service_type=" + SharedHelper.getKey(context, "service_type");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, constructedURL, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {

                    if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                        //Log.v("request ride", "getApproximateFare");

                        // Calculation
                        if (!response.optString("estimated_fare").equalsIgnoreCase("")) {
                            Double price_with_pres = (Double.parseDouble(response.optString("estimated_fare")) * 25 / 100);
                            Double ToPrice = Double.parseDouble(response.optString("estimated_fare")) + price_with_pres;
                            Double roundedPercentage = (double) Math.round(ToPrice * 100) / 100;
                            SharedHelper.putKey(context, "to_price_ride", roundedPercentage.toString());
                        }

                        SharedHelper.putKey(context, "estimated_fare", response.optString("estimated_fare"));
                        SharedHelper.putKey(context, "distance", response.optString("distance"));
                        SharedHelper.putKey(context, "eta_time", response.optString("time"));
                        SharedHelper.putKey(context, "surge", response.optString("surge"));
                        SharedHelper.putKey(context, "surge_value", response.optString("surge_value"));
                        setValuesForApproximateLayout();
                        double wallet_balance = response.optDouble("wallet_balance");
                        SharedHelper.putKey(context, "wallet_balance", "" + response.optDouble("wallet_balance"));
                        SharedHelper.putKey(context, "base_price", "" + response.optDouble("base_price"));


                        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
                            lineView.setVisibility(VISIBLE);
                        } else {
                            lineView.setVisibility(View.GONE);
                        }
                        flowValue = 2;
                        layoutChanges();
                    }
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
                                getApproximateFare(); // Invoke Method Again
                            } catch (Exception e) {
                                utils.showAlert(context, context.getString(R.string.something_went_wrong));
                                Toast.makeText(activity, context.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            }
                        } else if (response.statusCode == 401) {
                            Utilities.goToLogin(getActivity());
                        } else if (response.statusCode == 422) {
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.showAlert(context, json);
                            } else {
                                getApproximateFare(); // Invoke Method Again
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(context, context.getString(R.string.server_down));
                        } else {
                            getApproximateFare(); // Invoke Method Again
                        }

                    } catch (Exception e) {
                        utils.showAlert(context, context.getString(R.string.something_went_wrong));
                        //Log.v("request ride", "getApproximateFare");
                    }

                } else {
                    //utils.showAlert(context, context.getString(R.string.please_try_again));
                    getApproximateFare(); // Invoke Method Again
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("lang", Utilities.getDeviceLocal());
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    // Method Send Request To Driver
    public void sendRequest() {
        btnRequestRideConfirm.setText(R.string.cancel_ride);
        CancelTripButton = true;
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        object = new JSONObject();
        source_addressEN_clean = source_addressEN.replace("Unnamed Road", "");
        dest_address_clean = dest_address.replace("Unnamed Road", "");
        //Toast.makeText(activity, "Source : "+source_addressEN_clean +"\n"+ "Destination : "+ dest_address_clean, Toast.LENGTH_LONG).show();
        try {
            object.put("s_latitude", source_lat);
            object.put("s_longitude", source_lng);
            object.put("d_latitude", dest_lat);
            object.put("d_longitude", dest_lng);
            object.put("first_name", SharedHelper.getKey(context, "first_name"));
            object.put("last_name", SharedHelper.getKey(context, "last_name"));
            object.put("email", SharedHelper.getKey(context, "email"));
            object.put("picture", SharedHelper.getKey(context, "picture"));
            object.put("rating", SharedHelper.getKey(context, "rating"));
            object.put("mobile", SharedHelper.getKey(context, "mobile"));
            object.put("status", "SEARCHING");
            object.put("booking_id", "");
            try {
                //object.put("s_address_en", source_addressEN);
                //object.put("d_address_en", dest_address);
                object.put("s_address_en", source_addressEN_clean);
                object.put("d_address_en", dest_address_clean);
                object.put("s_address_ar", source_addressAr);
                object.put("d_address_ar", dest_addressAR);
//                Log.v("sssssssss","source arabic : "+source_addressAr+
//                        "\nsource english : "+source_addressEN+"\ndistination arabic : "+
//                        dest_addressAR+"\n distination english : "+dest_address);

            } catch (Exception e) {
                Log.v("error language", "error language in api ");
            }
            object.put("id", SharedHelper.getKey(getContext(), "id"));
            object.put("service_type", SharedHelper.getKey(context, "service_type"));
            object.put("distance", SharedHelper.getKey(context, "distance"));
            object.put("schedule_date", scheduledDate);
            object.put("schedule_time", scheduledTime);
            if (walletCheckBox.isChecked()) {
                object.put("use_wallet", 1);
            } else {
                object.put("use_wallet", 0);
            }
            if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
            } else {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                object.put("card_id", SharedHelper.getKey(context, "card_id"));
            }

            utils.print("SendRequestInput", "" + object.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

        waiting.setVisibility(VISIBLE);

        flow = checkStatusFlow.SEARCHING;
        scheduleBtn.setEnabled(false);
        Intent intent = new Intent(getActivity(), SocketService.class);
        intent.setAction(SocketService.ACTION_START_FOREGROUND_SERVICE);

        SendRequestToSocket();

    }

    public void sendSechedualRequest() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        object = new JSONObject();
        //Toast.makeText(activity, SharedHelper.getKey(context, "rating"), Toast.LENGTH_SHORT).show();
        source_addressEN_clean = source_addressEN.replace("Unnamed Road", "");
        dest_address_clean = dest_address.replace("Unnamed Road", "");
        try {
            object.put("s_latitude", source_lat);
            object.put("s_longitude", source_lng);
            object.put("d_latitude", dest_lat);
            object.put("d_longitude", dest_lng);
            object.put("first_name", SharedHelper.getKey(context, "first_name"));
            object.put("last_name", SharedHelper.getKey(context, "last_name"));
            object.put("email", SharedHelper.getKey(context, "email"));
            object.put("picture", SharedHelper.getKey(context, "picture"));
            object.put("rating", SharedHelper.getKey(context, "rating"));
            object.put("mobile", SharedHelper.getKey(context, "mobile"));
            object.put("status", "SEARCHING");
            object.put("booking_id", "");
            try {
                //object.put("s_address_en", source_addressEN);
                //object.put("d_address_en", dest_address);
                object.put("s_address_en", source_addressEN_clean);
                object.put("d_address_en", dest_address_clean);
                object.put("s_address_ar", source_addressAr);
                //object.put("d_address_ar", dest_address);
                object.put("d_address_ar", dest_addressAR);
            } catch (Exception e) {
                Log.v("error language", "error language in api ");
            }
            object.put("id", SharedHelper.getKey(getContext(), "id"));
            object.put("service_type", SharedHelper.getKey(context, "service_type"));
            object.put("distance", SharedHelper.getKey(context, "distance"));
            object.put("schedule_date", scheduledDate);
            object.put("schedule_time", scheduledTime);
            if (walletCheckBox.isChecked()) {
                object.put("use_wallet", 1);
            } else {
                object.put("use_wallet", 0);
            }
            if (SharedHelper.getKey(context, "payment_mode").equals("CASH")) {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
            } else {
                object.put("payment_mode", SharedHelper.getKey(context, "payment_mode"));
                object.put("card_id", SharedHelper.getKey(context, "card_id"));
            }
            utils.print("SendRequestInput", "" + object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        waiting.setVisibility(VISIBLE);
        flow = checkStatusFlow.SEARCHING;
        Intent intent = new Intent(getActivity(), SocketService.class);
        intent.setAction(SocketService.ACTION_START_FOREGROUND_SERVICE);
        getActivity().startService(intent);
        createRequest(object);
    }

    private void SendRequestToSocket() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (object.length() > 0) {
                    try {
                        object.put("request_id", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Sending All Value In Socket io
                    JSONObject requestObj = new JSONObject();
                    try {
                        requestObj.put("request", object);
                        requestObj.put("UserId", SharedHelper.getKey(getContext(), "id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (SocketService.socket != null) {
                        SocketService.socket.emit("request_range_clients", requestObj);
                        Utilities.Trip_Type = 1;
                        loadingDialog.dismiss();
                    }
                }
            }
        }, 5000);
    }

    //    Send To Socket
    private void createRequest(final JSONObject mobject) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mobject.length() > 0) {

                    if (SocketService.socket != null) {
                        SocketService.socket.emit("schedule_trip", mobject);
                        //Toast.makeText(getContext(), "Done Send", Toast.LENGTH_SHORT).show();
                        Utilities.Trip_Type = 0;

                        loadingDialog.dismiss();

                    } else {
                        //Toast.makeText(getContext(), "Don't Send", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 5000);
    }

    public void cancelRequest() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("cancel_reason", cancalReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("CancelRequestResponse", response.toString());
                Toast.makeText(context, getResources().getString(R.string.request_cancel), Toast.LENGTH_SHORT).show();
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                mapClear();
                SharedHelper.putKey(context, "request_id", "");
                flowValue = 0;
                PreviousStatus = "";
                layoutChanges();
                setupMap();
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
                    flowValue = 0;
                    //flowValue = 4;
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                //utils.displayMessage(getView(), getString(R.string.places_try_again));
                            } catch (Exception e) {
                                //utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                            layoutChanges();//1
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("CANCEL_REQUEST");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (!json.equals("") && json != null) {//2 Apppear
                                //utils.displayMessage(getView(), getString(R.string.please_try_again));
                            } else {
                                //utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                            layoutChanges();//2
//                                lnrProviderAccepted.setVisibility(View.GONE);
//                                lnrAfterAcceptedStatus.setVisibility(View.GONE);
//                                lnrProviderPopup.setVisibility(View.GONE);
//                                lnrRequestProviders.setVisibility(View.GONE);
                        } else if (response.statusCode == 503) {
                            //utils.displayMessage(getView(), getString(R.string.server_down));
                            layoutChanges();//3
                        } else {
                            //utils.displayMessage(getView(), getString(R.string.please_try_again));
                            layoutChanges();//4
                        }

                    } catch (Exception e) {
                        //utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                        layoutChanges();//5
                    }

                } else {
                    //utils.displayMessage(getView(), getString(R.string.please_try_again));
                    layoutChanges();//6
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

    public void setValuesForSourceAndDestination() {
        if (isInternet) {
            if (!source_lat.equalsIgnoreCase("")) {
                if (!source_addressEN.equalsIgnoreCase("")) {
                    if (Utilities.getDeviceLocal().equals("ar"))
                        frmSource.setText(source_addressAr);
                    else
                        frmSource.setText(source_addressEN);
                } else {
                    frmSource.setText(current_address);
                }
            } else {
                if (Utilities.getDeviceLocal().equals("ar"))
                    frmSource.setText(current_addressAr);
                else
                    frmSource.setText(current_address);


            }

            if (!dest_lat.equalsIgnoreCase("")) {
                if (Utilities.getDeviceLocal().equals("ar")) {
                    frmDest.setText(dest_addressAR.replace("null", ""));
                    destination.setText(dest_addressAR.replace("null", ""));
                } else {
                    destination.setText(dest_address.replace("null", ""));
                    frmDest.setText(dest_address.replace("null", ""));
                }
                srcDestLayout.setVisibility(VISIBLE);
            }


            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }

            if (sourceLatLng != null && destLatLng != null) {
                utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);

                String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute(url);
            }

        }
    }

    public void setValueForLnrProviderAccepted(Provider provider) {
        strTag = "ride_accepted";
        statusProviders = "ride_accepted";
        try {
            SharedHelper.putKey(context, "provider_mobile_no", "" + provider.getMobile());
            lblProvider.setText(provider.getFirstName() + " " + provider.getLastName());
            if (provider.getPicture().startsWith("http"))
                Picasso.with(context).load(provider.getPicture()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);
            else
                Picasso.with(context).load(URLHelper.base_pic + provider.getPicture()).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(imgProvider);

            if (!provider.getRating().isEmpty())
                ratingProvider.setRating(Float.parseFloat(provider.getRating()));
            else
                ratingProvider.setRating(Float.parseFloat("4.0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkViewVisipilty(String state) {


//        Log.d("sttttitt", state + " of  destinationLayer is " + destinationLayer.getVisibility());
//        Log.d("sttttitt", state + " of  fragment_home_li_2_cancel_event is " + fragment_home_li_2_cancel_event.getVisibility());
//        Log.d("sttttitt", state + " of  fragment_home_li_3_cancel_event is " + fragment_home_li_3_cancel_event.getVisibility());
//        Log.d("sttttitt", state + " of  r_1 is" + r_1.getVisibility());
//        Log.d("sttttitt", state + " of  sourceAndDestinationLayout is " + sourceAndDestinationLayout.getVisibility());
//        Log.d("sttttitt", state + " of  frmDestination is " + frmDestination.getVisibility());
//        Log.d("sttttitt", state + " of  lnrRequestProviders is " + lnrRequestProviders.getVisibility());
//        Log.d("sttttitt", state + " of  lnrProviderPopup is " + lnrProviderPopup.getVisibility());
//        Log.d("sttttitt", state + " of  lnrHidePopup is " + lnrHidePopup.getVisibility());
//        Log.d("sttttitt", state + " of  lnrPriceBase is " + lnrPriceBase.getVisibility());
//        Log.d("sttttitt", state + " of  lnrPricemin is " + lnrPricemin.getVisibility());
//        Log.d("sttttitt", state + " of  lnrPricekm is " + lnrPricekm.getVisibility());
//        Log.d("sttttitt", state + " of  lnrSearchAnimation is " + lnrSearchAnimation.getVisibility());
//        Log.d("sttttitt", state + " of  lnrApproximate is " + lnrApproximate.getVisibility());
//        Log.d("sttttitt", state + " of  waiting is " + waiting.getVisibility());
//        Log.d("sttttitt", state + " of  lineView is " + lineView.getVisibility());
//        Log.d("sttttitt", state + " of  ScheduleLayout is " + ScheduleLayout.getVisibility());
//        Log.d("sttttitt", state + " of  lnrWaitingForProviders is " + lnrWaitingForProviders.getVisibility());
//        Log.d("sttttitt", state + " of  lnrProviderAccepted is " + lnrProviderAccepted.getVisibility());
//        Log.d("sttttitt", state + " of  lnrInvoice is " + lnrInvoice.getVisibility());
//        Log.d("sttttitt", state + " of  rtlStaticMarker is " + rtlStaticMarker.getVisibility());
//        Log.d("sttttitt", state + " of  destinationLayer is " + destinationLayer.getVisibility());

    }

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
                if (tag.equalsIgnoreCase("SERVICE_LIST")) {
                    getServiceList();
                    getApproximateFare();
                } else if (tag.equalsIgnoreCase("APPROXIMATE_RATE")) {
                    getApproximateFare();
                } else if (tag.equalsIgnoreCase("SEND_REQUEST")) {
                    sendRequest();
                } else if (tag.equalsIgnoreCase("CANCEL_REQUEST")) {
                    cancelRequest();
                } else if (tag.equalsIgnoreCase("PROVIDERS_LIST")) {
                    //getProvidersList("");
                } else if (tag.equalsIgnoreCase("SUBMIT_REVIEW")) {
                    submitReviewCall();
                } else if (tag.equalsIgnoreCase("PAY_NOW")) {
                    payNow();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = "";
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    utils.GoToBeginActivity(getActivity());
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

    private String getDirectionsUrl(LatLng sourceLatLng, LatLng destLatLng) {

        // Origin of routelng;
        String str_origin = "origin=" + source_lat + "," + source_lng;
        String str_dest = "destination=" + dest_lat + "," + dest_lng;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        utils.print("url", url.toString());
        return url;

    }

    // Method Go To Activity Payment
    private void showChooser() {
        Intent intent = new Intent(getActivity(), com.user.albaz.rafiq.activities.Payment.class);
        startActivityForResult(intent, 5555);
    }

    // Method Get Card Info
    private void getCardDetailsForPayment(CardInfo cardInfo) {
        if (cardInfo.getLastFour().equals("CASH")) {
            SharedHelper.putKey(context, "payment_mode", "CASH");
            imgPaymentType.setImageResource(R.drawable.money_icon);
            lblPaymentType.setText("CASH");
        } else {
            SharedHelper.putKey(context, "card_id", cardInfo.getCardId());
            SharedHelper.putKey(context, "payment_mode", "CARD");
            imgPaymentType.setImageResource(R.drawable.visa);
            lblPaymentType.setText("XXXX-XXXX-XXXX-" + cardInfo.getLastFour());
        }
    }

    public void payNow() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("payment_mode", paymentMode);
            object.put("is_paid", isPaid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.PAY_NOW_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("PayNowRequestResponse", response.toString());
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                flowValue = 6;
                layoutChanges();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                String json = "";
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.displayMessage(getView(), errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            refreshAccessToken("PAY_NOW");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), json);
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), getString(R.string.please_try_again));
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void mapClear() {
        if (parserTask != null)
            parserTask.cancel(true);
        mMap.clear();
        source_lat = "";
        source_lng = "";
        dest_lat = "";
        dest_lng = "";
        if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
            LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
            //camera zoom
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(25f).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void reCreateMap() {
        if (mMap != null) {
            if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                sourceLatLng = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
            }
            if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
            }
            utils.print("LatLng", "Source:" + sourceLatLng + " Destination: " + destLatLng);
            //String url = getDirectionsUrl(sourceLatLng, destLatLng);
            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.execute(url);
           /* DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);*/
        }
    }

    private void show(final View view) {
        mIsShowing = true;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ViewPropertyAnimator animator = view.animate()
                        .translationY(0)
                        .setInterpolator(INTERPOLATOR)
                        .setDuration(500);

                animator.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        view.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mIsShowing = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        // Canceling a show should hide the view
                        mIsShowing = false;
                        if (!mIsHiding) {
                            hide(view);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                animator.start();
            }
        });
    }

    private void hide(final View view) {
        mIsHiding = true;
        ViewPropertyAnimator animator = view.animate()
                .translationY(view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animator.start();
    }

    // Get Action by Status From Server
    public void liveNavigation(String status, String lat, String lng, String strTag) {

        if (!lat.equalsIgnoreCase("") && !lng.equalsIgnoreCase("")) {

            switch (status) {

                case "ACCEPTED":
                case "STARTED":
                case "PICKEDUP":
                case "ARRIVED":

                    // Check List Provider Markers
                    if (!lstProviderMarkers.isEmpty()) {
                        for (int i = 0; i < lstProviderMarkers.size(); i++) {
                            lstProviderMarkers.get(i).remove();
                        }
                    }
                    Double proLat = Double.parseDouble(lat);
                    Double proLng = Double.parseDouble(lng);
                    Location targetLocation = new Location("");//provider name is unecessary
                    targetLocation.setLatitude(proLat);//your coords of course
                    targetLocation.setLongitude(proLng);
                    Float rotation = 0.0f;
                    MarkerOptions markerOptions = new MarkerOptions()
                            .anchor(0.5f, 0.75f)
                            .position(new LatLng(proLat, proLng))
                            .rotation(rotation)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_location_icon));
                    if (providerMarker != null) {
                        rotation = getBearing(providerMarker.getPosition(), markerOptions.getPosition());
                        markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                        providerMarker.remove();
                    }
                    providerMarker = mMap.addMarker(markerOptions);
                    if (status.equalsIgnoreCase("PICKEDUP")) {
                        utils.animateMarker(mMap, targetLocation, providerMarker);
                    } else {
                        providerMarker.setPosition(new LatLng(proLat, proLng));
                    }
                    break;
                default:
                    getProvider();
                    break;
            }
        }
    }

    public float getBearing(LatLng oldPosition, LatLng newPosition) {
        double deltaLongitude = newPosition.longitude - oldPosition.longitude;
        double deltaLatitude = newPosition.latitude - oldPosition.latitude;
        double angle = (Math.PI * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) {
            return (float) angle;
        } else if (deltaLongitude < 0) {
            return (float) (angle + Math.PI);
        } else if (deltaLatitude < 0) {
            return (float) Math.PI;
        }

        return 0.0f;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc(); // Invoke Method Add Api Google Map in App Only One Time
        }
    }

    // Method Add Api Google Map in App Only One Time
    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                    }
                }).build();
        mGoogleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;

                    case LocationSettingsStatusCodes.CANCELED:
                        showDialogForGPSIntent();
                        break;
                }
            }
        });
    }

    public void submitReviewCall() {

        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(false);
        if (loadingDialog != null)
            loadingDialog.show();

        JSONObject object = new JSONObject();
        try {
            object.put("request_id", SharedHelper.getKey(context, "request_id"));
            object.put("rating", feedBackRating);
            object.put("comment", "" + txtCommentsRate.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RATE_PROVIDER_API, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                utils.print("SubmitRequestResponse", response.toString());
                utils.hideKeypad(context, activity.getCurrentFocus());
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
                destination.setText("");
                frmDest.setText("");
                mapClear();
                flowValue = 0;
                //getProvidersList("");
                layoutChanges();
                if (!current_lat.equalsIgnoreCase("") && !current_lng.equalsIgnoreCase("")) {
                    LatLng myLocation = new LatLng(Double.parseDouble(current_lat), Double.parseDouble(current_lng));
                    //camera zoom
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(25f).build();
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                                utils.displayMessage(getView(), errorObj.optString("message"));
                            } catch (Exception e) {
                                utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 401) {
                            refreshAccessToken("SUBMIT_REVIEW");
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                utils.displayMessage(getView(), json);
                            } else {
                                utils.displayMessage(getView(), getString(R.string.please_try_again));
                            }
                        } else if (response.statusCode == 503) {
                            utils.displayMessage(getView(), getString(R.string.server_down));
                        } else {
                            utils.displayMessage(getView(), getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        utils.displayMessage(getView(), getString(R.string.something_went_wrong));
                    }

                } else {
                    utils.displayMessage(getView(), getString(R.string.please_try_again));
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

    // Class Get Sevices Car in App
    private class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.MyViewHolder> {

        JSONArray jsonArray;
        private SparseBooleanArray selectedItems;
        int selectedPosition;

        public ServiceListAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.service_type_list_item, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            utils.print("Title: ", "" + jsonArray.optJSONObject(position).optString("name") + " Image: " + jsonArray.optJSONObject(position).optString("image") + " Grey_Image:" + jsonArray.optJSONObject(position).optString("grey_image"));

            holder.serviceTitle.setText(jsonArray.optJSONObject(position).optString("name"));
            if (position == currentPostion) {
                SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(position).optString("id"));
                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.ic_car).error(R.drawable.ic_car).into(holder.serviceImg);
                holder.selector_background.setBackgroundResource(R.drawable.full_rounded_button);
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.text_color_white));
            } else {
                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("image"))
                        .placeholder(R.drawable.ic_car).error(R.drawable.ic_car).into(holder.serviceImg);
                holder.selector_background.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                holder.serviceTitle.setTextColor(getResources().getColor(R.color.black_text_color));
            }

            holder.linearLayoutOfList.setTag(position);

            holder.linearLayoutOfList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == currentPostion) {
                        try {
                            lnrHidePopup.setVisibility(VISIBLE);
                            showProviderPopup(jsonArray.getJSONObject(position));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPostion = Integer.parseInt(view.getTag().toString());
                    SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
                    SharedHelper.putKey(context, "name", "" + jsonArray.optJSONObject(currentPostion).optString("name"));
                    notifyDataSetChanged();
                    utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));
                    utils.print("Service name", "" + SharedHelper.getKey(context, "name"));
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

    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(mMap, routeList);
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        handleCheckStatus.removeCallbacksAndMessages(null);
        if (mapRipple != null && mapRipple.isAnimationRunning()) {
            mapRipple.stopRippleMapAnimation();
        }
        super.onDestroy();
        checkViewVisipilty("on destory");

        //Log.d("gggggggg", "onDestroy: " + lnrApproximate.getVisibility());

//        if (lnrApproximate.getVisibility() == VISIBLE) {
//            JSONObject requestObj = new JSONObject();
//            try {
//                btnRequestRideConfirm.setText(R.string.request_now);
//
//                requestObj.put("reason", cancalReason);
//                if (true) {
//                    requestObj.put("provider_id", "0");
//                    requestObj.put("Trip_id", "0");
//                    requestObj.put("client_id", SharedHelper.getKey(getContext(), "id"));
//
//                } else {
//                    requestObj.put("Trip_id", SharedHelper.getKey(getContext(), "request_id"));
//                    requestObj.put("provider_id", provider.getId());
//                    requestObj.put("client_id", SharedHelper.getKey(getContext(), "id"));
//                    requestObj.put("first_name", SharedHelper.getKey(context, "first_name"));
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (SocketService.socket != null) {
//                SocketService.socket.emit("cancel_trip_user", requestObj);
//
//                Utilities.Trip_Type = 0;
//            }
//
//            SharedHelper.putKey(context, "request_id", "");
//            String fname = SharedHelper.getKey(context, "first_name");
//
//            //TODO clear cashe data
//            flowValue = 0;
//        }
    }

    private void stopAnim() {
        if (mMap != null) {
            MapAnimator.getInstance().stopAnim();
        } else {
            Toast.makeText(context, "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;


            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                if (!source_lat.equalsIgnoreCase("") && !source_lng.equalsIgnoreCase("")) {
                    LatLng location = new LatLng(Double.parseDouble(source_lat), Double.parseDouble(source_lng));
                    //mMap.clear();
                    if (sourceMarker != null)
                        sourceMarker.remove();
                    MarkerOptions markerOptions = new MarkerOptions()
                            .anchor(0.5f, 0.75f)
                            .position(location).title("Source").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker));
                    marker = mMap.addMarker(markerOptions);
                    sourceMarker = mMap.addMarker(markerOptions);
                }
                if (!dest_lat.equalsIgnoreCase("") && !dest_lng.equalsIgnoreCase("")) {
                    destLatLng = new LatLng(Double.parseDouble(dest_lat), Double.parseDouble(dest_lng));
                    if (destinationMarker != null)
                        destinationMarker.remove();
                    MarkerOptions destMarker = new MarkerOptions()
                            .position(destLatLng).title("Destination").draggable(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.provider_marker));
                    destinationMarker = mMap.addMarker(destMarker);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(sourceMarker.getPosition());
                    builder.include(destinationMarker.getPosition());
                    LatLngBounds bounds = builder.build();
                    int padding = 150; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLACK);


            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null && points != null) {
                //mMap.addPolyline(lineOptions);
                startAnim(points);
            } else {
            }
        }
    }

    // Get All Places From Source To Destnation To Drow Line in Map
    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude) {
        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;
        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkViewVisipilty("on resume");



//        alert = null;
//        if (!SharedHelper.getKey(context, "wallet_balance").equalsIgnoreCase("")) {
//            wallet_balance = Double.parseDouble(SharedHelper.getKey(context, "wallet_balance"));
//        }
//        if (!Double.isNaN(wallet_balance) && wallet_balance > 0) {
//            if (lineView != null && walletCheckBox != null) {
//                lineView.setVisibility(VISIBLE);
//                walletCheckBox.setVisibility(VISIBLE);
//            }
//        } else {
//            if (lineView != null && walletCheckBox != null) {
//                lineView.setVisibility(View.GONE);
//                walletCheckBox.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void getJSONArrayResult(String strTag, JSONArray response) {
        if (strTag.equalsIgnoreCase("Get Services")) {
            utils.print("GetServices", response.toString());
            if (SharedHelper.getKey(context, "service_type").equalsIgnoreCase("")) {
                SharedHelper.putKey(context, "service_type", "" + response.optJSONObject(0).optString("id"));
            }
            if ((loadingDialog != null) && (loadingDialog.isShowing()))
                loadingDialog.dismiss();
            if (response.length() > 0) {
                currentPostion = 0;
                ServiceListAdapter serviceListAdapter = new ServiceListAdapter(response);
                rcvServiceTypes.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                rcvServiceTypes.setAdapter(serviceListAdapter);
                //getProvidersList(SharedHelper.getKey(context, "service_type"));
            } else {
                utils.displayMessage(getView(), getString(R.string.no_service));
            }
            mMap.clear();
            setValuesForSourceAndDestination();
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void checkTripStatus() {
        if (MyTrip != null) {
            if (MyTrip.getStatus().equals("FOUND")) {

                SharedHelper.putKey(context, "request_id", MyTrip.getId());
                Toast.makeText(activity, "My Trip Found", Toast.LENGTH_SHORT).show();
                flow = checkStatusFlow.Found;
                flowValue = 4;
                layoutChanges();

                provider = MyTrip.getProvider();
                //car = MyTrip.getProvider();
                setValueForLnrProviderAccepted(provider);
                btnCancelTrip.setVisibility(VISIBLE);
                Toast.makeText(activity, "FOUND  Status", Toast.LENGTH_SHORT).show();



            } else if (MyTrip.getStatus().equals("ARRIVED")) {
                {
                    SharedHelper.putKey(context, "request_id", MyTrip.getId());
                    flow = checkStatusFlow.Arrived;
                    flowValue = 4;
                    layoutChanges();
                    setValueForLnrProviderAccepted(MyTrip.getProvider());
                    btnCancelTrip.setVisibility(VISIBLE);
                    Toast.makeText(activity, "ARRIVED  Status", Toast.LENGTH_SHORT).show();


                }
                //Ali
                btnCancelTrip.setVisibility(VISIBLE);
                lblStatus.setText(getString(R.string.arrived));

            } else if (MyTrip.getStatus().equals("STARTED") || MyTrip.getStatus().equals("PICKUP")) {
                {
                    SharedHelper.putKey(context, "request_id", MyTrip.getId());

                    flow = checkStatusFlow.pickedUp;
                    flowValue = 4;
                    layoutChanges();
                    btnCancelTrip.setVisibility(View.GONE);
                    Toast.makeText(activity, "STARTED  PICKUP  Status", Toast.LENGTH_SHORT).show();

                    provider = MyTrip.getProvider();
                    setValueForLnrProviderAccepted(provider);

                }

                AfterAcceptButtonLayout.setVisibility(View.GONE);
                lblStatus.setText(getString(R.string.picked_up));
                btnCancelTrip.setVisibility(View.GONE);
                lblStatus.setBackgroundColor(Color.blue(2));


            }
        }
    }

    // Destroy Session When User Offline
    void destroy_App() {
        JSONObject object = new JSONObject();
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.destroyApp, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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


    String formate_distance(String distance) {
        double meters;
        if (distance.equals(null))
            meters = 0.0;
        meters = Double.parseDouble(distance);
        if (meters < 1000) {
            return ((int) meters) + " meters";
        } else {
            return ((double) Math.round(meters / 100)) / 10 + " km";
        }
    }

    //    get the online Providers
    private void getProvider() {
        String providers_request = URLHelper.GET_PROVIDERS_LIST_API;
        JSONObject mapOject = new JSONObject();

        try {
            mapOject.put("latitude", current_lat);
            mapOject.put("longitude", current_lng);
            mapOject.put("service", strTag);

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, providers_request, mapOject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        getProviders(response);
                    }
                },
                new Response.ErrorListener() {
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
                                    } catch (Exception e) {
                                    }
                                } else if (response.statusCode == 401) {
                                    try {
                                        if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                            refreshAccessToken("FORGOT_PASSWORD");
                                        } else {
                                        }
                                    } catch (Exception e) {
                                    }

                                } else if (response.statusCode == 422) {

                                }

                            } catch (Exception e) {
                            }

                        } else {

                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        App.getInstance().addToRequestQueue(postRequest);


    }

    private void getProviders(JSONObject response) {
        JSONArray jsonArray = null;
        try {
            jsonArray = response.getJSONArray("data");
            showProvidersOnMap(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonArray == null)
            mGoogleMap.clear();
    }

    private void showProvidersOnMap(JSONArray response) {
        if (response != null) {
            for (int i = 0; i < lstProviderMarkers.size(); i++) {
                lstProviderMarkers.get(i).remove();
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObj = response.getJSONObject(i);
                    //utils.print("GetProvidersList", jsonObj.getString("latitude") + "," + jsonObj.getString("longitude"));
                    if (!jsonObj.getString("latitude").equalsIgnoreCase("") && !jsonObj.getString("longitude").equalsIgnoreCase("")) {
                        Double proLat = Double.parseDouble(jsonObj.getString("latitude"));
                        Double proLng = Double.parseDouble(jsonObj.getString("longitude"));
                        int providerId = Integer.parseInt(jsonObj.getString("id"));
                        Location targetLocation = new Location("");//provider name is unecessary
                        targetLocation.setLatitude(proLat);//your coords of course
                        targetLocation.setLongitude(proLng);
                        Float rotation = 0.0f;
                        MarkerOptions markerOptions = new MarkerOptions()
                                .anchor(0.5f, 0.75f)
                                .position(new LatLng(proLat, proLng))
                                .title(String.valueOf(providerId))
                                .rotation(rotation)
                                .icon(BitmapDescriptorFactory.fromResource
                                        (R.drawable.provider_location_icon));
                        lstProviderMarkers.add(i, mMap.addMarker(markerOptions));
                        builder.include(new LatLng(proLat, proLng));
                        if (lstProviderMarkers.get(i) != null) {
                            rotation = getBearing(lstProviderMarkers.get(i).getPosition(), markerOptions.getPosition());
                            markerOptions.rotation(rotation * (180.0f / (float) Math.PI));
                            lstProviderMarkers.get(i).remove();
                        }
                        lstProviderMarkers.add(i, mMap.addMarker(markerOptions));
                        utils.animateMarker(mMap, targetLocation, lstProviderMarkers.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            mGoogleMap.clear();
        }
    }

    private void showNetWorkDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_internet), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
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

    public void checkUserTrips() {

        JSONObject object = new JSONObject();
        try {
            object.put("device_mac", Utilities.getMacAddr());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.CEECK_TRIPS + "?device_mac=" + Utilities.getMacAddr(), object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray object;
                try {
                    object = response.getJSONArray("data");
                    MyTrip = new TripModel(object.getJSONObject(0));
                    checkTripStatus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.dismiss();
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


}

