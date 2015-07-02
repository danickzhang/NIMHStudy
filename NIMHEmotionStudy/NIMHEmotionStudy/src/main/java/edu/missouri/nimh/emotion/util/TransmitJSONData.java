package edu.missouri.nimh.emotion.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    public TransmitJSONData(URI uri, JSONMode mode) {
        this.uri       = uri;
        this.uriString = uri.toASCIIString();
        this.mode      = mode;
    }

    @Override
    protected Boolean doInBackground(JSONObject... objects) {
        String message = null;

        if(objects == null || objects.length == 0) {
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
            }
            else {
                message = new JSONArray(objects).toString(JSON_INDENTION);
            }
        } catch(JSONException e) {
            Log.e(TAG, JSON_EXCEPTION_MSG);
            e.printStackTrace();
            return false;
        }

        Log.d(TAG, String.format(LOG_THREAD_ID_MSG, Thread.currentThread().getId()));

        HttpPost request = new HttpPost(uri);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("json", message));

        try {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient().execute(request);

            int statusCode = response.getStatusLine().getStatusCode();

            switch (statusCode) {
                case HttpStatus.SC_OK:
                    String result = EntityUtils.toString(response.getEntity());
                    Log.d(TAG, result);
                    return true;
                default:
                    Log.w(TAG, String.format(POST_ERROR_MSG, uriString, statusCode));
                    return false;
            }
        } catch(UnsupportedEncodingException e) {
            Log.e(TAG, String.format(UNSUPPORTED_ENCODING_MSG, params.get(0)));
            e.printStackTrace();
            return false;
        }
        catch(ClientProtocolException e) {
            Log.e(TAG, String.format(CLIENT_PROTOCOL_EXCEPTION_MSG, uriString));
            e.printStackTrace();
            return false;
        }
        catch(IOException e) {
            Log.e(TAG, String.format(IO_ERROR_MSG, uriString));
            e.printStackTrace();
            return false;
        }
    }
}
