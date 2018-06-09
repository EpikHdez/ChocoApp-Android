package com.example.ximena.nomnom.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ximena.nomnom.interfaces.IAPICaller;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HerokuService {
    private static String BASE_URL;
    private static RequestQueue queue;
    private static Map<String, String> headers;

    public static void init(Context context) {
        BASE_URL = "https://nomnomapp.herokuapp.com/api/v1/";
        headers = new HashMap<>();

        addHeader("Content-Type", "application/json");
        addHeader("Accept", "application/json");
        queue = Volley.newRequestQueue(context);
        queue.start();
    }

    /**
     * Creates a new get request, response and error, given the case, will be delivered through the
     * methods of the IAPICaller interface .
     *
     * @param relativeURL the url path where to do the request.
     * @param caller the caller of the call.
     */
    public static synchronized void get(String relativeURL,
                                        int requestCode,
                                        IAPICaller caller) {
        createJsonObjectRequest(Request.Method.GET,
                relativeURL,
                null, requestCode,
                caller);
    }

    /**
     * Creates a new find request, response and error, given the case, will be delivered through the
     * methods of the IAPICaller interface .
     *
     * @param relativeURL the url path where to do the request (including id).
     * @param caller the caller of the call.
     */
    public static synchronized void find(String relativeURL, int requestCode, IAPICaller caller) {
        createJsonObjectRequest(Request.Method.GET, relativeURL, null, requestCode, caller);
    }

    /**
     * Creates a new post request, response and error, given the case, will be delivered through the
     * methods of the IAPICaller interface.
     *
     * @param relativeURL the url path where to do the request.
     * @param data the data to be send in the request.
     * @param caller the caller of the call.
     */
    public static synchronized void post(String relativeURL,
                                         JSONObject data,
                                         int requestCode,
                                         IAPICaller caller) {
        createJsonObjectRequest(Request.Method.POST, relativeURL, data, requestCode, caller);
    }

    /**
     * Creates a new put request, response and error, given the case, will be delivered through the
     * methods of the IAPICaller interface.
     *
     * @param relativeURL the url path where to do the request.
     * @param data the data to be send in the request.
     * @param caller the caller of the call.
     */
    public static synchronized void put(String relativeURL,
                                        JSONObject data,
                                        int requestCode,
                                        IAPICaller caller) {
        createJsonObjectRequest(Request.Method.PUT, relativeURL, data, requestCode, caller);
    }

    /**
     * Creates a new delete request, response and error, given the case, will be delivered through
     * the methods of the IAPICaller interface.
     *
     * @param relativeURL the url path where to do the request (including id).
     * @param caller the caller of the call.
     */
    public static synchronized void delete(String relativeURL, int requestCode, IAPICaller caller) {
        createJsonObjectRequest(Request.Method.DELETE, relativeURL, null, requestCode, caller);
    }

    /**
     * Adds a new header to append in the requests made to the server.
     *
     * @param key the access name.
     * @param value the value related to the key.
     */
    private static synchronized void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Creates the final request that it's going to made to the server and adds it to the Volley
     * queue.
     *
     * @param method the http verb to use.
     * @param relativeURL the relative api url.
     * @param data a JSONObject with the data for the server.
     * @param caller the caller of the call.
     */
    private static void createJsonObjectRequest(int method,
                                                String relativeURL,
                                                JSONObject data,
                                                final int requestCode,
                                                final IAPICaller caller) {
        final String endPoint = String.format("%s%s", BASE_URL, relativeURL);

        Request request = new JsonObjectRequest(method, endPoint, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("jwt")) {
                            String authToken = response.optString("jwt");
                            addHeader("Authorization", authToken);
                        }

                        caller.onSuccess(requestCode, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        caller.onFailure(requestCode, error);
                    }
                }) {
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        queue.add(request);
    }
}
