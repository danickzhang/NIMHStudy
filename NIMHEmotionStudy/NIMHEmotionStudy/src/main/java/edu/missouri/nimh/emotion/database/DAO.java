package edu.missouri.nimh.emotion.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import edu.missouri.nimh.emotion.location.LocationBroadcast;

import static edu.missouri.nimh.emotion.database.DatabaseHelper.EVENT_TABLE;
import static edu.missouri.nimh.emotion.database.DatabaseHelper.HARDWARE_INFO_TABLE;
import static edu.missouri.nimh.emotion.database.DatabaseHelper.LOCATION_DATA_TABLE;
import static edu.missouri.nimh.emotion.database.DatabaseHelper.QUESTION_ON_SURVEY_TABLE;
import static edu.missouri.nimh.emotion.database.DatabaseHelper.QUESTION_TABLE;
import static edu.missouri.nimh.emotion.database.DatabaseHelper.SUBMISSION_ANSWER_TABLE;
import static edu.missouri.nimh.emotion.database.DatabaseHelper.SURVEY_SUBMISSION_TABLE;
import static edu.missouri.nimh.emotion.database.DatabaseHelper.SURVEY_TABLE;

/**
 * @author Andrew Smith
 * @author Jay Kelner
 *
 * Contains functions to insert into the database and to retrieve database information in JSON.
 */
public class DAO {
    private final SQLiteDatabase db;

    /**
     *
     * @param context
     */
    public DAO(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        db = helper.getWritableDatabase();
    }

    // *************************** Functions which emulate existing CSV functions ****************
    protected void writeSurveyToDatabase(String survey, HashMap<String, List<String>> surveyData) {

        /*
        hashmap of
            survey question name ->
                question -> answer
         */

        /*
        create event
        create a surveySubmission
        for each entry in the hashmap
            create a submission answer record
            The answer is the result of concatenating every value in the answer list
         */

    }




    /**
     * Writes the current location of the phone to the database.
     *
     * @param  location the location of the phone
     * @param  type the means by which the location was obtained (GPS, WiFi?)
     * @return true if the write succeeded, false otherwise
     */
    public boolean writeLocationToDatabase(Location location, String type) {

        Date time = Calendar.getInstance().getTime();

        double latitude  = location.getLatitude();
        double longitude = location.getLongitude();
        float  accuracy  = location.getAccuracy();
        String provider  = location.getProvider();

        long result = insertLocationData(latitude, longitude, accuracy, provider, type);

        if(result == -1) {
            return false;
        }

        result = insertEvent(LocationBroadcast.ID, time, "?", 0, null, null, null, null, result, null);

        return result != -1;
    }


    // ******************** Functions which insert only their parameters into the database *********

