package com.shenkar.tamar.todolist_vr6;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.List;

import static com.shenkar.tamar.todolist_vr6.R.id.listViewInfo;


public class MainActivity extends ActionBarActivity {

    private static final int ADD_NEW_TASK = 1;
    private static final int EDIT_TASK = 2;

    private List<Task> taskListItems = new ArrayList<>();

    DBAdapter myDb;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_NEW_TASK) {
            if (resultCode == RESULT_OK) {
                Task myTask = (Task) data.getSerializableExtra("task");
                long id = myDb.insertRow(myTask.getTaskTitle(), myTask.getTaskDescription(), myTask.getTaskDateReminder(), myTask.getTaskHourReminder(), myTask.getTaskIsDone());

                myTask.setId(id);
                Toast.makeText(MainActivity.this, "id in db " + id + "id in task " + myTask.getId(), Toast.LENGTH_SHORT).show();
                taskListItems.add(myTask);
                populateListView();
            }
        }
        if (requestCode == EDIT_TASK) {
            if (resultCode == RESULT_OK) {
                Task myTask = (Task) data.getSerializableExtra("task");
                myDb.updateRow(myTask.getId(), myTask.getTaskTitle(), myTask.getTaskDescription(), myTask.getTaskDateReminder(), myTask.getTaskHourReminder(), myTask.getTaskIsDone());
                if (myTask.getTaskIsDeleted()) {
                    myDb.deleteRow(myTask.getId());
                    for (int i = 0; i < taskListItems.size(); i++) {
                        if (taskListItems.get(i).getId() == myTask.getId()) {
                            taskListItems.remove(i);
                        }
                    }
                }
                for (int i = 0; i < taskListItems.size(); i++) {
                    if (taskListItems.get(i).getId() == myTask.getId()) {
                        Toast.makeText(MainActivity.this, "task " + taskListItems.get(i).getId() + "found " + i + "new task " + myTask.getId(), Toast.LENGTH_SHORT).show();
                        taskListItems.set(i, myTask);
                    }
                }
                populateListView();
            }

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get a Tracker (should auto-report)
        ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

        openDB();
        populateListViewFromDB();
        registerListClickCallBack();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }


    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

    private void closeDB() {
        myDb.close();
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


    public void onClick_AddRecord(View view) {

        Intent intent = new Intent(this, DisplayTaskAcitvity.class);
        startActivityForResult(intent, ADD_NEW_TASK);
    }


    private void populateListViewFromDB() {

        Cursor cursor = myDb.getAllRows();

        if (cursor.moveToFirst()) {
            do {
                long idInDB = cursor.getLong(DBAdapter.COL_ROWID);
                String title = cursor.getString(DBAdapter.COL_TITLE);
                String description = cursor.getString(DBAdapter.COL_DESCRIPTION);
                String dateReminder = cursor.getString(DBAdapter.COL_DATEREMINDER);
                String hourReminder = cursor.getString(DBAdapter.COL_HOURREMINDER);
                int isDone = cursor.getInt(DBAdapter.COL_ISDONE);

                Task task = new Task(idInDB, title, description, dateReminder, hourReminder, isDone);
                taskListItems.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();

        populateListView();
    }

    private void populateListView() {
        ArrayAdapter<Task> taskArrayAdapter = new MyTaskAdapter();
        ListView listView = (ListView) findViewById(listViewInfo);
        listView.setAdapter(taskArrayAdapter);
    }

    private class MyTaskAdapter extends ArrayAdapter<Task> {

        public MyTaskAdapter() {
            super(MainActivity.this, R.layout.item_layout, taskListItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemVeiw = convertView;


            if (itemVeiw == null) {
                itemVeiw = getLayoutInflater().inflate(R.layout.item_layout, parent, false);
            }
            // find task to work with
            final Task currentTask = taskListItems.get(position);

            //fill the view
            final TextView textViewTitle = (TextView) itemVeiw.findViewById(R.id.item_title);
            textViewTitle.setText(currentTask.getTaskTitle());

            TextView textViewDescription = (TextView) itemVeiw.findViewById(R.id.item_description);
            textViewDescription.setText(currentTask.getTaskDescription());

            TextView textViewDate = (TextView) itemVeiw.findViewById(R.id.item_dateReminder);
            textViewDate.setText(currentTask.getTaskDateReminder() + " " + currentTask.getTaskHourReminder());

            CheckBox checkBoxVeiw = (CheckBox) itemVeiw.findViewById(R.id.isDone);

            if (currentTask.getTaskIsDone() == 0) {
                checkBoxVeiw.setChecked(false);
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            } else{
                checkBoxVeiw.setChecked(true);
                textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            itemVeiw.findViewById(R.id.isDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    currentTask.setTaskIsDone((checkBox.isChecked()) ? 1 : 0);
                    if (checkBox.isChecked()) {
                        myDb.updateRow(currentTask.getId(), currentTask.getTaskTitle(), currentTask.getTaskDescription(),
                                currentTask.getTaskDateReminder(), currentTask.getTaskHourReminder(), currentTask.getTaskIsDone());
                        textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        myDb.updateRow(currentTask.getId(), currentTask.getTaskTitle(), currentTask.getTaskDescription(),
                                currentTask.getTaskDateReminder(), currentTask.getTaskHourReminder(), currentTask.getTaskIsDone());
                        textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });

            return itemVeiw;
        }
    }

    private void registerListClickCallBack() {
        ListView myList = (ListView) findViewById(listViewInfo);
        final Intent intent = new Intent(this, DisplayTaskAcitvity.class);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long idFromDB) {

                Task editTask = (Task) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "entering edit task", Toast.LENGTH_LONG).show();
                intent.putExtra("task", editTask);
                startActivityForResult(intent, EDIT_TASK);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}
