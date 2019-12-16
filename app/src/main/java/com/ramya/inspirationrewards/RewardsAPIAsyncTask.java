package com.ramya.inspirationrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class RewardsAPIAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "RewardsAPIAsyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint = "/rewards";
    private RewardsBean bean;
    @SuppressLint("StaticFieldLeak")
    private RewardActivity rAct;
    private int code = -1;

    public RewardsAPIAsyncTask(RewardActivity rewardActivity, RewardsBean bean) {

        this.rAct = rewardActivity;
        this.bean = bean;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String stdid = bean.studentIdSource;
        String usrc = bean.usernameSource;
        String ntar = bean.nameTarget;
        String dtar = bean.dateTarget;
        String psrc = bean.passwordSource;
        String stdtar = bean.studentIdTarget;
        String utar = bean.usernameTarget;
        String notes = bean.notesTarget;
        int val = bean.valueTarget;
        JSONObject srcjson = new JSONObject();
        JSONObject tarjson = new JSONObject();
        JSONObject jsend = new JSONObject();
        Log.d(TAG, "doInBackground: Rewards" + usrc + psrc + stdid + stdtar + utar + ntar + dtar + notes + val);
        try {
            srcjson.put("studentId", stdid);
            srcjson.put("username", usrc);
            srcjson.put("password", psrc);
            tarjson.put("studentId", stdtar);
            tarjson.put("username", utar);
            tarjson.put("name", ntar);
            tarjson.put("date", dtar);
            tarjson.put("notes", notes);
            tarjson.put("value", val);

            jsend.put("target", tarjson);
            jsend.put("source", srcjson);
            Log.d(TAG, "input JSON" + jsend);
            return doAPICall(jsend);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connect = null;
        BufferedReader read = null;

        try {

            String urlString = baseUrl + loginEndPoint;

            Uri uri = Uri.parse(urlString);
            URL url = new URL(uri.toString());

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");  // POST - others might use PUT, DELETE, GET
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connect.setRequestProperty("Accept", "application/json");
            connect.connect();
            OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int respCode = connect.getResponseCode();

            StringBuilder res = new StringBuilder();
            Log.d(TAG, "doAPICall: rewards" + respCode);

            if (respCode == HTTP_OK) {
                code = respCode;
                read = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String line;
                while (null != (line = read.readLine())) {
                    res.append(line).append("\n");
                }
                return res.toString();

            } else {
                code = respCode;
                read = new BufferedReader(new InputStreamReader(connect.getErrorStream()));
                String line;
                while (null != (line = read.readLine())) {
                    res.append(line).append("\n");
                }
                return res.toString();
            }

        } catch (Exception e) {
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally {
            if (connect != null) {
                connect.disconnect();
            }
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground:closing error: " + e.getMessage());
                }
            }
        }
        return "error occurred";
    }

    @Override
    protected void onPostExecute(String conres) {
        Log.d(TAG, "onPostExecute: " + conres);
        if (code == HTTP_OK)
            rAct.getRewardsAPIResp(conres);
        else if (code == -1)
            rAct.getRewardsAPIResp("error occured");
        else {
            Log.d(TAG, "onPostExecute: Inside else loop ");
            try {
                JSONObject errDetailsJson = new JSONObject(conres);
                JSONObject errJsonMsg = errDetailsJson.getJSONObject("errordetails");
                String errMsg = errJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error message " + errMsg);
                rAct.getRewardsAPIResp(errMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}

