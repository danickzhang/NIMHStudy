package edu.missouri.nimh.emotion.test;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import edu.missouri.nimh.emotion.database.DAO;
import edu.missouri.nimh.emotion.database.DatabaseHelper;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.setTolerance;

public class DAOTest extends ApplicationTestCase<Application> {
    private Context        context;
    private DAO            dao;
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

            Log.e("DAOTest", hardwareInfo.toString(4));

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

            Log.e("DAOTest", actual.toString(4));

            assertJsonEquals(expected.toString(), actual.toString());
        } catch(JSONException e) {
            Assert.fail();
        }
    }

    @SmallTest
    public void testGetSurvey() {
        dao.insertSurvey("SurveyID", "SurveyName");

        try{
            JSONObject expected = new JSONObject();

            expected.put("surveyID", "SurveyID");
            expected.put("name",     "SurveyName");

            JSONObject actual = dao.getSurvey("SurveyID");

            Log.e("DAOTest", actual.toString(4));
            assertJsonEquals(expected.toString(), actual.toString());
        } catch(JSONException e) {
            Assert.fail();
        }
    }

    @SmallTest
    public void testGetSurveySubmission() {
        String id = dao.insertSurveySubmission("SurveyID");

        dao.insertSubmissionAnswer(id, "Question1", "0");
        dao.insertSubmissionAnswer(id, "Question2", "1");

        try {
            JSONObject object = new JSONObject();


            object.put("Question1", 0);
            object.put("Question2", 1);

            JSONObject expected = new JSONObject();
            expected.put("surveySubmissionID", id);
            expected.put("surveyID",           "SurveyID");
            expected.put("submissionAnswer",   object);

            JSONObject actual = dao.getSurveySubmission(id);

            assertJsonEquals(expected.toString(), actual.toString());

            Log.e("DAOTest", actual.toString(4));

        } catch(JSONException e) {
            Assert.fail();
        }
    }

    // getQuestion
    // getEventsToSync

    @SmallTest
    public void testGetAnswersForSurveySubmission() {
        String uuid = dao.insertSurveySubmission("survey");
        dao.insertSubmissionAnswer(uuid, "Question1", "0");
        dao.insertSubmissionAnswer(uuid, "Question2", "1");
        dao.insertSubmissionAnswer(uuid, "Question3", "2");

        try {
            JSONObject object = new JSONObject();

            object.put("Question1", 0);
            object.put("Question2", 1);
            object.put("Question3", 2);

            JSONObject actual = dao.getAnswersForSurveySubmission(uuid);

            Log.e("DAOTest", actual.toString(4));

            assertJsonEquals(object.toString(), actual.toString());

        } catch(JSONException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    public void testGetQuestion() throws Exception {
        long result = dao.insertQuestion("question", "text");

        if (result != -1) {

            JSONObject expected = new JSONObject();

            expected.put("questionID", "question");
            expected.put("text", "text");

            JSONObject actual = dao.getQuestion("question");

            assertJsonEquals(expected, actual);

        } else {
            Assert.fail();
        }
    }
}