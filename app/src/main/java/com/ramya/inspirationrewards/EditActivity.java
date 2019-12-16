package com.ramya.inspirationrewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    private EditText unameEdit;
    private EditText psswdedit;
    private EditText fEdit;
    private EditText storyEdit;
    private ImageView uPhoto;
    private TextView avail;
    private TextView lEdit;
    private EditText deptEdit;
    private TextView posEdit;
    private CheckBox chkbx;
    CreateProfile bean = null;
    CreateProfile respBean = null;
    private String locationFromLatLong = "";
    private String studentId = "A20449938";
    private String uname = "";
    private String password = "";
    private String fName = "";
    private String lName = "";
    private int pointsToAward ;
    private String dept = "";
    private String story = "";
    private String pos = "";
    private boolean admin = false;
    private String rewards = "";
    private static int STORAGE_REQUEST_CODE = 329;
    private static final int U_REQUEST_CODE = 1;
    private File curimg;
    private int REQUEST_GALLERY = 1;
    private int REQUEST_CAPTURE = 2;
    private String imgString = "";
    private static int LOCATION_REQUEST_CODE = 330;
    private LocationManager locationManager;
    private Location curloc;
    private Criteria criteria;
    private double latitude;
    private double longitude;
    private List<RewardRecords> rewardArrayList = new ArrayList<>();
    Intent dataFromEditToProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //Setting ActionBar icon and title
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_with_logo);
        setTitle("  Edit Profile");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        unameEdit = findViewById(R.id.editUname);
        psswdedit = findViewById(R.id.editpwd);
        chkbx = findViewById(R.id.editChkBox);
        fEdit = findViewById(R.id.editFirstName);
        lEdit = findViewById(R.id.editLastName);
        deptEdit = findViewById(R.id.editDept);
        posEdit = findViewById(R.id.editPos);
        storyEdit = findViewById(R.id.editStory);
        avail = findViewById(R.id.editAvailChars);
        uPhoto = findViewById(R.id.editpic);

        storyEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.toString().length();
                String countText = "( " + len + " of 360 )";
                avail.setText(countText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadDatafromUserProfile();

        //Location Loading
        setLocationPrereq();
    }

    public void setLocationPrereq() {
        Log.d(TAG, "setLocationPrereq: ");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

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

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + permissions);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                openCameraGalleryDialog();
                return;
            }
        } else if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        } else {
            makeCustomToast(this, "Permission denied!", Toast.LENGTH_LONG);
        }
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        Log.d(TAG, "setLocation: ");
        String bestProvider = locationManager.getBestProvider(criteria, true);
        curloc = locationManager.getLastKnownLocation(bestProvider);
        List<Address> addresses;
        if (curloc != null) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                latitude = (double) curloc.getLatitude();
                longitude = (double) curloc.getLongitude();
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                displayAddresses(addresses);
                Log.d(TAG, "setLocation: " + addresses.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            makeCustomToast(this, "Location Unavailable", Toast.LENGTH_SHORT);
        }
    }

    public void displayAddresses(List<Address> addresses) {
        StringBuilder sb = new StringBuilder();
        if (addresses.size() == 0) {
            locationFromLatLong = "No Location Found";
            return;
        }

        for (Address ad : addresses) {

            String a = String.format("%s, %s",
                    (ad.getLocality() == null ? "" : ad.getLocality()),
                    (ad.getAdminArea() == null ? "" : ad.getAdminArea())
            );

            if (!a.trim().isEmpty())
                sb.append(" ").append(a.trim());
            sb.append("\n");
        }

        locationFromLatLong = sb.toString().trim();
        Log.d(TAG, "displayAddresses: " + sb.toString().trim());
    }

    public void loadDatafromUserProfile() {
        Intent intent = getIntent();
        if (intent.hasExtra("EDITPROFILE")) {
            bean = (CreateProfile) intent.getSerializableExtra("EDITPROFILE");
            Log.d(TAG, "loadDatafromUserProfile: " + bean.getUsername() + bean.getFirstName() + bean.getLastName() + bean.getLocation() + bean.getDepartment() + bean.getPassword() + bean.getPosition() + bean.getStory() + bean.getPointsToAward());
            try {
                unameEdit.setText(bean.getUsername());
                psswdedit.setText(bean.getPassword());
                chkbx.setChecked(false);
                fEdit.setText(bean.getFirstName());
                lEdit.setText(bean.getLastName());
                deptEdit.setText(bean.getDepartment());
                posEdit.setText(bean.getPosition());
                storyEdit.setText(bean.getStory());
                //Image Transformation
                imgString = bean.getImageBytes();
                byte[] imageBytes = Base64.decode(imgString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                uPhoto.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent arrListIntent=getIntent();
        if (intent.hasExtra("EDITPROFILE_LIST")) {
            List<RewardRecords> list=new ArrayList<>();
            list=(List<RewardRecords>) intent.getSerializableExtra("EDITPROFILE_LIST");
            rewardArrayList.addAll(list);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: editActivity");
        switch (item.getItemId()) {
            case R.id.saveMenu:
                saveChangesDialog();
                return true;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: Up Navigation");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveChangesDialog() {
        Log.d(TAG, "saveChangesDialog: " + locationFromLatLong + imgString);
        //Setting the Location to Chicago, Illinois if location is not found by Location Service
        if (locationFromLatLong.isEmpty())
            locationFromLatLong = "Chicago, Illinois";
        if (imgString == "")
            ifNewImageIsNotSelected();
        if (!unameEdit.getText().toString().isEmpty() && !psswdedit.getText().toString().isEmpty() && !fEdit.getText().toString().isEmpty()
                && !lEdit.getText().toString().isEmpty() && !deptEdit.getText().toString().isEmpty() && !posEdit.getText().toString().isEmpty()
                && !storyEdit.getText().toString().isEmpty() && locationFromLatLong != "" && imgString != "") {
            saveAlertOnActivityCreate();
        } else {
            warningDialog("Warning dialog on Incomplete data fields");
        }
    }

    public void warningDialog(String logStmt) {
        Log.d(TAG, logStmt);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Incomplete Profile Data!");
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        builder.setMessage("Please fill all the fields...");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void ifNewImageIsNotSelected() {
        Bitmap bm = ((BitmapDrawable) uPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 0, bitmapAsByteArrayStream);
        imgString = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "processCamera: baseEncoder" + imgString);
    }

    public void saveAlertOnActivityCreate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes?");
        builder.setIcon(R.drawable.logo);
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

    public void captureImageEditActivity(View v) {
        Log.d(TAG, "setFilePermissions: ");
        int permExt = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permExt != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    STORAGE_REQUEST_CODE);
        } else {
            openCameraGalleryDialog();
            makeCustomToast(this, "Permission pre granted", Toast.LENGTH_LONG);
        }
    }

    public void openCameraGalleryDialog() {
        Log.d(TAG, "openCameraGalleryDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Picture");
        builder.setIcon(R.drawable.logo);
        builder.setMessage("Take picture from: ");

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
        Intent PicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PicIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(curimg));
        startActivityForResult(PicIntent, REQUEST_CAPTURE);
    }

    public void doGallery() {
        Intent photointent = new Intent(Intent.ACTION_PICK);
        photointent.setType("image/*");
        startActivityForResult(photointent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + REQUEST_CAPTURE + " " + resCode + " " + reqCode + " " + data);
        if (reqCode == REQUEST_GALLERY && resCode == RESULT_OK) {
            try {
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

    private void processCamera() {
        Uri secimg = Uri.fromFile(curimg);
        uPhoto.setImageURI(secimg);
        Bitmap bm = ((BitmapDrawable) uPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream bitStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bitStream);
        imgString = Base64.encodeToString(bitStream.toByteArray(), Base64.DEFAULT);
        Log.d(TAG, "processCamera: baseEncoder" + imgString);
        makeCustomToast(this, "Photo Size: " + String.format(Locale.getDefault(), "%,d", bm.getByteCount()), Toast.LENGTH_LONG);
        curimg.delete();
    }

    private void processGallery(Intent data) {
        Uri gllryimg = data.getData();
        if (gllryimg == null)
            return;

        InputStream imgstr = null;
        try {
            imgstr = getContentResolver().openInputStream(gllryimg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap secimg = BitmapFactory.decodeStream(imgstr);
        uPhoto.setImageBitmap(secimg);
        makeCustomToast(this, String.format(Locale.getDefault(), "%,d", secimg.getByteCount()), Toast.LENGTH_LONG);
        Bitmap bm = ((BitmapDrawable) uPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream bitmapAsByteArrayStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bitmapAsByteArrayStream);
        imgString = Base64.encodeToString(bitmapAsByteArrayStream.toByteArray(), Base64.DEFAULT);
        makeCustomToast(this, "Image size over Network: " + bm.getByteCount(), Toast.LENGTH_LONG);
        Log.d(TAG, "processCamera: baseEncoder" + imgString);
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

    public void callAsyncAPI() {
        uname = unameEdit.getText().toString();
        password = psswdedit.getText().toString();
        fName = fEdit.getText().toString();
        dept = deptEdit.getText().toString();
        story = storyEdit.getText().toString().trim();
        lName = lEdit.getText().toString();
        pos = posEdit.getText().toString();
        Log.d(TAG, "callAsyncAPI: " + imgString);
        respBean = new CreateProfile(studentId, uname, password, fName, lName, bean.getPointsToAward(), dept, story, pos, admin, locationFromLatLong, imgString);
        new UpdateProfileAPIAsyncTask(this, respBean,rewardArrayList).execute();
    }

    public void getEditProfileAPIResp(String res) {
        Log.d(TAG, "getEditProfileAPIResp: " + res);

        if (res.trim().equals("SUCCESS")) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("USERPROFILE_LIST", (Serializable) rewardArrayList);
            i.putExtra("USERPROFILE", respBean);
            setResult(RESULT_OK,i);
            finish();
            makeCustomToast(EditActivity.this, "Updated Successfully", Toast.LENGTH_SHORT);
        }
        else
        {
            AlertDialog.Builder build = new AlertDialog.Builder(this);
            build.setTitle("Error");
            build.setIcon(R.drawable.ic_warning_black_24dp);
            build.setMessage(res);
            AlertDialog dialog = build.create();
            dialog.show();
        }

    }
    @Override
    public void onBackPressed()
    {
        finish();
        makeCustomToast(EditActivity.this, "Information not saved", Toast.LENGTH_SHORT);
    }

}
