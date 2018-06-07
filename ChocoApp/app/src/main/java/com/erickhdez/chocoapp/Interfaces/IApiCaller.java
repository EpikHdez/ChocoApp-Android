package com.erickhdez.chocoapp.Interfaces;

import org.json.JSONObject;

public interface IApiCaller {
    void onSuccess(int requestCode, JSONObject response);
    void onFailure(int requestCode);
}
