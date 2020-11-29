package com.user.albaz.rafiq.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.user.albaz.rafiq.models.CardDetails;
import com.user.albaz.rafiq.R;
import com.user.albaz.rafiq.utils.MyTextView;

import java.util.ArrayList;

public class PaymentListAdapter extends ArrayAdapter<CardDetails>{

    int vg;

    public  ArrayList<CardDetails> list; // Visa Cards

    Context context;

    // Constrictor To Set All Visa Card To List
    public PaymentListAdapter(Context context, int vg, ArrayList<CardDetails> list){

        super(context,vg,list);

        this.context=context;

        this.vg = vg;

        this.list=list;
    }

    // Method Getting Position VisaCard & Checked Brand Visa Card
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(vg, parent, false);

        ImageView paymentTypeImg = itemView.findViewById(R.id.paymentTypeImg);

        MyTextView cardNumber = itemView.findViewById(R.id.cardNumber);

        ImageView tickImg = itemView.findViewById(R.id.img_tick);

        try {
           if(list.get(position).getBrand().equalsIgnoreCase("MASTERCARD")){
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
