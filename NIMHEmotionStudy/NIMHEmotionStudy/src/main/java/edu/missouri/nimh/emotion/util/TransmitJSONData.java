package edu.missouri.nimh.emotion.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import edu.missouri.nimh.emotion.database.DAO;

/**
 * Sends JSON to a URI using HTTP POST.
 *
 * @author Andrew Smith
 */
public class TransmitJSONData extends AsyncTask<JSONObject, Void, Boolean> {

    private static final int    JSON_INDENTION = 4;

    // *************************** Format Strings *************************************************
    private static final String POST_ERROR_MSG                = "POST to %s returned code %s ";
    private static final String UNSUPPORTED_ENCODING_MSG      = "Unable to encode parameter %s";
    private static final String CLIENT_PROTOCOL_EXCEPTION_MSG = "Client protocol writing JSON string to %s";
    private static final String IO_ERROR_MSG                  = "I/O error writing JSON string to %s";
    private static final String TAG                           = "JSON";
    private static final String JSON_EXCEPTION_MSG            = "Failed to generate JSON";

    // ************************** Log Messages ****************************************************
    private static final String LOG_THREAD_ID_MSG = "TransmitJSONData thread id: %s";
    private static final String LOG_NO_DATA_MSG   = "No JSON data to transmit";

    private final URI      uri;
    private final String   uriString;
    private final JSONMode mode;
    private final DAO      db;


    /**
     * Controls how to represent objects as JSON when there is only one element
     *
     * ALWAYS_OUTPUT_ARRAY will result in a JSON array containing a single JSON object
     * OUTPUT_ARRAY_OR_OBJECT will result in a single JSON array
     */
    public enum JSONMode {
        ALWAYS_OUTPUT_ARRAY,
        OUTPUT_ARRAY_OR_OBJECT
    }


    /**
     * @param uri  The uri to POST the JSON to.
     * @param mode The way to represent a single object as JSON
     */
    public TransmitJSONData(URI uri, JSONMode mode, DAO db) {
        this.uri       = uri;
        this.uriString = uri.toASCIIString();
        this.mode      = mode;
        this.db        = db;
    }

    @Override
    protected Boolean doInBackground(JSONObject... objects) {
        String message = null;
        boolean status200 = false;

        if (objects == null || objects.length == 0) {
            Log.e(TAG, LOG_NO_DATA_MSG);
            return false;
        }

        try {

            if (objects.length == 1) {
                switch (mode) {
                    case OUTPUT_ARRAY_OR_OBJECT:
                        message = objects[0].toString(JSON_INDENTION);
                        break;
                    case ALWAYS_OUTPUT_ARRAY:
                        message = new JSONArray(objects).toString(JSON_INDENTION);
                        break;
                }
            } else {
                message = new JSONArray(objects).toString(JSON_INDENTION);
            }
        } catch (JSONException e) {
            Log.e(TAG, JSON_EXCEPTION_MSG);
            e.printStackTrace();
            return false;
        }

        Log.d(TAG, String.format(LOG_THREAD_ID_MSG, Thread.currentThread().getId()));

        HttpPost request = new HttpPost(uri);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("json", message));

        try {
            request.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = new DefaultHttpClient().execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            Log.w("html", responseString);

            // This is returning an incorrect status code. I don't understand.
            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case HttpStatus.SC_OK:
//                    String result = EntityUtils.toString(response.getEntity());
                    String result = "Nominal request status code received.";
                    Log.d(TAG, result + ", about to mark events as processed.");
                    JSONArray jsonArray = new JSONArray();
                    for (JSONObject jo : objects) {
                        jsonArray.put(jo);
                    }
                    db.markEventsAsProcessed(jsonArray);
                    return true;
//                    Log.d(TAG, "The parameters sent to the server are as follows: " + params.toString());
                case HttpStatus.SC_BAD_REQUEST:
                    String results = "BAD REQUEST ENCOUNTERED";
                    status200 = true;
                    Log.d(TAG, results);
//                    Log.d(TAG, "Params sent: " + params.toString());
                    break;
                default:
                    Log.w(TAG, String.format(POST_ERROR_MSG, uriString, statusCode));
//                    Log.w(TAG, "Params sent to server are lookie like: "+ params.toString());
                    return false;
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, String.format(UNSUPPORTED_ENCODING_MSG, params.get(0)));
            e.printStackTrace();
            return false;
        } catch (ClientProtocolException e) {
            Log.e(TAG, String.format(CLIENT_PROTOCOL_EXCEPTION_MSG, uriString));
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e(TAG, String.format(IO_ERROR_MSG, uriString));
            e.printStackTrace();
            return false;
        }

        // If the server returned a status 200 code, mark the events received as processed.
//        if (status200) {
//
//        }

        return status200;
    }
}
