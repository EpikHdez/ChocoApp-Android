package com.example.ximena.nomnom.services;

import android.net.Uri;
import android.os.AsyncTask;

import com.cloudinary.android.MediaManager;
import com.example.ximena.nomnom.interfaces.IAPICaller;

import org.json.JSONObject;

import java.util.UUID;

public class CouldinaryService extends AsyncTask<Uri, Void, String> {
    private IAPICaller mRequester;
    private int requestCode;

    public CouldinaryService(int requestCode, IAPICaller requester) {
        this.mRequester = requester;
        this.requestCode = requestCode;
    }

    @Override
    protected String doInBackground(Uri... uris) {
        String publicId = UUID.randomUUID().toString();

        MediaManager.get().upload(uris[0]).option("public_id", publicId).dispatch();
        return MediaManager.get().url().generate(publicId);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        JSONObject response = new JSONObject();

        try {
            response.put("picture_url", s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRequester.onSuccess(requestCode, response);
    }
}
