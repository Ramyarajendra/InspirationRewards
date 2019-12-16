package com.ramya.inspirationrewards;

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

public class LoginAPIAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "LoginProfileAsyncTask";
    private MainActivity mActivity;
    private final String urlAdd = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private final String loginURL = "/login";
    private int code=-1;
    public LoginAPIAsyncTask(MainActivity ma) {
        mActivity = ma;
    }

    @Override
    protected void onPostExecute(String connectionResult) {
        Log.d(TAG, "onPostExecute: " + connectionResult);
        CreateProfile bean = null;
        if (code == HTTP_OK)
        {
            try {
                JSONObject jObj = new JSONObject(connectionResult);
                String stdId = jObj.getString("studentId");
                String uname = jObj.getString("username");
                String story = jObj.getString("story");
                String pos = jObj.getString("position");
                String pd = jObj.getString("password");
                String fName = jObj.getString("firstName");
                String loc = jObj.getString("location");
                String lName = jObj.getString("lastName");
                int pts = jObj.getInt("pointsToAward");
                String dept = jObj.getString("department");
                boolean admin = jObj.getBoolean("admin");
                String imb = jObj.getString("imageBytes");

                List<RewardRecords> rList=new ArrayList<>();
                if(jObj.getJSONArray("rewards")!=null)
                {
                    JSONArray rwd = jObj.getJSONArray("rewards");
                    for(int i=0;i<rwd.length();i++)
                    {
                        RewardRecords rBean=new RewardRecords();
                        JSONObject reward=rwd.getJSONObject(i);
                        String stdentId = reward.getString("studentId");
                        String notesR = reward.getString("notes");
                        String dateR = reward.getString("date");
                        String unR = reward.getString("username");
                        String nameR = reward.getString("name");
                        int valueR = reward.getInt("value");
                        rBean.setStudentId(stdentId);
                        rBean.setUsernameRecord(unR);
                        rBean.setName(nameR);
                        rBean.setDate(dateR);
                        rBean.setNotes(notesR);
                        rBean.setValue(valueR);
                        rList.add(rBean);
                    }
                }

                bean = new CreateProfile(stdId, uname, pd, fName, lName, pts, dept, story, pos, admin, loc, imb);
                Log.d(TAG, "onPostExecute: " + bean);
                mActivity.getLoginAPIResp(bean,rList,"SUCCESS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (code == -1)
            mActivity.getLoginAPIResp(null,null,"Error Occured");
        else {
            Log.d(TAG, "onPostExecute: Inside else ");
            try {
                JSONObject errDetailsJson = new JSONObject(connectionResult);
                JSONObject errJsonMsg = errDetailsJson.getJSONObject("errordetails");
                String errMsg = errJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errMsg);
                mActivity.getLoginAPIResp(null,null,errMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String stuId = strings[0];
        String uName = strings[1];
        String pswd = strings[2];
        Log.d(TAG, "doInBackground: Inputs+"+stuId+uName+pswd);
        try {
            JSONObject jObj = new JSONObject();
            jObj.put("studentId", stuId);
            jObj.put("username", uName);
            jObj.put("password", pswd);

            String ab = doAPICall(jObj);
            Log.d(TAG, "doInBackground: " + ab);
            return doAPICall(jObj);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private String doAPICall(JSONObject jsonObject) {
        HttpURLConnection connect = null;
        BufferedReader read = null;

        try {

            String urlstr = urlAdd + loginURL;  // Build the full URL

            Uri uri = Uri.parse(urlstr);    // Convert String url to URI
            URL url = new URL(uri.toString()); // Convert URI to URL

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");  // POST - others might use PUT, DELETE, GET

            // Set the Content-Type and Accept properties to use JSON data
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connect.setRequestProperty("Accept", "application/json");
            connect.connect();

            // Write the JSON (as String) to the open connection
            OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int resp = connect.getResponseCode();

            StringBuilder result = new StringBuilder();
            Log.d(TAG, "doAPICall: response" + resp);
            // If successful (HTTP_OK)
            if (resp == HTTP_OK) {
                code=resp;
                // Read the results - use connection's getInputStream
                read = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String line;
                while (null != (line = read.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
                return result.toString();

            } else {
                // Not HTTP_OK - some error occurred - use connection's getErrorStream
                code=resp;
                read = new BufferedReader(new InputStreamReader(connect.getErrorStream()));
                String line;
                while (null != (line = read.readLine())) {
                    result.append(line).append("\n");
                }

                // Return the results (to onPostExecute)
                return result.toString();
            }

        } catch (Exception e) {
            // Some exception occurred! Log it.
            Log.d(TAG, "doAuth: " + e.getClass().getName() + ": " + e.getMessage());

        } finally { // Close everything!
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
        return "Some error has occurred"; // Return an error message if Exception occurred
    }
}