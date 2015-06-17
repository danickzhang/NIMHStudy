package edu.missouri.nimh.emotion.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.missouri.nimh.emotion.location.LocationBroadcast;

/**
 * @author Andrew Smith
 *
 * Contains functions to insert into the database and to retrieve database information in JSON.
 */
public class DAO {
    private final DatabaseHelper helper;
    private SQLiteDatabase db;

    public DAO(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
    }

    // *************************** Functions which emulate existing CSV functions ****************
    protected void writeSurveyToDatabase(String survey, HashMap<String, List<String>> surveyData) {

    }

    public boolean writeLocationToDatabase(Location location, String type) {

        Date time = Calendar.getInstance().getTime();

        double latitude  = location.getLatitude();
        double longitude = location.getLongitude();
        float  accuracy  = location.getAccuracy();
        String provider  = location.getProvider();

        db.beginTransaction();

        long result = insertLocation(latitude,longitude,accuracy,provider, type);

        if(result == -1) {
            return false;
        }

        result = insertEvent(LocationBroadcast.ID, time, "?", 0, null, null, null, null, result, null);

        if(result != -1) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();
        return result != -1;
    }


    // ******************** Functions which insert only their parameters into the database **********************


    /**
     * Inserts locoation data into the location table.
     *
     * @param latitude   The latitude of the location
     * @param longitude  The longitude of the location
     * @param accuracy   The accuracy of the measurement
     * @param provider   The source of the location information
     * @param type       The users activity type
     * @return returns a row id on success or -1 on failure
     */
    public long insertLocation(double latitude, double longitude, float accuracy, String provider, String type) {
        ContentValues values = new ContentValues();
        values.put("lattitude", latitude);
        values.put("longitude", longitude);
        values.put("unknown1",  accuracy);
        values.put("unknown2", provider);
        values.put("type",      type);

        db.beginTransaction();

        long result = db.insert("locationData", null, values);

        if(result!= -1) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();

        return result;
    }


    public long insertEvent(String userId, Date timestamp, String type, int studyDay, Date scheduledTS, Date startTS, Date endTS, Long surveySubmissionId, Long locationDataId, Long hardwareInfoId) {

        ContentValues values = new ContentValues();

        db.beginTransaction();

        values.put("userId", userId);
        values.put("timestamp", timestamp.toString());
        values.put("type", type);
        values.put("studyDay", studyDay);
        values.put("scheduledTS", scheduledTS.toString());
        values.put("startTS", startTS.toString());
        values.put("endTS", endTS.toString());
        values.put("surveySubmissionId", surveySubmissionId);
        values.put("locationDataId", locationDataId);
        values.put("hardwareInfo", hardwareInfoId);

        long result = db.insert("event", null, values);

        if(result != -1) {
            db.setTransactionSuccessful();
        }

        db.endTransaction();

        return result;
    }

    // *************************************** Data to JSON ********************************


    //               Functions which load data and return them as JSON will be here.


   // **************************************** Data to JSON ********************************

}
