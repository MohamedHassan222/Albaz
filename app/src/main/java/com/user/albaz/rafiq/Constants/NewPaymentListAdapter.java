package com.user.albaz.rafiq.Constants;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.user.albaz.rafiq.models.CardDetails;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.utils.MyTextView;

import java.util.List;

/**
 * Adapter Payment List
 */

public class NewPaymentListAdapter extends ArrayAdapter<CardDetails>{

    List<CardDetails> list; // Visa Cards

    Context context;

    Activity activity;

    // Constrictor To Set All Visa Card To List
    public NewPaymentListAdapter(Context context, List<CardDetails> list, Activity activity){
        super(context, R.layout.payment_list_item, list);
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    // Method Getting Position VisaCard & Checked Brand Visa Card
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.payment_list_item, parent, false);

        ImageView paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);

        MyTextView cardNumber = itemView.findViewById(R.id.cardNumber);

        try {

           if(list.get(position).getBrand().equalsIgnoreCase("MASTER")){
               paymentTypeImg.setImageResource(R.drawable.credit_card); // Set This Image Brand
           }else if(list.get(position).getBrand().equalsIgnoreCase("MASTRO")){
               paymentTypeImg.setImageResource(R.drawable.visa_payment_icon); // Set This Image Brand
           }else if(list.get(position).getBrand().equalsIgnoreCase("Visa")){
               paymentTypeImg.setImageResource(R.drawable.visa); // Set This Image Brand
           }

           cardNumber.setText("xxxx - xxxx - xxxx - " + list.get(position).getLast_four());

        } catch (Exception e) {

            e.printStackTrace();
        }

        return itemView; // Set View Layout
    }
}
