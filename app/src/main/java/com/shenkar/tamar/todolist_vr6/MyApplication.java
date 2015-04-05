package com.shenkar.tamar.todolist_vr6;
/**
 * Created by tamar & haim on 3/22/15.
 tamar zanzuri : 200212777;
 haim yaakov : 204729107;
 */

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class MyApplication extends Application {

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-61189361-1";

    //Logging TAG
    private static final String TAG = "MyApp";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.

    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public MyApplication() {
        super();
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            Tracker t = null;
            if(trackerId == TrackerName.APP_TRACKER)
            {
                t = analytics.newTracker(R.xml.app_tracker);
            }
            else if(trackerId == TrackerName.GLOBAL_TRACKER)
            {
                t = analytics.newTracker(PROPERTY_ID);
            }

            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
