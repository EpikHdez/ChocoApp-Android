package com.example.ximena.nomnom.interfaces;

import org.json.JSONObject;

public interface IAPICaller {
    void onSuccess(int requestCode, JSONObject response);
    void onFailure(int requestCode, Object error);
}
