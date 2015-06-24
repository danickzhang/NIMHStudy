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

import java.lang.Exception;

import edu.missouri.nimh.emotion.database.DAO;
import edu.missouri.nimh.emotion.database.DatabaseHelper;

public class DAOTest extends ApplicationTestCase<Application> {
    Context context;
    DAO dao;
    private SQLiteDatabase db;

    public DAOTest() {
        super(Application.class);
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

            Assert.assertEquals("",expected.toString(), hardwareInfo.toString());
        } catch (JSONException e) {
            Assert.fail();
        }

        Assert.assertEquals(true, true);
    }

    public void insertHardwareInfoTestData() {

    }
}