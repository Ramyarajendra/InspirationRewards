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

public class GetAllProfilesAPIAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetAllProfilesAPIAsyncTask";
    private LeaderBoardActivityInspiration LeaderBoardActivityInspiration;
    private final String urladd = "http://inspirationrewardsapi-env.6mmagpm2pv.us-east-2.elasticbeanstalk.com";
    private final String loginURL = "/allprofiles";
    private List<LeaderBoardInspiration> LeaderArrayList =new ArrayList<>() ;
    int code=-1;
    public GetAllProfilesAPIAsyncTask(LeaderBoardActivityInspiration LeaderBoardActivityInspiration) {
        this.LeaderBoardActivityInspiration = LeaderBoardActivityInspiration;
    }

    @SuppressLint("LongLogTag")
    @Override
    protected String doInBackground(String... strings) {
        String stuId = strings[0];
        String uName = strings[1];
        String pswd = strings[2];

        try {
            JSONObject jObj = new JSONObject();
            jObj.put("studentId", stuId);
            jObj.put("username", uName);
            jObj.put("password", pswd);

            Log.d(TAG, "doInBackground: " );
            return doAPICall(jObj);

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

            String urlString = urladd + loginURL;

            Uri uri = Uri.parse(urlString);
            URL url = new URL(uri.toString());

            connec = (HttpURLConnection) url.openConnection();
            connec.setRequestMethod("POST");

            connec.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connec.setRequestProperty("Accept", "application/json");
            connec.connect();

            OutputStreamWriter out = new OutputStreamWriter(connec.getOutputStream());
            out.write(jsonObject.toString());
            out.close();

            int respCode = connec.getResponseCode();

            StringBuilder result = new StringBuilder();
            if (respCode == HTTP_OK) {
                code=respCode;

                read = new BufferedReader(new InputStreamReader(connec.getInputStream()));
                String line;
                while (null != (line = read.readLine())) {
                    result.append(line).append("\n");
                }


                return result.toString();

            } else {
                code=respCode;
                read = new BufferedReader(new InputStreamReader(connec.getErrorStream()));
                String line;
                while (null != (line = read.readLine())) {
                    result.append(line).append("\n");
                }

                return result.toString();
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
                    Log.e(TAG, "doInBackground: closing: " + e.getMessage());
                }
            }
        }
        return "error occurred";
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(String connectionResult) {
        Log.d(TAG, "onPostExecute: " );
        LeaderBoardInspiration bean;
        if (code == HTTP_OK) {
            try {
                JSONArray jArray = new JSONArray(connectionResult);
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jObj = jArray.getJSONObject(i);
                    String studentId = jObj.getString("studentId");
                    String username = jObj.getString("username");
                    String password = jObj.getString("password");
                    String department = jObj.getString("department");
                    String story = jObj.getString("story");
                    String position = jObj.getString("position");
                    String fName = jObj.getString("firstName");
                    String lName = jObj.getString("lastName");
                    int pointsToAward = jObj.getInt("pointsToAward");
                    String location = jObj.getString("location");
                    String imageBytes = jObj.getString("imageBytes");
                    boolean admin = jObj.getBoolean("admin");
                    String rewards1 = jObj.getString("rewards");
                    List<RewardRecords> rewardsList = new ArrayList<>();

                    if (rewards1 != "null") {
                        JSONArray rewards = new JSONArray(rewards1);
                        for (int j = 0; j < rewards.length(); j++) {
                            RewardRecords rewardRecordsBean = new RewardRecords();
                            JSONObject reward = rewards.getJSONObject(j);
                            String studentIdR = reward.getString("studentId");
                            String usernameR = reward.getString("username");
                            String nameR = reward.getString("name");
                            String notesR = reward.getString("notes");
                            String dateR = reward.getString("date");
                            int valueR = reward.getInt("value");
                            rewardRecordsBean.setStudentId(studentIdR);
                            rewardRecordsBean.setUsernameRecord(usernameR);
                            rewardRecordsBean.setName(nameR);
                            rewardRecordsBean.setDate(dateR);
                            rewardRecordsBean.setNotes(notesR);
                            rewardRecordsBean.setValue(valueR);
                            rewardsList.add(rewardRecordsBean);
                        }
                        bean = new LeaderBoardInspiration(studentId, username, password, fName, lName, pointsToAward, department, story, position, admin, location, imageBytes, rewardsList);
                    }
                    else
                        bean = new LeaderBoardInspiration(studentId, username, password, fName, lName, pointsToAward, department, story, position, admin, location, imageBytes, null);
                    LeaderArrayList.add(bean);
                }
                LeaderBoardActivityInspiration.getAllProfilesAPIResp(LeaderArrayList,"SUCCESS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if (code == -1)
            LeaderBoardActivityInspiration.getAllProfilesAPIResp(null,"Error Occured");
        else
        {
            Log.d(TAG, "onPostExecute: Inside else loop ");
            try {
                JSONObject errDetailsJson = new JSONObject(connectionResult);
                JSONObject errJsonMsg = errDetailsJson.getJSONObject("errordetails");
                String errMsg = errJsonMsg.getString("message");
                Log.d(TAG, "onPostExecute: Error " + errMsg);
                LeaderBoardActivityInspiration.getAllProfilesAPIResp(null,errMsg.split("\\{")[0].trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



    }
}
