package com.ramya.inspirationrewards;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class UpdateProfileAPIAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "UpdateProfileAPIAsyncTask";
    private static final String baseUrl = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private static final String loginEndPoint = "/profiles";
    private CreateProfile bean;
    private List<RewardRecords> raarlist = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private EditActivity eact;
    private JSONArray rjson;
    private int code = -1;

    public UpdateProfileAPIAsyncTask(EditActivity editActivity, CreateProfile bean, List<RewardRecords> rewardArrayList) {

        this.eact = editActivity;
        this.bean = bean;
        this.raarlist = rewardArrayList;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(Void... voids) {
        JSONObject jobj = new JSONObject();
        String stdid = bean.stdId;
        String uname = bean.uname;
        String pwd = bean.psswd;
        String fName = bean.fName;
        String lName = bean.lName;
        int pts = bean.pts;
        String story = bean.stry;
        String dept = bean.dept;
        String pos = bean.pos;
        boolean admin = bean.admin;
        String loc = bean.loc;
        String imageBytes = bean.imaBytes;
        rjson = new JSONArray();
        if (!raarlist.isEmpty()) {
            for (int i = 0; i < raarlist.size(); i++) {
                JSONObject rewJson = new JSONObject();
                RewardRecords r = raarlist.get(i);
                String studentR = r.getStudentId();
                String usernmaeR = r.getUsernameRecord();
                String nameR = r.getName();
                String dateR = r.getDate();
                String notesR = r.getNotes();
                int valueR = r.getValue();
                rjson.put(rewJson);
            }
        }

        Log.d(TAG, "doInBackground: UpdateProfile" + stdid + uname + pwd + fName + lName + pts + dept + pos + admin + loc + imageBytes);
        try {
            jobj.put("studentId", stdid);
            jobj.put("username", uname);
            jobj.put("password", pwd);
            jobj.put("firstName", fName);
            jobj.put("lastName", lName);
            jobj.put("pointsToAward", pts);
            jobj.put("department", dept);
            jobj.put("story", story);
            jobj.put("position", pos);
            jobj.put("admin", admin);
            jobj.put("location", loc);
            jobj.put("imageBytes", imageBytes);
            if (raarlist.size() == 0)
                jobj.put("rewardRecords", "[]");
            else
                jobj.put("rewardRecords", rjson);

            return doAPICall(jobj);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connec = null;
        BufferedReader read = null;

        try {

            String urlStr = baseUrl + loginEndPoint;

            Uri uri = Uri.parse(urlStr);
            URL url = new URL(uri.toString());

            connec = (HttpURLConnection) url.openConnection();
            connec.setRequestMethod("PUT");


            connec.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connec.setRequestProperty("Accept", "application/json");
            connec.connect();

            // Write the JSON (as String) to the open connection
            OutputStreamWriter out = new OutputStreamWriter(connec.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int respCode = connec.getResponseCode();

            StringBuilder res = new StringBuilder();
            Log.d(TAG, "doAPICall: UpdateProfile" + respCode);
            if (respCode == HTTP_OK) {
                code = respCode;
                read = new BufferedReader(new InputStreamReader(connec.getInputStream()));
                String line;
                while (null != (line = read.readLine())) {
                    res.append(line).append("\n");
                }

                return res.toString();

            } else {
                code = respCode;
                read = new BufferedReader(new InputStreamReader(connec.getErrorStream()));
                String line;
                while (null != (line = read.readLine())) {
                    res.append(line).append("\n");
                }

                return res.toString();
            }

        } catch (Exception e) {
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally {
            if (connec != null) {
                connec.disconnect();
            }
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: error close " + e.getMessage());
                }
            }
        }
        return "error occurred";
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(String connres) {
        Log.d(TAG, "onPostExecute: UpdateProfile" + connres);
        if (code == HTTP_OK)
            eact.getEditProfileAPIResp("SUCCESS");
        else if (code == -1)
            eact.getEditProfileAPIResp("error occured");
        else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errDetailsJson = new JSONObject(connres);
                JSONObject errJsonMsg = errDetailsJson.getJSONObject("errordetails");
                String errMsg = errJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errMsg);
                eact.getEditProfileAPIResp(errMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
