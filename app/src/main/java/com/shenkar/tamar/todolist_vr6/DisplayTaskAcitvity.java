package com.shenkar.tamar.todolist_vr6;

import java.text.ParseException;
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
/**
 * Created by tamar & haim on 3/22/15.
 tamar zanzuri : 200212777;
 haim yaakov : 204729107;
 */

public class DisplayTaskAcitvity extends Activity {

    Task newTask = new Task();
    boolean isTaskNew = true;
    public final int REQUEST_CODE_GET_LOC = 3;//update locaition in 3 sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // Stores the PendingIntent used to request geofence monitoring.
    private GoogleApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_display_task_acitvity);
        EditText textTitle = (EditText) findViewById(R.id.textTitle);
        EditText textDescription = (EditText) findViewById(R.id.description);
        EditText textDate = (EditText) findViewById(R.id.showMyDate);
        EditText textHour = (EditText) findViewById(R.id.TimeEdit);

        if (intent.hasExtra("task")) {
            isTaskNew = false;
            newTask = (Task) intent.getSerializableExtra("task");
            textTitle.append(newTask.getTaskTitle());
            textDescription.append(newTask.getTaskDescription());
            textDate.append(newTask.getTaskDateReminder());
            textHour.append(newTask.getTaskHourReminder());
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
        if(newTask.getTaskDateReminder()!=null)
        {
            Intent myIntent = new Intent(getBaseContext(), ReminderNotification.class);
            myIntent.putExtra("task", newTask);

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(getBaseContext(), (int) newTask.getId(), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();

        EditText textTitle = (EditText) findViewById(R.id.textTitle);
        EditText textDescription = (EditText) findViewById(R.id.description);
        EditText textDate = (EditText) findViewById(R.id.showMyDate);
        EditText textHour = (EditText) findViewById(R.id.TimeEdit);
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
            newTask.setTaskHourReminder(taskHour);
        }
        if(newTask.getTaskHourReminder() != "")
        {
            Intent myIntent = new Intent(getBaseContext(), ReminderNotification.class);
            myIntent.putExtra("task", newTask);

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(getBaseContext(), (int) newTask.getId(), myIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                Date date = fmt.parse(taskDate+" "+taskHour);
                calendar.setTimeInMillis(date.getTime());
//                Toast.makeText(DisplayTaskAcitvity.this, calendar.getTimeInMillis() + " ", Toast.LENGTH_SHORT).show();
                Log.d("time in miliseconds", calendar.getTimeInMillis() +"");
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        returnIntent.putExtra("task", newTask);

        setResult(RESULT_OK, returnIntent);
        finish();
    }




    protected synchronized void buildGoogleApiClient() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((OnConnectionFailedListener) this)
                .addApi(LocationServices.API).build();
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
