package com.bkset.vutuan.bkreslora.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkset.vutuan.bkreslora.utils.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Phung Dinh Phuc on 29/07/2017.
 */

public class DownloadJSON {
    Context ctx;

    public DownloadJSON(Context ctx){
        this.ctx = ctx;
    }

    public interface  DownloadJSONCallBack{
        void onSuccess (String msgData);

        void onFail(String msgError);
    }


    public void GetJSONArray(Uri builder, final DownloadJSONCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = builder.toString();
        Log.i(Constant.TAG_URL_SERVICE, url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(Constant.TAG_DATA_RESPONSE,response.toString());
                DeleteCache.deleteCache(ctx);
                String msgData = "";
                try {
                    JSONArray jsonObj = new JSONArray(response);

                    callBack.onSuccess(jsonObj.toString());

                } catch(JSONException e){
                    e.printStackTrace();
                    callBack.onFail(e.toString());
                    HienThiThongBao.HienThiAlertDialogLoiMang(ctx);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constant.TAG_DATA_RESPONSE, error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = ctx.getSharedPreferences("access_code",MODE_PRIVATE);
                String access_token = pref.getString("access_token","");
                System.out.println(access_token);
                params.put("Authorization","Bearer "+ access_token);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Adding request to request queue
        queue.add(strReq);
    }

    public void GetJSONObject(Uri builder, final DownloadJSONCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = builder.toString();
        Log.i(Constant.TAG_URL_SERVICE, url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(Constant.TAG_DATA_RESPONSE,response.toString());
                DeleteCache.deleteCache(ctx);
                String msgData = "";
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    msgData = jsonObj.toString();

                    callBack.onSuccess(msgData);

                } catch(JSONException e){
                    e.printStackTrace();
                    callBack.onFail(e.toString());
                    HienThiThongBao.HienThiAlertDialogLoiMang(ctx);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constant.TAG_DATA_RESPONSE, error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = ctx.getSharedPreferences("access_code",MODE_PRIVATE);
                String access_token = pref.getString("access_token","");
                System.out.println(access_token);
                params.put("Authorization","Bearer "+ access_token);
                return super.getHeaders();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        // Adding request to request queue
        queue.add(strReq);
    }

    public void PostJSONObject(Uri builder, final DownloadJSONCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = builder.toString();
        Log.i(Constant.TAG_URL_SERVICE, url);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(Constant.TAG_DATA_RESPONSE,response.toString());
                DeleteCache.deleteCache(ctx);
                String msgData = "";
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    msgData = jsonObj.toString();

                    callBack.onSuccess(msgData);

                } catch(JSONException e){
                    e.printStackTrace();
                    callBack.onFail(e.toString());
                    HienThiThongBao.HienThiAlertDialogLoiMang(ctx);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constant.TAG_DATA_RESPONSE, error.getMessage());
            }
        });

        // Adding request to request queue
        queue.add(strReq);
    }

    public void GetJSON2(Uri builder, final DownloadJSONCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = builder.toString();
        Log.i(Constant.TAG_URL_SERVICE, url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(Constant.TAG_DATA_RESPONSE,response.toString());
                DeleteCache.deleteCache(ctx);
                String msgData = "";
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    boolean success = jsonObj.getBoolean("success");
                    if(success){
                        JSONObject jsonArray = jsonObj.getJSONObject("data");
                        msgData = jsonArray.toString();

                    }

                    callBack.onSuccess(msgData);

                } catch(JSONException e){
                    e.printStackTrace();
                    callBack.onFail(e.toString());
                    HienThiThongBao.HienThiAlertDialogLoiMang(ctx);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constant.TAG_DATA_RESPONSE, error.getMessage());
            }
        });

        // Adding request to request queue
        queue.add(strReq);
    }
}
