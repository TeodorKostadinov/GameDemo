package com.example.fos.gamedemo.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fos.gamedemo.R;
import com.example.fos.gamedemo.cmn.Question;
import com.example.fos.gamedemo.db.WebServiceHelper;
import com.example.fos.gamedemo.location.LocationReciever;
import com.example.fos.gamedemo.location.LocationService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    public static final int MAX_QUESTION_DISTANCE = 5000;
    private static final int REQUEST_PERMISSIONS = 131;
    private GoogleMap map;
    private List<Question> questions;
    private HashMap<Marker, Question> markerMap;
    private AlertDialog dialog;
    private Button dialogBtn4;
    private TextView dialogText;
    private Button dialogBtn1;
    private Button dialogBtn2;
    private Button dialogBtn3;
    private DialogListener dialogListener;
    private Location lastLocation;
    private boolean mapCentered;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer)
    DrawerLayout drawerLayout;


    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.accept, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("asd");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("asda");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(mDrawerToggle);

        setSupportActionBar(toolbar);

        markerMap = new HashMap<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        WebServiceHelper helper = WebServiceHelper.getInstance(this);
        helper.readQuestions(listener);

        checkForPermissions();
    }

    private void checkForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] perms = new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

                requestPermissions(perms, REQUEST_PERMISSIONS);
                return;
            }
        }
        setupNotifications();
    }

    private void setupNotifications() {
//        LocationReciever.startLocationBroadcasting(this);
//        IntentFilter filter = new IntentFilter(LocationReciever.ACTION_NEW_LOCATION);
//        registerReceiver(new LocationReciever(), filter);

        Intent intent = new Intent(this, LocationReciever.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                30 * 1000, alarmIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean flag = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) flag = false;
            }
            if (flag) setupNotifications();
        }
    }

    private void centerMap(Location location) {
        CameraUpdate cameraPos = CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 11);
        map.animateCamera(cameraPos);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.e(TAG, "MyLoc:" + location.getLongitude() + ", " + location.getLatitude());
                if (!mapCentered) {
                    Location centerOfEarth = new Location("center");
                    centerOfEarth.setLatitude(0.0);
                    centerOfEarth.setLongitude(0.0);
                    if (location.distanceTo(centerOfEarth) > 10000) {
                        mapCentered = true;
                        centerMap(location);
                    }
                }
            }
        });
        if (lastLocation != null) {
            centerMap(lastLocation);
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Question q = markerMap.get(marker);
                Location markerLocation = new Location("MarkerPosition");
                markerLocation.setLatitude(marker.getPosition().latitude);
                markerLocation.setLongitude(marker.getPosition().longitude);
                Log.e(TAG, "Distance:" + map.getMyLocation().distanceTo(markerLocation));
                if (map.getMyLocation().distanceTo(markerLocation) < MAX_QUESTION_DISTANCE) {
                    showQuestionDialog(q, marker);
                } else {
                    showErrorDialog();
                }
                return false;
            }
        });
        if (questions != null) {
            setupQuestions();
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage("You are too far from the question. Get closer and try again.")
                .setNeutralButton("OK", null);
        builder.create().show();
    }

    private void showQuestionDialog(Question q, Marker marker) {
        if (dialog == null) {
            dialog = getAlertDialog();
        }
        dialogListener.setData(q, marker);
        dialogText.setText(q.getText());
        dialogBtn1.setText(q.getAnswers()[0]);
        dialogBtn2.setText(q.getAnswers()[1]);
        dialogBtn3.setText(q.getAnswers()[2]);
        dialogBtn4.setText(q.getAnswers()[3]);

        dialog.show();
    }

    class DialogListener implements View.OnClickListener {

        private Question q;
        private Marker m;

        public void setData(Question q, Marker m) {
            this.q = q;
            this.m = m;
        }

        @Override
        public void onClick(View v) {
            int indexClicked;
            switch (v.getId()) {
                case R.id.btn_1: {
                    indexClicked = 0;
                    break;
                }
                case R.id.btn_2: {
                    indexClicked = 1;
                    break;
                }
                case R.id.btn_3: {
                    indexClicked = 2;
                    break;
                }
                case R.id.btn_4: {
                    indexClicked = 3;
                    break;
                }
                default:
                    indexClicked = -1;
            }
            Log.e(TAG, "Clicked btn:" + indexClicked);
            if (q.getCorrect() == indexClicked) {
                dialog.hide();
                m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }
        }
    }

    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_question, null);
        dialogText = ((TextView) dialogView.findViewById(R.id.txt_question_text));
        dialogBtn1 = ((Button) dialogView.findViewById(R.id.btn_1));
        dialogBtn2 = ((Button) dialogView.findViewById(R.id.btn_2));
        dialogBtn3 = ((Button) dialogView.findViewById(R.id.btn_3));
        dialogBtn4 = ((Button) dialogView.findViewById(R.id.btn_4));

        dialogListener = new DialogListener();
        dialogBtn1.setOnClickListener(dialogListener);
        dialogBtn2.setOnClickListener(dialogListener);
        dialogBtn3.setOnClickListener(dialogListener);
        dialogBtn4.setOnClickListener(dialogListener);

        builder
                .setView(dialogView);
        return builder.create();
    }

    private void setupQuestions() {
        for (Question q : questions) {
            MarkerOptions markerOptions = new MarkerOptions().position(q.getLocation());
            Marker marker = map.addMarker(markerOptions);
            markerMap.put(marker, q);
        }
    }

    WebServiceHelper.QuestionsDownloadListener listener = new WebServiceHelper.QuestionsDownloadListener() {
        @Override
        public void onQuestionsDownloaded(List<Question> dwQuestions) {
            questions = dwQuestions;
            if (map != null) {
                setupQuestions();
            }
        }

        @Override
        public void onQuestionsError() {
            Toast.makeText(MapsActivity.this, "Error dw q", Toast.LENGTH_SHORT).show();
        }
    };
}
