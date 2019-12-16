package com.ramya.inspirationrewards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoardActivityInspiration extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "LeaderBoardActivityInspiration";
    private static final int INSP_REQUEST_CODE = 1;
    private RecyclerView rView;
    private int pts = 0;
    private static final int REWAED_REQUEST_CODE = 2;
    CreateProfile bean = null;
    private final List<LeaderBoardInspiration> LeaderArrayList = new ArrayList<>();
    private LeaderAdapter LeaderAdap;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_with_logo);
        setTitle("  Inspiration Leaderboard");

        //RecyclerView
        rView = findViewById(R.id.recycler);
        LeaderAdap = new LeaderAdapter(this, LeaderArrayList);
        rView.setAdapter(LeaderAdap);
        rView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent.hasExtra("INSPLEADPROFILE")) {
            bean = (CreateProfile) intent.getSerializableExtra("INSPLEADPROFILE");
            Log.d(TAG, "getInspLeaderboard: " + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {

                pts = bean.getPointsToAward();
                Log.d(TAG, "onCreate: PointsToAward onCreate" + pts);
                new GetAllProfilesAPIAsyncTask(LeaderBoardActivityInspiration.this).execute(bean.getStudentId(), bean.getUsername(), bean.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onClick(View v) {
        int pos = rView.getChildLayoutPosition(v);

        LeaderBoardInspiration beanRec = LeaderArrayList.get(pos);
        Log.d(TAG, "onClick: Insp" + bean.getStudentId() + " " + bean.getUsername() + " " + bean.getPassword() + " ");
        Intent intent = new Intent(this, RewardActivity.class);
        intent.putExtra("AWARDPROFILE", beanRec);
        intent.putExtra("STUDENTIDLOGIN", bean.getStudentId());
        intent.putExtra("PASSLOGIN", bean.getPassword());
        intent.putExtra("UNAMELOGIN", bean.getUsername());
        intent.putExtra("NAMELOGIN", bean.getFirstName() + " " + bean.getLastName());
        startActivityForResult(intent, REWAED_REQUEST_CODE);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        Log.d(TAG, "onActivityResult: Insp: " + reqCode + " " + resCode);
        if (reqCode == REWAED_REQUEST_CODE) {
            if (resCode == RESULT_OK) {
                getIntentFromRewardToInsp(data);
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resCode);
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code " + reqCode);
        }
    }

    @SuppressLint("LongLogTag")
    public void getIntentFromRewardToInsp(Intent data) {
        if (data.hasExtra("STIDLOGIN")) {
            Log.d(TAG, "getInspLeaderboard: to refresh Leaderboard");
            try {
                new GetAllProfilesAPIAsyncTask(LeaderBoardActivityInspiration.this).execute(data.getStringExtra("STIDLOGIN")
                        , data.getStringExtra("USERLOGIN"), data.getStringExtra("PSSLOGIN"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @SuppressLint("LongLogTag")
    public void getAllProfilesAPIResp(List<LeaderBoardInspiration> respList, String connres) {
        LeaderArrayList.clear();
        Log.d(TAG, "getAllProfilesAPIResp: " + connres);
        if (respList != null) {
            for (int i = 0; i < respList.size(); i++) {
                if (bean.getUsername().trim().equals(respList.get(i).getUsername().trim())) {
                    pts = respList.get(i).getPointsToAward();
                    Log.d(TAG, "getAllProfiles: PointsToAward" + pts);
                    break;
                }
            }
        }
        if (respList != null) {
            for (int i = 0; i < respList.size(); i++) {
                Log.d(TAG, "getAllProfilesAPIResp: Usernames: " + respList.size()
                        + respList.get(i).getUsername());
            }
        }
        Collections.sort(respList);
        LeaderArrayList.addAll(respList);
        LeaderAdap.notifyDataSetChanged();
    }

    @SuppressLint("LongLogTag")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: Up Navigation");
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("POINTSTOAWARD", "" + pts);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        makeCustomToast(this, "Back to Profile Page", Toast.LENGTH_SHORT);
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("POINTSTOAWARD", "" + pts);
        setResult(RESULT_OK, i);
        finish();
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "" + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }
}
