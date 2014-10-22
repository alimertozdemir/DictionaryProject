package com.mydictionaryapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mydictionaryapp.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AliMert on 7.9.2014.
 */
public class HttpRequest {

    Context myActivity;
    HttpRequestCallback httpCallback;

    public HttpRequest(Context activity){
        myActivity = activity;
    }

    public void makeHttpPostWithVolley(String baseUrl, final Map<String, String> hmParams){
        RequestQueue queue = Volley.newRequestQueue(myActivity);

        final ProgressDialog pDialog = new ProgressDialog(myActivity);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest getRequest = new StringRequest(Request.Method.POST, baseUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response >>> ", response.toString());
                        httpCallback = (HttpRequestCallback) myActivity;
                        httpCallback.callback(response);
                        pDialog.hide();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AppUtils.showToast(myActivity, error.toString());
                        Log.d("Error.Response", error.toString());
                        pDialog.hide();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = hmParams;
                return params;
            }
        };


        // add it to the RequestQueue
        queue.add(getRequest);
    }



}
