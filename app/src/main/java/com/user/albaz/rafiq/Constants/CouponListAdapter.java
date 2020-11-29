package com.user.albaz.rafiq.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.user.albaz.rafiq.helper.SharedHelper;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.utils.MyBoldTextView;

import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter Coupon List
 */

public class CouponListAdapter extends ArrayAdapter<JSONObject> {

    int res;

    public ArrayList<JSONObject> list;

    Context context;

    // Set Coupon In List
    public CouponListAdapter(Context context, int res, ArrayList<JSONObject> list){

        super(context,res,list);

        this.context = context;

        this.res = res;

        this.list=list;

    }

    // Getting View
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(res, parent, false);

        MyBoldTextView discount = itemView.findViewById(R.id.discount);

        MyBoldTextView promo_code = itemView.findViewById(R.id.promo_code);

        MyBoldTextView expires = itemView.findViewById(R.id.expiry);


        try {
            discount.setText(SharedHelper.getKey(context, "currency")+""+list.get(position).optJSONObject("promocode").optString("discount")+" "+context.getString(R.string.off));
            promo_code.setText(context.getString(R.string.the_applied_coupon)+" "+list.get(position).optJSONObject("promocode").optString("promo_code")+".");
            String date = list.get(position).optJSONObject("promocode").optString("expiration");
            expires.setText(context.getString(R.string.valid_until)+" "+getDate(date)+" "+getMonth(date)+" "+getYear(date));
        } catch (Exception e) {
            e.printStackTrace();
        }

            return itemView;

    }

    // Method Getting Name Month
    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    // Method Getting Name Date
    private String getDate(String date) throws ParseException{
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    // Method Getting Name Year
    private String getYear(String date) throws ParseException{
        Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

}
