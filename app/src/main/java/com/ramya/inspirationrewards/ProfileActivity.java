package com.ramya.inspirationrewards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "ProfileActivity";
    private TextView uname;
    private TextView loc;
    private TextView pts;
    private TextView dept;
    private TextView pos;
    private TextView pointsAvail;
    private TextView stytxt;
    private ImageView upic;
    private ProfileAdapter padap;
    private List<RewardRecords> rAList = new ArrayList<>();
    private TextView name;
    private TextView commentcnt;
    private String imgStr = "";

    private static final int EDIT_REQUEST_CODE = 1;
    private static final int DASH_REQUEST_CODE = 2;

    private RecyclerView rView;
    CreateProfile bean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon);
        setTitle("  Your Profile");

        rView = findViewById(R.id.userProfileRecycler);
        padap = new ProfileAdapter(this, rAList);
        rView.setAdapter(padap);
        rView.setLayoutManager(new LinearLayoutManager(this));

        name = findViewById(R.id.ProfileName);
        uname = findViewById(R.id.ProfileUname);
        pos = findViewById(R.id.userProfilePos);
        loc = findViewById(R.id.userProfileLocation);
        dept = findViewById(R.id.userProfileDept);
        pointsAvail = findViewById(R.id.ProfilePt);
        pts = findViewById(R.id.userProfilePtAwarded);
        stytxt = findViewById(R.id.ProfileStoryText);
        stytxt.getBackground().setAlpha(80);
        upic = findViewById(R.id.ProfileImage);
        commentcnt = findViewById(R.id.ProfileCommentsCnt);
        Intent intent = getIntent();
        if (intent.hasExtra("USERPROFILE")) {
            Log.d(TAG, "onCreate: ");
            bean = (CreateProfile) intent.getSerializableExtra("USERPROFILE");
            Log.d(TAG, "getUserProfileAct: " + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {
                name.setText(bean.getLastName() + ", " + bean.getFirstName());
                uname.setText("(" + bean.getUsername() + ")");
                loc.setText(bean.getLocation());
                pointsAvail.setText("  " + bean.getPointsToAward());
                dept.setText("  " + bean.getDepartment());
                pos.setText("  " + bean.getPosition());
                stytxt.setText(bean.getStory());

                imgStr = bean.getImageBytes();
                byte[] imageBytes = Base64.decode(imgStr, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                upic.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent arrListIntent = getIntent();
        if (intent.hasExtra("USERPROFILE_LIST")) {
            List<RewardRecords> list = new ArrayList<>();
            rAList.clear();
            list = (List<RewardRecords>) intent.getSerializableExtra("USERPROFILE_LIST");
            if (list != null) {
                int totalRewards = 0;
                for (int i = 0; i < list.size(); i++) {
                    RewardRecords rewardRec = list.get(i);
                    totalRewards += rewardRec.getValue();
                }
                pts.setText("  " + totalRewards);
                commentcnt.setText("(" + list.size() + "):");
            } else {
                pts.setText("0");
                commentcnt.setText("(" + list.size() + "):");
            }

            rAList.addAll(list);
            padap.notifyDataSetChanged();
        }
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "Image Size: " + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.editMenu:
                Log.d(TAG, "onOptionsItemSelected: EditClick" + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
                Intent i = new Intent(this, EditActivity.class);
                i.putExtra("EDITPROFILE", bean);
                i.putExtra("EDITPROFILE_LIST", (Serializable) rAList);
                startActivityForResult(i, EDIT_REQUEST_CODE);
                return true;
            case R.id.dashboardMenu:
                Log.d(TAG, "onOptionsItemSelected: DashboardClick");
                Intent dintent = new Intent(this, LeaderBoardActivityInspiration.class);
                dintent.putExtra("INSPLEADPROFILE", bean);
                startActivityForResult(dintent, DASH_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        Log.d(TAG, "onActivityResult: Profile: " + reqCode + " " + resCode);
        if (reqCode == EDIT_REQUEST_CODE) {
            if (resCode == RESULT_OK) {
                getIntentFromEditToProfile(data);
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resCode);
            }
        } else if (reqCode == DASH_REQUEST_CODE) {
            if (resCode == RESULT_OK) {
                getIntentFromInspToProfile(data);
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resCode);
            }
        } else {
            Log.d(TAG, "onActivityResult: Request Code " + reqCode);
        }
    }

    public void getIntentFromInspToProfile(Intent data) {
        if (data.hasExtra("POINTSTOAWARD")) {
            Log.d(TAG, "getIntentFromInspToProfile: "+data.getStringExtra("POINTSTOAWARD"));
            int points=0;
            points=Integer.parseInt(data.getStringExtra("POINTSTOAWARD"));
            pointsAvail.setText("  " + data.getStringExtra("POINTSTOAWARD"));
            bean.setPointsToAward(points);
        }
    }

    public void getIntentFromEditToProfile(Intent data) {
        if (data.hasExtra("USERPROFILE")) {
            Log.d(TAG, "getIntentFromEditToProfile: ");
            bean = (CreateProfile) data.getSerializableExtra("USERPROFILE");
            Log.d(TAG, "getUserProfileAct: " + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {
                name.setText(bean.getLastName() + ", " + bean.getFirstName());
                uname.setText("(" + bean.getUsername() + ")");
                loc.setText(bean.getLocation());
                //Update
                pos.setText("  " + bean.getPosition());
                stytxt.setText(bean.getStory());
                pointsAvail.setText("  " + bean.getPointsToAward());
                dept.setText("  " + bean.getDepartment());

                //Image Transformation
                imgStr = bean.getImageBytes();
                byte[] imageBytes = Base64.decode(imgStr, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                upic.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (data.hasExtra("USERPROFILE_LIST")) {
            List<RewardRecords> list = new ArrayList<>();
            rAList.clear();
            list = (List<RewardRecords>) data.getSerializableExtra("USERPROFILE_LIST");
            if (list != null) {
                int totalRewards = 0;
                for (int i = 0; i < list.size(); i++) {
                    RewardRecords rewardRec = list.get(i);
                    totalRewards += rewardRec.getValue();
                }
                pts.setText("  " + totalRewards);
                commentcnt.setText("(" + list.size() + "):");
            } else {
                pts.setText("0");
                commentcnt.setText("(" + list.size() + "):");
            }

            rAList.addAll(list);
            padap.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
