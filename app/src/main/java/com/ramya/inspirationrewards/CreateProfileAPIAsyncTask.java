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

public class CreateProfileAPIAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "CreateProfileAPIAsyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String login = "/profiles";
    private CreateProfile bean;
    private int code = -1;
    @SuppressLint("StaticFieldLeak")
    private ActivityCreate actcreate;

    public CreateProfileAPIAsyncTask(ActivityCreate ActivityCreate, CreateProfile bean) {

        this.actcreate = ActivityCreate;
        this.bean = bean;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(Void... voids) {
        JSONObject jObj = new JSONObject();
        String stdid = bean.stdId;
        String username = bean.uname;
        String pwd = bean.psswd;
        String story = bean.stry;
        String dept = bean.dept;
        String pos = bean.pos;
        String fName = bean.fName;
        String lName = bean.lName;
        int pts = bean.pts;
        boolean admin = bean.admin;
        String loc = bean.loc;
        String img = bean.imaBytes;
        Log.d(TAG, "doInBackground: CreateProfile" + stdid + username + pwd + fName + lName + pts + dept + pos + admin + loc);
        try {
            jObj.put("studentId", stdid);
            jObj.put("username", username);
            jObj.put("password", pwd);
            jObj.put("firstName", fName);
            jObj.put("lastName", lName);
            jObj.put("pointsToAward", pts);
            jObj.put("department", dept);
            jObj.put("story", story);
            jObj.put("position", pos);
            jObj.put("admin", admin);
            jObj.put("location", loc);
            jObj.put("imageBytes", img);
            jObj.put("rewardRecords", "[]");

            return doAPICall(jObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    private String doAPICall(JSONObject jsonObj) {
        HttpURLConnection connect = null;
        BufferedReader read = null;

        try {

            String urlstr = baseUrl + login;

            Uri uri = Uri.parse(urlstr);
            URL url = new URL(uri.toString());

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");

            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connect.setRequestProperty("Accept", "application/json");
            connect.connect();

            OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream());
            out.write(jsonObj.toString());
            out.close();

            int respCode = connect.getResponseCode();

            StringBuilder res = new StringBuilder();
            Log.d(TAG, "doAPICall: CreateProfile" + respCode);
            if (respCode == HTTP_OK) {
                code = respCode;
                read = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String l;
                while (null != (l = read.readLine())) {
                    res.append(l).append("\n");
                }

                return res.toString();

            } else {
                code = respCode;
                read = new BufferedReader(new InputStreamReader(connect.getErrorStream()));
                String l;
                while (null != (l = read.readLine())) {
                    res.append(l).append("\n");
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
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        return "error occurred";
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(String connectionResult) {
        Log.d(TAG, "onPostExecute: " + connectionResult);
        CreateProfile bean = null;
        if (code == HTTP_OK) {
            try {
                JSONObject jsonObj = new JSONObject(connectionResult);
                String username = jsonObj.getString("username");
                String password = jsonObj.getString("password");
                String fName = jsonObj.getString("firstName");
                String lName = jsonObj.getString("lastName");
                int pts = jsonObj.getInt("pointsToAward");
                String dept = jsonObj.getString("department");
                String stry = jsonObj.getString("story");
                String position = jsonObj.getString("position");
                boolean admin = jsonObj.getBoolean("admin");
                String loc = jsonObj.getString("location");
                String imageBytes = jsonObj.getString("imageBytes");
                bean = new CreateProfile("A20449938", username, password, fName, lName, pts, dept, stry, position, admin, loc, imageBytes);
                actcreate.getCreateProfileAPIResp(bean, "Profile Created Successfully.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (code == -1) {
            actcreate.getCreateProfileAPIResp(null, "error occured");
        } else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errorDetailsJson = new JSONObject(connectionResult);
                JSONObject errorJsonMsg = errorDetailsJson.getJSONObject("errordetails");
                String errorMsg = errorJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errorMsg);
                actcreate.getCreateProfileAPIResp(null, errorMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }

}
