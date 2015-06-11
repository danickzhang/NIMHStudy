package edu.missouri.nimh.emotion.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Andrew Smith
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME     = "";
    private static final String DB_LOCATION = "";

    private static final String CREATE_SCRIPT_Location = "";
    private static final String CREATE_SCRIPT          = "";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // load SQL script from assets/res
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete
        // call onCreate
    }

    /**
     * This method should write a single event into the database
     */
    private void writeEventToDb() {

    }

    /**
     * This method should write the phones current GPS information into the database
     */
    private void writeLocationEvent() {

    }

    /**
     * This method should write information about a change in hardware status to the database
     */
    private void writeHardwareInfo() {

    }

    /**
     * @return Returns a JSON array of all of the data which has not been sent to the server.
     */
    public JSONArray getDataToSync() { return null; }

    /**
     *
     * @param moodId
     * @return
     */
    public JSONObject getMood(int moodId)                                   { return null; }

    /**
     *
     * @param impulsivityId
     * @return
     */
    public JSONObject getMoodAndImpulsivity(int impulsivityId)              { return null; }

    /**
     *
     * @param impulsivityId
     * @return
     */
    public JSONObject getImpulsivity(int impulsivityId)                     { return null; }
    public JSONObject getStudyDay(int studyDay, int userId)                 { return null; }
    public JSONObject getDrugs(int drugsId)                                 { return null; }
    public JSONObject getSinceLastSurvey(int sinceLastSurveyId)             { return null; }
    public JSONObject getSettings(int settingsId)                           { return null; }
    public JSONObject getSituationAndSetting(int situationAndSettingId)     { return null; }
    public JSONObject getSituation(int situationId)                         { return null; }
    public JSONObject getMoodDisregulation(int moodDisregulationId)         { return null; }
    public JSONObject getWorseMood(int worseMoodId)                         { return null; }
    public JSONObject getBetterMood(int betterMood)                         { return null; }
    public JSONObject getLocationData(int locationId)                       { return null; }
    public JSONObject getLifeEventsExperiences(int lifeEventsExperiencesId) { return null; }
    public JSONObject getRomanticPartner(int romanticPartnerId)             { return null; }
    public JSONObject getEvent(int eventId)                                 { return null; }
}
