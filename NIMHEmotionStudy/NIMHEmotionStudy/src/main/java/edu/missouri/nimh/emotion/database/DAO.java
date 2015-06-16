package edu.missouri.nimh.emotion.database;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Andrew Smith
 *
 * Contains functions to insert into the database and to retrieve database information in JSON.
 */
public class DAO {
    private final DatabaseHelper helper;

    public DAO(Context context) {
        helper = new DatabaseHelper(context);
        helper.getWritableDatabase();
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

    private void writeReport() {

    }

    /**
     * @return Returns a JSON array of all of the data which has not been sent to the server.
     */
    public JSONArray getDataToSync() { return null; }

    public JSONObject getReportsToSync()            { return null;  }
    public JSONObject getLocationEventsToSync()     { return null;  }
    public JSONObject getHardwareInfoEventsToSync() { return null;  }

    public JSONObject getSubmissionsToSync() { return null; }

    // JSON format is {type: report_submission, submission:{ userId, timestamp, report, answers: [id:answer]}

    // JSON format is submission { userId, timestamp, report }
    public JSONObject getSubmission(int id) { return null; }


    // *************************************************** Data to JSON ********************************
    public JSONObject eventAsJSON() {

        return null;
    }





// *************************************************** Data to JSON ********************************

}
