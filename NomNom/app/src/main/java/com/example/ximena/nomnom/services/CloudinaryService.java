package com.example.ximena.nomnom.services;

import android.net.Uri;
import android.os.AsyncTask;

import com.cloudinary.android.MediaManager;
import com.example.ximena.nomnom.interfaces.IAPICaller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class CloudinaryService extends AsyncTask<Uri, Void, String[]> {
    private IAPICaller mRequester;
    private int requestCode;

    public CloudinaryService(int requestCode, IAPICaller requester) {
        this.mRequester = requester;
        this.requestCode = requestCode;
    }

    @Override
    protected String[] doInBackground(Uri... uris) {
        String publicId = UUID.randomUUID().toString();
        String[] urls = new String[uris.length];

        for (int i = 0; i < uris.length; i++) {
            MediaManager.get().upload(uris[i]).option("public_id", publicId).dispatch();
            urls[i] = MediaManager.get().url().generate(publicId);
        }

        return urls;
    }

    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
        JSONObject response = new JSONObject();

        try {
            JSONArray pictures = new JSONArray(s);
            response.put("pictures_urls", pictures);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRequester.onSuccess(requestCode, response);
    }
}
