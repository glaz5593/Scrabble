package com.moshe.glaz.scrabble.server;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.moshe.glaz.scrabble.app.AppBase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WiktionaryTask extends AsyncTask<String, Void, Void> {
    WiktionaryResult listener;
    String word;

    public interface WiktionaryResult {
        void onResult(boolean valid);

        void onError();
    }

    public WiktionaryTask(String word,WiktionaryResult listener) {
        this.listener = listener;
        this.word = word;
    }

    @Override
    protected Void doInBackground(String... urls) {

        RequestQueue queue = Volley.newRequestQueue(AppBase.getContext());
        String url = "https://he.m.wiktionary.org/wiki/" + word;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResult(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error == null || error.networkResponse == null) {
                    listener.onError();
                } else if (error.networkResponse.statusCode == 404) {
                    listener.onResult(false);
                } else {
                    listener.onError();
                }

                Log.i("TestResponse error", "");
            }
        });

        queue.add(stringRequest);
        return null;
    }

    protected void onPostExecute(Boolean result) {

    }
}