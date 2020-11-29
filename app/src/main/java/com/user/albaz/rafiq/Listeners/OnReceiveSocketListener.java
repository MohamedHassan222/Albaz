package com.user.albaz.rafiq.Listeners;

import com.user.albaz.rafiq.Services.SocketService;

import org.json.JSONObject;


public interface OnReceiveSocketListener {
    void doAfterReceiveSocketData(SocketService.SocketMassageType massageType
            , JSONObject jsonObject
            , boolean IsMapInView);
}
