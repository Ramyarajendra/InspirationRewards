package com.ramya.inspirationrewards;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String sId = "A20449938";
    private static final int B_REQUEST_CODE = 1;
    private EditText uView;
    private EditText pView;
    private static final int UP_REQUEST_CODE = 2;
    private CheckBox chkbox;
    private ProgressBar pBar;
    public static final int MULTIPLE_PERMISSIONS = 1;
    private RewardsPref prefr;
    private TextView newAccView;
    private LocationManager locationManager;
    private Location currentLocation;
    private Criteria crit;
    private JSONObject jsonObj;
    private static int MY_LOCATION_REQUEST_CODE = 329;
    private static int MY_EXT_STORAGE_REQUEST_CODE = 330;
    MainActivity mainActivity = this;
    private ArrayList<RewardRecords> rewardsArrList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon);
        setTitle("  Rewards");

        if (isOnline())
            setFileLocationPermissions();
        else
            errorDialog("errorDialog: No Internet Connectivity!!", "No Internet Connection", "Application aborting");
        uView = (EditText) findViewById(R.id.usernameView);
        chkbox = (CheckBox) findViewById(R.id.remCred);
        pView = (EditText) findViewById(R.id.passowrdView);
        pBar=findViewById(R.id.progressBar);
        prefr = new RewardsPref(this);

        Log.d(TAG, "onCreate: " + prefr.getValue(getString(R.string.pref_user)));
        uView.setText(prefr.getValue(getString(R.string.pref_user)));
        pView.setText(prefr.getValue(getString(R.string.pref_pass)));
        chkbox.setChecked(prefr.getBoolValue(getString(R.string.pref_check)));
    }

    public void setFileLocationPermissions() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        crit = new Criteria();
        crit.setPowerRequirement(Criteria.POWER_LOW);
        crit.setAccuracy(Criteria.ACCURACY_MEDIUM);
        crit.setAltitudeRequired(false);
        crit.setBearingRequired(false);
        crit.setSpeedRequired(false);
        int permLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permExt = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> perm = new ArrayList<>();
        if (permLoc != PERMISSION_GRANTED) {
            perm.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permExt != PERMISSION_GRANTED) {
            perm.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!perm.isEmpty()) {
            ActivityCompat.requestPermissions(this, perm.toArray(new String[perm.size()]), MULTIPLE_PERMISSIONS);
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public void errorDialog(String logStmt, String title, String message) {
        int d = Log.d(TAG, logStmt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onLoginBtnClick(View v) {

        if (chkbox.isChecked()) {
            Log.d(TAG, "login button clicked ");
            prefr.save(getString(R.string.pref_user), uView.getText().toString());
            prefr.save(getString(R.string.pref_pass), pView.getText().toString());
            prefr.saveBool(getString(R.string.pref_check), chkbox.isChecked());
        }
        String uName = uView.getText().toString();
        String pswd = pView.getText().toString();

        if (uName.equals("") || pswd.equals("")) {
            errorDialog("errorDialog: Incomplete Input!", "Incomplete Input ", "Please enter a valid UserName/Password");
        } else {
            pBar.setVisibility(View.VISIBLE);
            new LoginAPIAsyncTask(mainActivity).execute(sId, uName, pswd);

        }
    }

    public void onNewAccCreateClick(View v) {
        Log.d(TAG, "onNewAccCreateClick: Main");
        if (isOnline()) {
            makeCustomToast(this, "Creating new Profile", Toast.LENGTH_SHORT);
            Intent i = new Intent(this, ActivityCreate.class);
            startActivityForResult(i, B_REQUEST_CODE);
        } else
            makeCustomToast(this, "No Internet Connection", Toast.LENGTH_SHORT);
    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, " " + message, time);
        View toastView = toast.getView();
        toastView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }

    public void getLoginAPIResp(CreateProfile respBean, List<RewardRecords> rList, String connectionResult) {
        Log.d(TAG, "getLoginAPIResp: " + respBean);
        if (respBean == null) {
            makeCustomToast(this, connectionResult, Toast.LENGTH_SHORT);
            pBar.setVisibility(View.GONE);
            return;
        } else {
            Log.d(TAG, "getLoginAPIResp: " + respBean.getUsername() + respBean.getFirstName() + respBean.getLastName() + respBean.getLocation() + respBean.getDepartment() + respBean.getPassword() + respBean.getPosition() + respBean.getStory() + respBean.getPointsToAward());

            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("USERPROFILE", respBean);
            intent.putExtra("USERPROFILE_LIST", (Serializable) rList);
            pBar.setVisibility(View.GONE);
            startActivity(intent);

        }
    }
}
