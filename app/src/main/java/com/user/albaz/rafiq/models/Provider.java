package com.user.albaz.rafiq.models;

import org.json.JSONObject;

public class Provider {

    private String firstName;
    private String lastName;
    private String email;
    private String picture;
    private String rating;
    private String mobile;
    private String id;

    public Provider(String firstName, String lastName, String email, String picture, String rating, String mobile, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.picture = picture;
        this.rating = rating;
        this.mobile = mobile;
        this.id = id;
    }

    public  Provider(JSONObject data){
        this.firstName = data.optJSONObject("response").optString("first_name");
        this.lastName  = data.optJSONObject("response").optString("last_name");
        this.email     = data.optJSONObject("response").optString("email");

        this.rating =    data.optJSONObject("response").optString("rating");
        this.mobile =    data.optJSONObject("response").optString("mobile");

        this.id = data.optJSONObject("response").optString("id");

        if (data.optJSONObject("response").optString("picture").startsWith("http")) {
            this.picture = data.optJSONObject("response").optString("picture");
        }else {
            this.picture = data.optJSONObject("response").optString("picture");
        }

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
