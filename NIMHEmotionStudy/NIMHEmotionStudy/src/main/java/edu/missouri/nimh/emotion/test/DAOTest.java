package edu.missouri.nimh.emotion.test;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONException;
import org.json.JSONObject;
import junit.framework.Assert;

import static net.javacrumbs.jsonunit.JsonAssert.*;

import java.lang.Exception;

import edu.missouri.nimh.emotion.database.DAO;
import edu.missouri.nimh.emotion.database.DatabaseHelper;

public class DAOTest extends ApplicationTestCase<Application> {
    Context context;
    DAO dao;
    private SQLiteDatabase db;

    public DAOTest() {
        super(Application.class);

        setTolerance(.001);
    }

    public void setUp() throws Exception {
        super.setUp();

        context = new RenamingDelegatingContext(getContext(), "test_");
        setContext(context);


        context.deleteDatabase(DatabaseHelper.DB_NAME);

       dao = new DAO(context);


    }



    public void tearDown() throws Exception {
    }

    @SmallTest
    public void testGetHardwareInfo() {

        long id = dao.insertHardwareInfo("testMessage");

        JSONObject expected = new JSONObject();


        try {
            expected.put("message", "testMessage");

            JSONObject hardwareInfo = dao.getHardwareInfo(id);

            Assert.assertEquals(expected.toString(), hardwareInfo.toString());
        } catch (JSONException e) {
            Assert.fail();
        }



    }

    @SmallTest
    public void testGetLocationData() {
        long id = dao.insertLocationData(10.5, -6.5, .3f, "provider", "type");

        try {
            JSONObject expected = new JSONObject();
            expected.put("latitude",   10.5);
            expected.put("longitude", -6.5);
            expected.put("accuracy",   0.3);
            expected.put("provider",   "provider");
            expected.put("type",       "type");

            JSONObject actual = dao.getLocationData(id);

            assertJsonEquals(expected.toString(), actual.toString());
        } catch(JSONException e) {
            Assert.fail();
        }
    }

    // getQuestion
    // getSurvey
    @SmallTest
    public void testGetSurvey() {
        dao.insertSurvey("SurveyID", "SurveyName");

        try{
            JSONObject expected = new JSONObject();

            expected.put("surveyID", "SurveyID");
            expected.put("name", "SurveyName");

            JSONObject actual = dao.getSurvey("SurveyID");

            assertJsonEquals(expected.toString(), actual.toString());
        } catch(JSONException e) {
            Assert.fail();
        }
    }
    // getSurveySubmission
    // getEventsToSync
    // getAnswersForSurveySubmission
}