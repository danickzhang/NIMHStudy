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
    private final SQLiteDatabase db;

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

        long result = insertLocation(latitude, longitude, accuracy, provider, type);

        if(result == -1) {
            return false;
        }

        result = insertEvent(LocationBroadcast.ID, time, "?", 0, null, null, null, null, result, null);

        return result != -1;
    }


    // ******************** Functions which insert only their parameters into the database **********************

    public boolean insertSurvey(String surveyId, String name) {
        ContentValues values = new ContentValues();

        values.put("surveyID", surveyId);
        values.put("name",     name);

        boolean success = false;

        try {
            db.beginTransaction();

            success = db.insert("survey", null, values) != -1;

            if (success) {
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }

        return success;

    }

    public boolean insertQuestionOnSurvey(String surveyId, String questionId) {
        ContentValues values = new ContentValues();

        values.put("surveyID",   surveyId);
        values.put("questionID", questionId);

        boolean success = false;

        try {

            db.beginTransaction();

            success = db.insert("questionOnSurvey", null, values) != -1;

            if(success) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return success;
    }

    /**
     * Inserts location data into the location table.
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
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("unknown1",  accuracy);
        values.put("unknown2",  provider);
        values.put("type",      type);

        long result = -1;

        try {
            db.beginTransaction();

            result = db.insert("locationData", null, values);

            if (result != -1) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return result;
    }


    public long insertEvent(String userId, Date timestamp, String type, int studyDay, Date scheduledTS, Date startTS, Date endTS, Long surveySubmissionId, Long locationDataId, Long hardwareInfoId) {

        ContentValues values = new ContentValues();

        long result = -1;

        try {
            db.beginTransaction();

            values.put("userId",             userId);
            values.put("timestamp",          timestamp.toString());
            values.put("type",               type);
            values.put("studyDay",           studyDay);
            values.put("scheduledTS",        scheduledTS.toString());
            values.put("startTS",            startTS.toString());
            values.put("endTS",              endTS.toString());
            values.put("surveySubmissionId", surveySubmissionId);
            values.put("locationDataId",     locationDataId);
            values.put("hardwareInfo",       hardwareInfoId);

            result = db.insert("event", null, values);

            if (result != -1) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return result;
    }

    /**
     * Inserts a row into the hardwareInfo table
     * @param hardwareInfoID auto-incrementing primary key for this table
     * @param message the text of the hardware information
     * @return -1 if the insertion failed, or a row ID otherwise
     */
    public long insertHardwareInfo(Integer hardwareInfoID, String message){

        // The values that will be inserted in the new row
        ContentValues values = new ContentValues();


        values.put("hardwareInfoID", hardwareInfoID);
        values.put("message", message);


        // The result of the row insertion
        long result;

        try {

            // Attempt to insert the row into "event," and store the result.
           result = db.insert("hardwareInfo", null, values);

           // If the result returns -1, it failed. If it returns anything else, it was successful.
           if(result != -1) {
               db.setTransactionSuccessful();
           }

        } finally {
           db.endTransaction();
        }

        return result;
    }

    /**
     * Inserts a row into the submissionAnswer table
     * @param submissionAnswerID the auto-incrementing primary key of this table
     * @param surveySubmissionID the unofficial foreign key from the surveySubmission table
     * @param questionID the question ID
     * @param answer the answer
     * @return -1 if the insertion failed, or a row ID otherwise
     */
    public long insertSubmissionAnswer(Integer submissionAnswerID, Integer surveySubmissionID, String questionID, Integer answer){

        // The values that will be inserted in the new row
        ContentValues values = new ContentValues();

        values.put("submissionAnswerID", submissionAnswerID);
        values.put("surveySubmissionID", surveySubmissionID);
        values.put("questionID", questionID);
        values.put("answer", answer);

        // The result of the row insertion
        long result;

        try {

            // Attempt to insert the row into "event," and store the result.
            result = db.insert("submissionAnswer", null, values);

            // If the result returns -1, it failed. If it returns anything else, it was successful.
            if(result != -1) {
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }

        return result;

    }

    /**
     * Inserts a row into the surveySubmission table
     * @param surveySubmissionID the survey submission ID
     * @param surveyID the ID of the survey
     * @return -1 if the insertion failed, or a row ID otherwise
     */
    public long insertSurveySubmission(Integer surveySubmissionID, String surveyID){

        // The values that will be inserted in the new row
        ContentValues values = new ContentValues();

        values.put("surveySubmissionID", surveySubmissionID);
        values.put("surveyID", surveyID);

        // The result of the row insertion
        long result;

        try {

            // Attempt to insert the row into "event," and store the result.
            result = db.insert("surveySubmission", null, values);

            // If the result returns -1, it failed. If it returns anything else, it was successful.
            if(result != -1) {
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }
        
        return result;
    }

    // *************************************** Data to JSON ********************************


    //               Functions which load data and return them as JSON will be here.


   // **************************************** Data to JSON ********************************

}
