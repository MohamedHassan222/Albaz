package com.user.albaz.rafiq.models;

import org.json.JSONObject;

public class Car {

    private String picture;
    private String color;
    private String number;

    public  Car(JSONObject data){
        this.color = data.optJSONObject("response").optString("first_name");
        this.number  = data.optJSONObject("response").optString("last_name");

        if (data.optJSONObject("response").optString("picture").startsWith("http")) {
            this.picture = data.optJSONObject("response").optString("picture");
        }else {
            this.picture = data.optJSONObject("response").optString("picture");
        }
    }

}
