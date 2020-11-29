package com.user.albaz.rafiq.models;

import org.json.JSONException;
import org.json.JSONObject;

public class TripModel {

    private String id;

    private String bookingId;

    private String userId;

    private String providerId;

    private String currentProviderId;

    private String serviceTypeId;

    private String status;

    private String cancelledBy;

    private String cancelReason;

    private String paymentMode;

    private String paid;

    private String distance;

    private String sAddress;

    private String sLatitude;

    private String sLongitude;

    private String dAddress;

    private String dLatitude;

    private String dLongitude;

    private String assignedAt;

    private String scheduleAt;

    private String startedAt;

    private String finishedAt;

    private String userRated;

    private String providerRated;

    private String useWallet;

    private String surge;

    private String routeKey;

    private String deletedAt;

    private Provider provider;


    public TripModel(JSONObject response) {
        try {
            this.id = response.getString("id");
            this.bookingId = response.getString("booking_id");
            this.userId = response.getString("user_id");
            this.providerId = response.getString("provider_id");
            this.currentProviderId = response.getString("current_provider_id");
            this.serviceTypeId = response.getString("service_type_id");
            this.status = response.getString("status");
            this.cancelledBy = response.getString("cancelled_by");
            this.cancelReason = response.getString("cancel_reason");
            this.paymentMode = response.getString("payment_mode");
            this.paid = response.getString("paid");
            this.distance = response.getString("distance");
            this.sAddress = response.getString("s_address");
            this.sLatitude = response.getString("s_address");
            this.sLongitude = response.getString("s_longitude");
            this.dAddress = response.getString("d_address");
            this.dLatitude = response.getString("d_latitude");
            this.dLongitude = response.getString("d_longitude");
            this.assignedAt = response.getString("assigned_at");
            this.scheduleAt = response.getString("schedule_at");
            this.startedAt = response.getString("started_at");
            this.finishedAt = response.getString("finished_at");
            this.userRated = response.getString("user_rated");
            this.providerRated = response.getString("provider_rated");
            this.useWallet = response.getString("use_wallet");
            this.surge = response.getString("surge");
            this.routeKey = response.getString("route_key");
            this.deletedAt = response.getString("deleted_at");
            this.provider = new Provider(
                    response.getJSONObject("provider").getString("first_name"),
                    response.getJSONObject("provider").getString("last_name"),
                    response.getJSONObject("provider").getString("email"),
                    response.getJSONObject("provider").getString("logo"),
                    response.getJSONObject("provider").getString("rating"),
                    response.getJSONObject("provider").getString("mobile"),
                    response.getJSONObject("provider").getString("id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getCurrentProviderId() {
        return currentProviderId;
    }

    public void setCurrentProviderId(String currentProviderId) {
        this.currentProviderId = currentProviderId;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getsAddress() {
        return sAddress;
    }

    public void setsAddress(String sAddress) {
        this.sAddress = sAddress;
    }

    public String getsLatitude() {
        return sLatitude;
    }

    public void setsLatitude(String sLatitude) {
        this.sLatitude = sLatitude;
    }

    public String getsLongitude() {
        return sLongitude;
    }

    public void setsLongitude(String sLongitude) {
        this.sLongitude = sLongitude;
    }

    public String getdAddress() {
        return dAddress;
    }

    public void setdAddress(String dAddress) {
        this.dAddress = dAddress;
    }

    public String getdLatitude() {
        return dLatitude;
    }

    public void setdLatitude(String dLatitude) {
        this.dLatitude = dLatitude;
    }

    public String getdLongitude() {
        return dLongitude;
    }

    public void setdLongitude(String dLongitude) {
        this.dLongitude = dLongitude;
    }

    public Object getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(String assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Object getScheduleAt() {
        return scheduleAt;
    }

    public void setScheduleAt(String scheduleAt) {
        this.scheduleAt = scheduleAt;
    }

    public Object getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public Object getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(String finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getUserRated() {
        return userRated;
    }

    public void setUserRated(String userRated) {
        this.userRated = userRated;
    }

    public String getProviderRated() {
        return providerRated;
    }

    public void setProviderRated(String providerRated) {
        this.providerRated = providerRated;
    }

    public String getUseWallet() {
        return useWallet;
    }

    public void setUseWallet(String useWallet) {
        this.useWallet = useWallet;
    }

    public String getSurge() {
        return surge;
    }

    public void setSurge(String surge) {
        this.surge = surge;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
