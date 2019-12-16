package com.ramya.inspirationrewards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RewardActivity extends AppCompatActivity {
    private static final String TAG = "RewardActivity";


    private TextView pointsAwarded;
    private TextView dept;
    private TextView pos;
    private TextView stxt;
    private ImageView upic;
    private TextView name;
    private String imgStr = "";
    private EditText rEdit;
    private EditText cmmtEdit;
    private TextView cmtview;
    LeaderBoardInspiration bean = null;

    //To send over API
    private String stdsrc = "";
    private String usrc = "";
    private String psrc = "";
    private String stdtarget = "";
    private String utarget = "";
    private String nameTarget = "";
    private String dtar = "";
    private String notes = "";
    private int val = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        //Setting ActionBar icon and title
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon);

        name = findViewById(R.id.awarduName);
        pointsAwarded = findViewById(R.id.awardProfilePtAwarded);
        dept = findViewById(R.id.awardDept);
        pos = findViewById(R.id.awardProfilePos);
        stxt = findViewById(R.id.awardProfileStory);
        upic = findViewById(R.id.awardimg);
        cmmtEdit = findViewById(R.id.awardProfileComment);
        rEdit = findViewById(R.id.awardProfilePtSend);
        cmtview = findViewById(R.id.awardProfileCmtLen);
        cmmtEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                String countText = "( " + len + " of 80 )";
                cmtview.setText(countText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("AWARDPROFILE")) {
            bean = (LeaderBoardInspiration) intent.getSerializableExtra("AWARDPROFILE");
            Log.d(TAG, "getAwardProfileAct: " + bean.getImageBytes().length() + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {
                setTitle(" " + bean.getFirstName() + " " + bean.getLastName());
                name.setText(bean.getLastName() + ", " + bean.getFirstName());
                //Update
                pointsAwarded.setText("  " + bean.rewardPtAward);
                dept.setText("  " + bean.getDepartment());
                pos.setText("  " + bean.getPosition());
                stxt.setText(bean.getStory());
                //Image Transformation
                imgStr = bean.getImageBytes();
                byte[] imgBytes = Base64.decode(imgStr, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                upic.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: RewardActivity");
        switch (item.getItemId()) {
            case R.id.saveMenu:
                saveChangesDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveChangesDialog() {
        Log.d(TAG, "saveChangesDialog: ");

        if (!rEdit.getText().toString().isEmpty() &&!cmmtEdit.getText().toString().isEmpty()) {
            saveAlertOnActivityCreate();
        } else {
            warningDialog("Warning dialog on Incomplete data fields");
        }
    }

    public void warningDialog(String logStmt) {
        Log.d(TAG, logStmt);
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Incomplete Profile Data!");
        build.setIcon(R.drawable.ic_warning_black_24dp);
        build.setMessage("Please fill all the fields...");
        AlertDialog dialog = build.create();
        dialog.show();
    }

    public void saveAlertOnActivityCreate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Rewards Points?");
        builder.setIcon(R.drawable.logo);
        builder.setMessage("Add rewards for "+bean.getFirstName()+ " "+bean.getLastName()+"?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: OK button Clicked");
                callAsyncAPI();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
    }

    public void callAsyncAPI() {
        Intent logint = getIntent();
        stdsrc = logint.getStringExtra("STUDENTIDLOGIN");
        psrc = logint.getStringExtra("PASSLOGIN");
        usrc = logint.getStringExtra("UNAMELOGIN");
        nameTarget = logint.getStringExtra("NAMELOGIN");
        utarget = bean.getUsername();
        stdtarget = bean.getStudentId();
        notes = cmmtEdit.getText().toString().trim();
        val = Integer.parseInt(rEdit.getText().toString());
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/YYYY");
        dtar = format.format(new Date());
        Log.d(TAG, "callAsyncAPI: value" + val);

        Log.d(TAG, "callAsyncAPI: " + usrc + psrc + stdsrc + stdtarget + utarget + nameTarget + dtar + notes + val);
        new RewardsAPIAsyncTask(this, new RewardsBean(stdsrc, usrc, psrc, stdtarget, utarget, nameTarget, dtar, notes, val)).execute();
    }

    public void getRewardsAPIResp(String resp) {
        Log.d(TAG, "getRewardsAPIResp: from API " + resp);
        if (resp.contains("Reward Added")) {
            makeCustomToast(RewardActivity.this, resp, Toast.LENGTH_SHORT);
            Intent i = new Intent(RewardActivity.this, LeaderBoardActivityInspiration.class);
            i.putExtra("STIDLOGIN", stdsrc);
            i.putExtra("PSSLOGIN", psrc);
            i.putExtra("USERLOGIN", usrc);
            setResult(RESULT_OK,i);
            finish();
        } else {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Error!");
            build.setIcon(R.drawable.ic_warning_black_24dp);
            build.setMessage(resp);
            AlertDialog dialog = build.create();
            dialog.show();
        }
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
