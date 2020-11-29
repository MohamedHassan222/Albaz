package com.user.albaz.rafiq.helper;

import org.json.JSONObject;

public class Invoice {

    private double distance_final;
    private double tripTime;
    private double waitingTime;
    private double distance_price;
    private double waiting_price;
    private double time_price;
    private double total_price;
    private double base_price;
    private double tax;
    private String Trip_id;
    private String currency;

    @Override
    public String toString() {
        return "Invoice{" +
                "distance_final=" + distance_final +
                ", tripTime=" + tripTime +
                ", waitingTime=" + waitingTime +
                ", distance_price=" + distance_price +
                ", waiting_price=" + waiting_price +
                ", time_price=" + time_price +
                ", total_price=" + total_price +
                ", base_price=" + base_price +
                ", tax=" + tax +
                ", Trip_id='" + Trip_id + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }

    public Invoice(){}
    public Invoice(JSONObject data){
        try {
            this.distance_final = data.getDouble("distance");
            this.distance_price = data.getDouble("distance_price");
            this.waiting_price = data.getDouble("time_price");
            this.total_price = data.getDouble("total_price");
            this.base_price = data.getDouble("fixed_price");
            this.tax = data.getDouble("tax");

            this.tripTime = data.getDouble("tripTime");
            this.waitingTime = data.getDouble("wattingTime");

            this.Trip_id = data.getString("Trip_id");
            this.time_price = data.getDouble("watting_price");
            this.currency = " LE";
        }catch (Exception e){

        }
    }
    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("distance_final", distance_final);
        }catch (Exception e){

        }
        try {
            json.put("tripTime",tripTime);
        }catch (Exception e){

        }
        try {
            json.put("watting_price", waitingTime);
        }catch (Exception e){

        }try {
            json.put("distance_price", distance_price);
        }catch (Exception e){

        }try {
            json.put("watting_price", waiting_price);
        }catch (Exception e){

        }try {
            json.put("time_price", time_price);
        }catch (Exception e){

        }try {
            json.put("total_price", total_price);
        }catch (Exception e){

        }try {
            json.put("Trip_id", Trip_id);
        }catch (Exception e){

        }try {
            json.put("base_price", base_price);
        }catch (Exception e){

        }try {
            json.put("currency", currency);
        }catch (Exception e){

        }


        return json;
    }

    public double getDistance_final() {
        return distance_final;
    }

    public void setDistance_final(double distance_final) {
        this.distance_final = distance_final;
    }

    public double getTripTime() {
        return tripTime;
    }

    public void setTripTime(long tripTime) {
        this.tripTime = tripTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(long watingTime) {
        this.waitingTime = watingTime;
    }

    public double getDistance_price() {
        return distance_price;
    }

    public void setDistance_price(double distance_price) {
        this.distance_price = distance_price;
    }

    public double getWaiting_price() {
        return waiting_price;
    }

    public void setWaiting_price(double waiting_price) {
        this.waiting_price = waiting_price;
    }

    public double getTime_price() {
        return time_price;
    }

    public void setTime_price(double time_price) {
        this.time_price = time_price;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }
    public double getBase_price() {
        return base_price;
    }

    public void setBase_price(int base_price) {
        this.base_price = base_price;
    }
    public String getTrip_id() {
        return Trip_id;
    }

    public void setTrip_id(String trip_id) {
        Trip_id = trip_id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setTripTime(double tripTime) {
        this.tripTime = tripTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setBase_price(double base_price) {
        this.base_price = base_price;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
