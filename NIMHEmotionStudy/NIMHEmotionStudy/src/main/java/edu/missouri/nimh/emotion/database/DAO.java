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

import edu.missouri.nimh.emotion.BuildConfig;
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
    public static final String LOG_TAG = "DAO";
    private final SQLiteDatabase db;

    /**
     *
     * @param context A context
     */
    public DAO(Context context) {
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        db                    = helper.getWritableDatabase();
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
    public long insertLocationData(
            double   latitude,
            double   longitude,
            float    accuracy,
            @NonNull String provider,
            @NonNull String type
    ) {
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

            String scheduledTSValue = scheduledTS != null ? scheduledTS.toString() : null;
            String startTSValue     = startTS     != null ? startTS.toString()     : null;
            String endTSValue       = endTS       != null ? endTS.toString()       : null;

            values.put("userID",             userId);
            values.put("timestamp",          timestamp.toString());
            values.put("type",               type);
            values.put("studyDay",           studyDay);
            values.put("scheduledTS",        scheduledTSValue);
            values.put("startTS",            startTSValue);
            values.put("endTS",              endTSValue);
            values.put("surveySubmissionID", surveySubmissionId);
            values.put("locationDataID",     locationDataId);
            values.put("hardwareInfoID",     hardwareInfoId);
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
    @Nullable
    public String insertSurveySubmission(@NonNull String surveyID){

        // The values that will be inserted in the new row
        ContentValues values = new ContentValues();
        String uuid          = UUID.randomUUID().toString();

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
    @NonNull
    public JSONObject getHardwareInfo(long hardwareInfoID) throws JSONException {

        final int MESSAGE = 0;

        Cursor cursor;

        final String[] columns   = { "message" };
        final String[] arguments = { Long.toString(hardwareInfoID) };

        cursor = db.query(HARDWARE_INFO_TABLE, columns, "hardwareInfoID = ?", arguments, null, null, null);

        if(cursor.getCount() <= 0) {
            Log.e(LOG_TAG, String.format("HardwareInfo record with an ID of %s does not exist.", hardwareInfoID));

            if(BuildConfig.DEBUG) {
                throw new AssertionError("HardwareInfo requested does not exist");
            }
        }

        cursor.moveToFirst();

        String messageText = cursor.getString(MESSAGE);

        cursor.close();

        JSONObject hardwareInfo = new JSONObject();

        hardwareInfo.put("message", messageText);

        return  hardwareInfo;

    }

    /**
     * Retrieves a question from the question table
     * @param questionID the questionID of the question to be retrieved
     * @return the JSONObject of the question retrieved
     * @throws JSONException
     */
    @NonNull
    public JSONObject getQuestion(@NonNull String questionID) throws JSONException {

        final int QUESTION_ID = 0;
        final int TEXT        = 1;

        final String[] columns   = { "questionID", "text" };
        final String[] arguments = { questionID           };

        Cursor cursor = db.query(QUESTION_TABLE, columns, "questionID = ?", arguments, null, null, null);

        if(cursor.getCount() <= 0) {
            Log.e(LOG_TAG, String.format("Question with an ID of %s does not exist", questionID));

            if (BuildConfig.DEBUG) {
                throw new AssertionError("Question requested does not exist");
            }
        }

        cursor.moveToFirst();

        String questionId = cursor.getString(QUESTION_ID);
        String text       = cursor.getString(TEXT);

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
    @NonNull
    public JSONObject getSurvey(@NonNull String surveyID) throws JSONException {

        final int SURVEY_ID = 0;
        final int NAME      = 1;

        final String[] columns   = { "surveyID", "name" };
        final String[] arguments = { surveyID           };

        Cursor cursor = db.query(SURVEY_TABLE, columns, "surveyID = ?", arguments, null, null, null);

        if(cursor.getCount() <= 0) {
            Log.e(LOG_TAG, String.format("Survey with an ID of %s does not exist", surveyID));

            if (BuildConfig.DEBUG) {
                throw new AssertionError("Requested survey does not exist");

            }
        }

        cursor.moveToFirst();

        String surveyId = cursor.getString(SURVEY_ID);
        String name     = cursor.getString(NAME);

        cursor.close();

        JSONObject survey = new JSONObject();

        survey.put("surveyID", surveyId);
        survey.put("name",     name);

        return  survey;

    }

    /**
     * Loads a locationData row by id returning it in JSON.
     *
     * @param  locationDataID The id of the LocationData object to retrieve
     * @return A JSON object representing a row of the LocationData table
     * @throws JSONException
     */
    @NonNull
    public JSONObject getLocationData(long locationDataID) throws JSONException {
        final int LATITUDE  = 0;
        final int LONGITUDE = 1;
        final int ACCURACY  = 2;
        final int PROVIDER  = 3;
        final int TYPE      = 4;

        final String[] columns   = { "latitude", "longitude", "accuracy", "provider", "type"};
        final String[] arguments = { Long.toString(locationDataID)                          };

        Cursor cursor;

        cursor = db.query(LOCATION_DATA_TABLE, columns, "locationDataId = ?", arguments, null, null, null);

        if(cursor.getCount() <= 0) {
            Log.e(LOG_TAG, String.format("LocationData with an ID of %s does not exist", locationDataID));

            if (BuildConfig.DEBUG) {
                throw new AssertionError("Requested locationData row does not exist");
            }
        }

        cursor.moveToFirst();

        double latitude  = cursor.getDouble(LATITUDE);
        double longitude = cursor.getDouble(LONGITUDE);
        float accuracy   = cursor.getFloat(ACCURACY);
        String provider  = cursor.getString(PROVIDER);
        String type      = cursor.getString(TYPE);

        cursor.close();

        JSONObject locationData = new JSONObject();

        locationData.put("latitude",  latitude);
        locationData.put("longitude", longitude);
        locationData.put("accuracy",  accuracy);
        locationData.put("provider",  provider);
        locationData.put("type",      type);

        return locationData;
    }

    /**
     * Returns all of the unsynchronized events as a JSON array of JSON objects.
     *
     * @return JSON encoded events not yet synchronized.
     */
    @NonNull
    public JSONArray getEventsToSync() {

        final int USER_ID              = 0;
        final int TIMESTAMP            = 1;
        final int TYPE                 = 2;
        final int STUDY_DAY            = 3;
        final int SCHEDULED_TS         = 4;
        final int START_TS             = 5;
        final int END_TS               = 6;
        final int LOCATION_DATA_ID     = 7;
        final int SURVEY_SUBMISSION_ID = 8;
        final int HARDWARE_INFO_ID     = 9;

        final String[] columns = {
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

        final String   UNSYNCHRONIZED = Integer.toString(0);
        final String[] arguments      = { UNSYNCHRONIZED };

        JSONArray events = new JSONArray();

        Cursor cursor;

        cursor = db.query(EVENT_TABLE, columns, "isSynchronized = ?", arguments, null, null, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            JSONObject event = new JSONObject();

            String userId             = cursor.getString(USER_ID);
            String timestamp          = cursor.getString(TIMESTAMP);
            String type               = cursor.getString(TYPE);
            int    studyDay           = cursor.getInt(STUDY_DAY);
            String scheduledTS        = cursor.getString(SCHEDULED_TS);
            String startTS            = cursor.getString(START_TS);
            String endTS              = cursor.getString(END_TS);
            int    locationDataId     = cursor.getInt(LOCATION_DATA_ID);
            String surveySubmissionId = cursor.getString(SURVEY_SUBMISSION_ID);
            int    hardwareInfoId     = cursor.getInt(HARDWARE_INFO_ID);

            try {
                event.put("userID",      userId);
                event.put("timestamp",   timestamp);

                if (!cursor.isNull(TYPE))                   event.put("type",            type);
                if (!cursor.isNull(STUDY_DAY))              event.put("studyDay",        studyDay);
                if (!cursor.isNull(SCHEDULED_TS))           event.put("scheduledTS",     scheduledTS);
                if (!cursor.isNull(START_TS))               event.put("startTS",         startTS);
                if (!cursor.isNull(END_TS))                 event.put("endTS",           endTS);
                if (!cursor.isNull(LOCATION_DATA_ID))      event.put("locationData",     getLocationData(locationDataId));
                if (!cursor.isNull(SURVEY_SUBMISSION_ID))  event.put("surveySubmission", getSurveySubmission(surveySubmissionId));
                if (!cursor.isNull(HARDWARE_INFO_ID))      event.put("hardwareInfo",     getHardwareInfo(hardwareInfoId));

                events.put(event);
            } catch(JSONException e) {
                Log.e(LOG_TAG, "JSONException converting event rows to JSON");
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
    @NonNull
    public JSONObject getSurveySubmission(@NonNull String surveySubmissionId) throws JSONException {

        final int SURVEY_ID            = 0;
        final int SURVEY_SUBMISSION_ID = 1;

        final String[] columns   = { "surveyID", "surveySubmissionID"};
        final String[] arguments = { surveySubmissionId              };

        JSONObject jsonObject = new JSONObject();

        Cursor cursor = db.query(SURVEY_SUBMISSION_TABLE, columns, "surveySubmissionID = ?", arguments, null, null, null);

        cursor.moveToFirst();

        if(cursor.getCount() <= 0) {
            Log.e(LOG_TAG, String.format("SurveySubmission with an ID of %s does not exist", surveySubmissionId));

            if(BuildConfig.DEBUG) {
                throw new AssertionError("Requested surveySubmission does not exist");
            }
        }

        String surveyId           = cursor.getString(SURVEY_ID);
        String surveySubmissionID = cursor.getString(SURVEY_SUBMISSION_ID);

        jsonObject.put("surveySubmissionID", surveySubmissionID);
        jsonObject.put("surveyID",           surveyId);
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
    @NonNull
    public JSONObject getAnswersForSurveySubmission(@NonNull String surveySubmissionId) throws JSONException {

        final int QUESTION_ID = 0;
        final int ANSWER      = 1;

        final String[] columns   = { "questionID", "answer"};
        final String[] arguments = { surveySubmissionId    };

        JSONObject jsonObject = new JSONObject();

        Cursor cursor;

        cursor = db.query(SUBMISSION_ANSWER_TABLE, columns, "surveySubmissionID = ?", arguments, null, null, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            String questionId = cursor.getString(QUESTION_ID);
            int    answer     = cursor.getInt(ANSWER);

            jsonObject.put(questionId, answer);

            cursor.moveToNext();
        }

        cursor.close();

        return jsonObject;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