    /**
     * Inserts a row into the survey table.
     *
     * @param surveyId the ID of the survey
     * @param name     the name of the survey
     * @return         true if the insertion succeeded, or false otherwise.
     */
    public boolean insertSurvey(@NonNull String surveyId, @NonNull String name) {
        ContentValues values = new ContentValues();

        values.put("surveyID", surveyId);
        values.put("name",     name);

        boolean success = false;

        try {
            db.beginTransaction();

            success = db.insert(SURVEY_TABLE, null, values) != -1;

            if (success) {
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }

        return success;

    }

    /**
     * Inserts a row into the questionOnSurvey table.
     *
     * @param surveyId   the survey ID
     * @param questionId the question ID
     * @return           true if the insertion succeeded, or false otherwise
     */
    public boolean insertQuestionOnSurvey(@NonNull String surveyId, @NonNull String questionId) {
        ContentValues values = new ContentValues();

        values.put("surveyID",   surveyId);
        values.put("questionID", questionId);

        boolean success = false;

        try {

            db.beginTransaction();

            success = db.insert(QUESTION_ON_SURVEY_TABLE, null, values) != -1;

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
     * @return           Returns a row id on success or -1 on failure
     */
    public long insertLocationData(double latitude, double longitude, float accuracy, @NonNull String provider, @NonNull String type) {
        ContentValues values = new ContentValues();

        values.put("latitude",  latitude);
        values.put("longitude", longitude);
        values.put("accuracy",  accuracy);
        values.put("provider",  provider);
        values.put("type",      type);

        long result = -1;

        try {
            db.beginTransaction();

            result = db.insert(LOCATION_DATA_TABLE, null, values);

            if (result != -1) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return result;
    }


    /**
     * Inserts a new record into the event table.
     *
     * @param userId             The id of the user
     * @param timestamp          The time the event occurred
     * @param type               The type of the event
     * @param studyDay           The day since the study started
     * @param scheduledTS        The scheduled time (for a survey to be started)
     * @param startTS            The actual time the user started (for a survey)
     * @param endTS              The actual time the user finished (for a survey)
     * @param surveySubmissionId The id of the surveySubmission record (for a survey submission event)
     * @param locationDataId     The id of the locationData record (for a location report event)
     * @param hardwareInfoId     The id of the hardwareInfo record (for a hardware setting change event)
     * @return                   The id of the new event
     */
    public long insertEvent(
            @NonNull  String userId,
            @NonNull  Date   timestamp,
            @Nullable String type,
                      int    studyDay,
            @Nullable Date   scheduledTS,
            @Nullable Date   startTS,
            @Nullable Date   endTS,
            @Nullable String surveySubmissionId,
            @Nullable Long   locationDataId,
            @Nullable Long   hardwareInfoId) {

        ContentValues values = new ContentValues();

        long result = -1;

        try {
            db.beginTransaction();

            values.put("userID",             userId);
            values.put("timestamp", timestamp.toString());
            values.put("type",               type);
            values.put("studyDay",           studyDay);
            values.put("scheduledTS",        scheduledTS.toString());
            values.put("startTS",            startTS.toString());
            values.put("endTS",              endTS.toString());
            values.put("surveySubmissionID", surveySubmissionId);
            values.put("locationDataID",     locationDataId);
            values.put("hardwareInfoID",       hardwareInfoId);
            values.put("isSynchronized",     false);

            result = db.insert(EVENT_TABLE, null, values);

            if (result != -1) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }

        return result;
    }

    /**
     * Inserts a row into the hardwareInfo table.
     *
     * @param  message the text of the hardware information
     * @return -1 if the insertion failed, or a row ID otherwise
     */
    public long insertHardwareInfo(@NonNull String message){

        // The values that will be inserted in the new row
        ContentValues values = new ContentValues();

        values.put("message", message);

        // The result of the row insertion
        long result;

        try {
            db.beginTransaction();

            // Attempt to insert the row into "event," and store the result.
           result = db.insert(HARDWARE_INFO_TABLE, null, values);

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
     * Inserts a row into the submissionAnswer table.
     *
     * @param  surveySubmissionID the unofficial foreign key from the surveySubmission table
     * @param  questionID the question ID
     * @param  answer the answer
     * @return -1 if the insertion failed, or a row ID otherwise
     */
    public long insertSubmissionAnswer(@NonNull String surveySubmissionID, @NonNull String questionID, @NonNull Integer answer){
        // The values that will be inserted in the new row
        ContentValues values = new ContentValues();

        values.put("surveySubmissionID", surveySubmissionID);
        values.put("questionID",         questionID);
        values.put("answer",             answer);

        // The result of the row insertion
        long result;

        try {
            db.beginTransaction();
            
            // Attempt to insert the row into "event," and store the result.
            result = db.insert(SUBMISSION_ANSWER_TABLE, null, values);

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
     * Inserts a row into the surveySubmission table.
     *
     * @param  surveyID the ID of the survey
     * @return null if the insertion failed, or a row ID otherwise
     */
    public String insertSurveySubmission(@NonNull String surveyID){

        // The values that will be inserted in the new row
        ContentValues values = new ContentValues();
        String uuid = UUID.randomUUID().toString();

        values.put("surveySubmissionID", uuid);
        values.put("surveyID",           surveyID);

        // The result of the row insertion
        long result;

        try {
            db.beginTransaction();
            
            // Attempt to insert the row into "event," and store the result.
            result = db.insert(SURVEY_SUBMISSION_TABLE, null, values);

            // If the result returns -1, it failed. If it returns anything else, it was successful.
            if(result != -1) {
                db.setTransactionSuccessful();
            } else {
                return null;
            }

        } finally {
            db.endTransaction();
        }
        
        return uuid;
    }

    // *************************************** Data to JSON ********************************
    //               Functions which load data and return them as JSON will be here.
    // *************************************************************************************

    /**
     * Retrieves a row from the hardwareInfo table as JSON
     * @param hardwareInfoID the hardwareInfoID by which the row is retrieved
     * @return the JSONObject containing the row that has been retrieved
     * @throws JSONException
     */
    public JSONObject getHardwareInfo(long hardwareInfoID) throws JSONException {

        Cursor cursor;

        String[] columns   = { "message" };
        String[] arguments = { Long.toString(hardwareInfoID) };


        cursor = db.query(HARDWARE_INFO_TABLE, columns, "hardwareInfoID = ?", arguments, null, null, null);

        assert cursor.getCount() > 0;

        cursor.moveToFirst();
        String messageText = cursor.getString(0);

        cursor.close();

        JSONObject hardwareInfo = new JSONObject();

        // Don't include this line because hardwareInfoID auto-increments on the server
//        hardwareInfo.put("hardwareInfoID", hardwareInfoId);
        hardwareInfo.put("message", messageText);


        return  hardwareInfo;

    }

    /**
     * Retrieves a question from the question table
     * @param questionID the questionID of the question to be retrieved
     * @return the JSONObject of the question retrieved
     * @throws JSONException
     */
    public JSONObject getQuestion(@NonNull String questionID) throws JSONException {

        Cursor cursor;

        String[] columns   = { "questionID", "text" };
        String[] arguments = { questionID };


        cursor = db.query(QUESTION_TABLE, columns, "questionID = ?", arguments, null, null, null);

        assert cursor.getCount() > 0;

        cursor.moveToFirst();

        String questionId = cursor.getString(0);
        String text = cursor.getString(1);

        cursor.close();

        JSONObject question = new JSONObject();

        question.put("questionID", questionId);
        question.put("text", text);

        return  question;

    }

    /**
     * Retrieves a row from the survey table in the database
     * @param surveyID the ID of the survey to retrieve
     * @return the JSONObject of the retrieved survey
     * @throws JSONException
     */
    public JSONObject getSurvey(@NonNull String surveyID) throws JSONException {

        Cursor cursor;

        String[] columns   = { "surveyID", "name" };
        String[] arguments = { surveyID };


        cursor = db.query(SURVEY_TABLE, columns, "surveyID = ?", arguments, null, null, null);

        assert cursor.getCount() > 0;

        cursor.moveToFirst();

        String surveyId = cursor.getString(0);
        String name = cursor.getString(1);

        cursor.close();

        JSONObject survey = new JSONObject();

        survey.put("surveyID", surveyId);
        survey.put("name", name);

        return  survey;

    }

    /**
     * Loads a locationData row by id returning it in JSON.
     *
     * @param  locationDataID The id of the LocationData object to retrieve
     * @return A JSON object representing a row of the LocationData table
     * @throws JSONException
     */
    public JSONObject getLocationData(long locationDataID) throws JSONException {

        Cursor cursor;

        String[] columns = { "latitude", "longitude", "accuracy", "provider", "type"};
        String[] arguments = {Long.toString(locationDataID)};

        cursor = db.query(LOCATION_DATA_TABLE, columns, "locationDataId = ?", arguments, null, null, null);

        assert cursor.getCount() == 1;

        cursor.moveToFirst();

        double latitude = cursor.getDouble(0);
        double longitude = cursor.getDouble(1);
        float accuracy = cursor.getFloat(2);
        String provider = cursor.getString(3);
        String type = cursor.getString(4);

        cursor.close();

        JSONObject locationData = new JSONObject();

        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);
        locationData.put("accuracy", accuracy);
        locationData.put("provider", provider);
        locationData.put("type", type);

        return locationData;

    }

    /**
     * Returns all of the unsynchronized events as a JSON array of JSON objects.
     *
     * @return JSON encoded events not yet synchronized.
     */
    public JSONArray getEventsToSync() {
        JSONArray events = new JSONArray();

        Integer UNSYNCHRONIZED = 0;

        Cursor cursor;

        String[] columns = {
                "userID",
                "timestamp",
                "type",
                "studyDay",
                "scheduledTS",
                "startTS",
                "endTS",
                "surveySubmissionID",
                "locationDataID",
                "hardwareInfoID",
                "isSynchronized"
        };

        String[] arguments = {UNSYNCHRONIZED.toString()};

        cursor = db.query(EVENT_TABLE, columns, "isSynchronized = ?", arguments, null, null, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            JSONObject event = new JSONObject();

            String userId             = cursor.getString(0);
            String timestamp          = cursor.getString(1);
            String type               = cursor.getString(2);
            int    studyDay           = cursor.getInt(3);
            String scheduledTS        = cursor.getString(4);
            String startTS            = cursor.getString(5);
            String endTS              = cursor.getString(6);
            int    locationDataId     = cursor.getInt(7);
            String surveySubmissionId = cursor.getString(8);
            int    hardwareInfoId     = cursor.getInt(9);

            try {
                event.put("userID",      userId);
                event.put("timestamp",   timestamp);

                if(!cursor.isNull(3)) {
                    event.put("type", type);
                }

                if(!cursor.isNull(4)) {
                    event.put("studyDay", studyDay);
                }

                if(!cursor.isNull(5)) {
                    event.put("scheduledTS", scheduledTS);
                }

                if(!cursor.isNull(6)) {
                    event.put("startTS", startTS);
                }

                if(!cursor.isNull(7)) {
                    event.put("endTS", endTS);
                }

                if (!cursor.isNull(8)) {
                    event.put("locationData", getLocationData(locationDataId));
                }

                if (!cursor.isNull(9)) {
                    event.put("surveySubmission", getSurveySubmission(surveySubmissionId));
                }

                if (!cursor.isNull(10)) {
                    event.put("hardwareInfo", getHardwareInfo(hardwareInfoId));
                }

                events.put(event);
            } catch(JSONException e) {
                Log.e("DAO", "JSONException converting event rows to JSON");
                e.printStackTrace();
            }
        }

        cursor.close();

        return events;
    }

    /**
     * Returns all of the data for a survey submission as a JSON object.
     *
     * @param surveySubmissionId The id of the submission
     * @return                   A JSON representation of the specified surveySubmission and its associated answers.
     * @throws JSONException
     */
    public JSONObject getSurveySubmission(@NonNull String surveySubmissionId) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        Cursor cursor;

        String[] columns   = { "surveyID", "surveySubmissionID"};
        String[] arguments = { surveySubmissionId };

        cursor = db.query(SURVEY_SUBMISSION_TABLE, columns, "surveySubmissionID = ?", arguments, null, null, null);
        cursor.moveToFirst();

        assert cursor.getCount() == 1;

        String surveyId = cursor.getString(0);
        String surveySubmissionID = cursor.getString(1);

        jsonObject.put("surveySubmissionID", surveySubmissionID);
        jsonObject.put("surveyID",         surveyId);
        jsonObject.put("submissionAnswer", getAnswersForSurveySubmission(surveySubmissionId));

        cursor.close();

        return  jsonObject;

    }

    /**
     * Returns all of the answers for a particular survey submission as a JSONObject.
     *
     * @param surveySubmissionId The id of the survey submission to get answers for
     * @return                   Answers for specified survey submission
     * @throws JSONException
     */
    public JSONObject getAnswersForSurveySubmission(@NonNull String surveySubmissionId) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        Cursor cursor;

        String[] columns   = { "questionID", "answer"};
        String[] arguments = { surveySubmissionId    };

        cursor = db.query(SUBMISSION_ANSWER_TABLE, columns, "surveySubmissionID = ?", arguments, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            String questionId = cursor.getString(0);
            int answer = cursor.getInt(1);

            jsonObject.put(questionId, answer);

            cursor.moveToNext();
        }

        return jsonObject;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
