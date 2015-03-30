package com.shenkar.tamar.todolist_vr6;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.util.StringTokenizer;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import android.support.v4.app.FragmentManager;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View.OnFocusChangeListener;
import android.widget.RadioButton;
import android.widget.Switch;


public class DisplayTaskAcitvity extends Activity {

    Task newTask = new Task();
    boolean isTaskNew = true;
    // DatePicker data
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMin;
    public final int REQUEST_CODE_GET_LOC = 3;//update locaition in 3 sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    GeofenceClass geofenceItem;
    List<Geofence> mGeofenceList = new ArrayList<Geofence>();

    private LocationRequest mLocationRequest;
    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_display_task_acitvity);
        EditText textTitle = (EditText) findViewById(R.id.textTitle);
        EditText textDescription = (EditText) findViewById(R.id.description);
        TextView textDate = (TextView) findViewById(R.id.showMyDate);
        TextView textHour = (TextView) findViewById(R.id.TimeEdit);

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMin = c.get(Calendar.MINUTE);



        if (intent.hasExtra("task")) {
            isTaskNew = false;
            newTask = (Task) intent.getSerializableExtra("task");
            textTitle.append(newTask.getTaskTitle());
            textDescription.append(newTask.getTaskDescription());
            textDate.append(newTask.getTaskDateReminder());
            textHour.append(newTask.getTaskHourReminder());
        } else {
            textDate.append(mYear + "/" + mMonth + "/" + mDay);
            textHour.append(mHour + " : " + mDay);
        }
        if(newTask.getHasLocation())
        {

            mApiClient.connect();
            Switch s = (Switch)findViewById(R.id.switch1);
            s.setChecked(true);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void deleteRecord(View v) {
        Intent returnIntent = new Intent();
        newTask.setTaskIsDeleted(true);

        returnIntent.putExtra("task", newTask);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
    private PendingIntent getGeofenceTransitionPendingIntent() {

        Intent intent = new Intent(getApplicationContext(), GeofencingReceiverIntentService.class);
        return PendingIntent.getService(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }
    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();

        EditText textTitle = (EditText) findViewById(R.id.textTitle);
        EditText textDescription = (EditText) findViewById(R.id.description);
        TextView textDate = (TextView) findViewById(R.id.showMyDate);
        TextView textHour = (TextView) findViewById(R.id.TimeEdit);
        String taskTitle = textTitle.getText().toString();
        String taskDescription = textDescription.getText().toString();
        String taskDate = textDate.getText().toString();
        String taskHour = textHour.getText().toString();

        if (isTaskNew) {
            newTask = new Task(0, taskTitle, taskDescription, taskDate, taskHour, 0);
        } else {
            newTask.setTaskTitle(taskTitle);
            newTask.setTaskDescription(taskDescription);
            newTask.setTaskDateReminder(taskDate);
        }

        returnIntent.putExtra("task", newTask);

        setResult(RESULT_OK, returnIntent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch(requestCode)
            {
                case REQUEST_CODE_GET_LOC:
                {
                    MapPoint point = (MapPoint)data.getSerializableExtra("latlng");
                    if(point==null)
                    {
                        Switch s = (Switch)findViewById(R.id.switch1);
                        s.setChecked(false);
                        return;
                    }
                    newTask.setLocation(point);
                    newTask.setHasLocation(true);
                }
                break;
            }
        }
        else
        {
            Switch s = (Switch)findViewById(R.id.switch1);
            s.setChecked(false);
        }
    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub

        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
        if(isTaskNew)
        {
            LocationServices.GeofencingApi.addGeofences(
                    mApiClient,
                    getGeofencingRequest(),
                    mGeofenceRequestIntent).setResultCallback((ResultCallback<com.google.android.gms.common.api.Status>) this);
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }
    protected synchronized void buildGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((OnConnectionFailedListener) this)
                .addApi(LocationServices.API).build();
    }

    public void showMap(View view) {

        Intent i = new Intent(this,MapActivity.class);
        startActivity(i);
    }
    public void showMapWindow(View view) {

        Switch s = (Switch)view;
        if(s.isChecked())
        {
            Intent i = new Intent(this,Map.class);
            startActivityForResult(i, REQUEST_CODE_GET_LOC);
        }
        else
        {
            if(newTask.getHasLocation())
            {
                List<String> ids =new ArrayList<String>();
                ids.add(String.valueOf(newTask.getId()));

                LocationServices.GeofencingApi.removeGeofences(mApiClient, ids);
                newTask.setHasLocation(false);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart(){
        super.onStart();
        //GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

   }
