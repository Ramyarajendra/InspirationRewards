package com.ramya.inspirationrewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.location.Address;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ActivityCreate extends AppCompatActivity {
    private static final String TAG = "ActivityCreate";
    private static int STORAGE_REQUEST_CODE = 329;
    private static int LOCATION_REQUEST_CODE = 330;
    private LocationManager locManager;
    private Location curLoc;
    private EditText storyEdit;
    private ImageView addUser;
    private TextView avail;
    private File curimg;
    private EditText uName;
    private EditText psswdEdit;
    private EditText fEdit;
    private TextView lEdit;
    private Criteria crit;
    private TextView posEdit;
    private EditText deptEdit;
    private int pts =1000;
    private String dept = "";
    private int REQUEST_GALLERY = 1;
    private int REQUEST_CAPTURE = 2;
    private String imgStr = "";
    private double latitude;
    private double longitude;
    private String loclatlong = "";
    private String stdid = "A20449938";
    private String uname = "";
    private String pwd = "";
    private String fName = "";
    private String lName = "";
    private String story = "";
    private String pos = "";
    private boolean admin = false;
    private String rewards = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_with_logo);
        setTitle("  Create Profile");

        uName = findViewById(R.id.createUname);
        psswdEdit = findViewById(R.id.createPass);
        fEdit = findViewById(R.id.createFirstName);
        deptEdit = findViewById(R.id.createDept);
        lEdit = findViewById(R.id.createLastName);
        storyEdit = findViewById(R.id.createStory);
        posEdit = findViewById(R.id.createPos);
        avail = findViewById(R.id.availChars);
        addUser = findViewById(R.id.createUserPhoto);
        setLocationPrereq();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        storyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                String cntText = "( " + len + " of 360 )";
                avail.setText(cntText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void setLocationPrereq() {
        Log.d(TAG, "setLocationPrereq: ");
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        crit = new Criteria();
        crit.setAltitudeRequired(false);
        crit.setBearingRequired(false);
        crit.setPowerRequirement(Criteria.POWER_LOW);
        crit.setAccuracy(Criteria.ACCURACY_MEDIUM);
        crit.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_REQUEST_CODE);
        } else {
            setLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        Log.d(TAG, "setLocation: ");
        String bestPro = locManager.getBestProvider(crit, true);
        curLoc = locManager.getLastKnownLocation(bestPro);
        List<Address> addr;
        if (curLoc != null) {
            try {
                Geocoder gcode = new Geocoder(this, Locale.getDefault());
                latitude = (double) curLoc.getLatitude();
                longitude = (double) curLoc.getLongitude();
                addr = gcode.getFromLocation(latitude, longitude, 1);
                displayAddresses(addr);
                Log.d(TAG, "setLocation: " + addr.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            makeCustomToast(this, "Location Unavailable", Toast.LENGTH_SHORT);
        }
    }

    public void displayAddresses(List<Address> addr) {
        StringBuilder sb = new StringBuilder();
        if (addr.size() == 0) {
            loclatlong = "No Location Found";
            return;
        }

        for (Address ad : addr) {

            String a = String.format("%s, %s",
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea())
            );

            if (!a.trim().isEmpty())
                sb.append(" ").append(a.trim());
            sb.append("\n");
        }
        loclatlong = sb.toString().trim();
        Log.d(TAG, "displayAddresses: " + loclatlong);
    }

    public void recheckLocation(View v) {
        makeCustomToast(this, "Rechecking Location", Toast.LENGTH_SHORT);
        setLocation();
    }


    public void captureImage(View v) {
        Log.d(TAG, "setFilePermissions: ");
        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (perm != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    STORAGE_REQUEST_CODE);
        } else {
            openCameraGalleryDialog();
            makeCustomToast(this, "Permission already granted", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int reqCode, @NonNull
            String[] perm, @NonNull
                    int[] grantres) {
        Log.d(TAG, "onRequestPermissionsResult: " + perm);
        super.onRequestPermissionsResult(reqCode, perm, grantres);
        if (reqCode == STORAGE_REQUEST_CODE) {
            if (perm[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    grantres[0] == PERMISSION_GRANTED) {
                openCameraGalleryDialog();
                return;
            }
        } else if (reqCode == LOCATION_REQUEST_CODE) {
            if (perm[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantres[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        } else {
            makeCustomToast(this, "Permission denied!", Toast.LENGTH_LONG);
        }
    }

    public void openCameraGalleryDialog() {
        Log.d(TAG, "openCameraGalleryDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setIcon(R.drawable.logo);
        builder.setMessage("Take picture from location: ");
        builder.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doGallery();
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                doCamera();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED);
    }

    public void doCamera() {
        Log.d(TAG, "doCamera: " + REQUEST_CAPTURE);
        curimg = new File(getExternalCacheDir(), "appimage_" + System.currentTimeMillis() + ".jpg");
        Intent picint = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        picint.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(curimg));
        startActivityForResult(picint, REQUEST_CAPTURE);
    }

    public void doGallery() {
        Intent picint = new Intent(Intent.ACTION_PICK);
        picint.setType("image/*");
        startActivityForResult(picint, REQUEST_GALLERY);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ActivityCreate");
        switch (item.getItemId()) {
            case R.id.saveMenu:
                saveChangesDialog();
                return true;
            case android.R.id.home:
                makeCustomToast(this, "New Profile Not Created", Toast.LENGTH_SHORT);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed()
    {
        finish();
        makeCustomToast(this, "New Profile Not Created", Toast.LENGTH_SHORT);
    }
    private void ifNewImageIsNotSelected() {
        Bitmap bm = ((BitmapDrawable) addUser.getDrawable()).getBitmap();
        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 0, bitmapAsByteArrayStream);
        imgStr = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "processCamera: baseEncoder" + imgStr);
    }

    public void warningDialog(String logStmt) {
        Log.d(TAG, logStmt);
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Profile Data Incomplete!");
        build.setIcon(R.drawable.ic_warning_black_24dp);
        build.setMessage("Please fill in all the fields...");
        AlertDialog dialog = build.create();
        dialog.show();
    }

    public void saveChangesDialog() {
        Log.d(TAG, "saveChangesDialog: " + loclatlong + imgStr);
        if (loclatlong.isEmpty()) {
            loclatlong = "Chicago, IL";
        }
        if (imgStr == "")
            ifNewImageIsNotSelected();
        if (!uName.getText().toString().isEmpty() && !psswdEdit.getText().toString().isEmpty() && !fEdit.getText().toString().isEmpty()
                && !lEdit.getText().toString().isEmpty() && !deptEdit.getText().toString().isEmpty() && !posEdit.getText().toString().isEmpty()
                && !storyEdit.getText().toString().isEmpty() && loclatlong != "" && imgStr != "") {
            saveAlertOnActivityCreate();
        } else {
            warningDialog("Warning dialog on Incomplete data fields");
        }
    }

    public void saveAlertOnActivityCreate() {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Save Changes?");
        build.setIcon(R.drawable.logo);
        build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: OK button Clicked");
                callAsyncAPI();
            }
        });

        build.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = build.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
    }

    public void callAsyncAPI() {
        uname = uName.getText().toString();
        pwd = psswdEdit.getText().toString();
        dept = deptEdit.getText().toString();
        story = storyEdit.getText().toString().trim();
        fName = fEdit.getText().toString();
        lName = lEdit.getText().toString();
        pos = posEdit.getText().toString();
        new CreateProfileAPIAsyncTask(this, new CreateProfile(stdid, uname, pwd, fName, lName, pts, dept, story, pos, admin, loclatlong, imgStr)).execute();
    }
    private void processCamera() {
        Uri secImg = Uri.fromFile(curimg);
        addUser.setImageURI(secImg);
        Bitmap bm = ((BitmapDrawable) addUser.getDrawable()).getBitmap();
        ByteArrayOutputStream bitStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bitStream);
        imgStr = Base64.encodeToString(bitStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "processCamera: baseEncoder" + imgStr);
        makeCustomToast(this, "Photo Size: " + String.format(Locale.getDefault(), "%,d", bm.getByteCount()), Toast.LENGTH_LONG);
        curimg.delete();
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + REQUEST_CAPTURE + " " + resCode + " " + reqCode + " " + data);
        if (reqCode == REQUEST_GALLERY && resCode == RESULT_OK) {
            try {
                Log.d(TAG, "onActivityResult: Gallery camera");
                processGallery(data);
            } catch (Exception e) {
                makeCustomToast(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        } else if (reqCode == REQUEST_CAPTURE && resCode == RESULT_OK) {
            try {
                Log.d(TAG, "onActivityResult: inside camera");
                processCamera();
            } catch (Exception e) {
                makeCustomToast(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        }
    }


    private void processGallery(Intent data) {
        Uri gllryImgUri = data.getData();
        if (gllryImgUri == null)
            return;

        InputStream imgStream = null;
        try {
            imgStream = getContentResolver().openInputStream(gllryImgUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap secImg = BitmapFactory.decodeStream(imgStream);
        addUser.setImageBitmap(secImg);
        makeCustomToast(this, String.format(Locale.getDefault(), "%,d", secImg.getByteCount()), Toast.LENGTH_LONG);
        Bitmap bm = ((BitmapDrawable) addUser.getDrawable()).getBitmap();
        ByteArrayOutputStream bitStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bitStream);
        imgStr = Base64.encodeToString(bitStream.toByteArray(), Base64.DEFAULT);
        makeCustomToast(this, "Image size over Network: " + bm.getByteCount(), Toast.LENGTH_LONG);
        Log.d(TAG, "processCamera: baseEncoder" + imgStr);

    }
    public void getCreateProfileAPIResp(CreateProfile respBean, String connres) {

        if(respBean==null)
        {
            makeCustomToast(this, connres, Toast.LENGTH_SHORT);
            return;
        }
        else
        {
            Intent i = new Intent(ActivityCreate.this, ProfileActivity.class);
            i.putExtra("USERPROFILE", respBean);
            startActivity(i);
            makeCustomToast(this, "User Created Successfully", Toast.LENGTH_SHORT);
            finish();
        }

    }

    public static void makeCustomToast(Context context, String message, int time) {
        Toast toast = Toast.makeText(context, "" + message, time);
        View tstView = toast.getView();
        tstView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView tv = toast.getView().findViewById(android.R.id.message);
        tv.setPadding(100, 50, 100, 50);
        tv.setTextColor(Color.WHITE);
        toast.show();
    }


}
