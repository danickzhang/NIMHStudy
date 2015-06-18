package edu.missouri.nimh.emotion.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * @author Andrew Smith
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    private static final String DB_NAME     = "db.db";
    private static final String DB_LOCATION = "";
    private static final int    DB_VERSION  = 1;

    private static final String LOCATION_DATA_SQL =
            "CREATE TABLE `locationData` (" +
            "    `locationDataId` INTEGER PRIMARY KEY NOT NULL," +
            "    `latitude`  DOUBLE NULL,"                  +
            "    `longitude` DOUBLE NULL,"                  +
            "    `accuracy`  Float NULL,"                  +
            "    `provider`  VARCHAR(45) NULL,"                  +
            "    `type`      VARCHAR(45) NULL"                   +
            ");";

    private static final String HARDWARE_INFO_SQL =
            "CREATE TABLE `hardwareInfo` ("                      +
            "    `hardwareInfoID` INTEGER PRIMARY KEY NOT NULL," +
            "    `message` TEXT NOT NULL"                        +
            ");";

    private static final String SURVEY_SQL =
            "CREATE TABLE `survey` ("              +
            "    `surveyID` VARCHAR(45) NOT NULL," +
            "    `name`     VARCHAR(45) NOT NULL," +
            "    PRIMARY KEY (`surveyID`)"         +
            ");";

    private static final String QUESTION_SQL =
            "CREATE TABLE `question` ("          +
            "    `questionID` VARCHAR(45) NULL," +
            "    `text`       TEXT NOT NULL"     +
            ");";

    private static final String QUESTION_SURVEY =
            "CREATE TABLE `questionOnSurvey` ("          +
            "    `surveyID`   INT NOT NULL,"             +
            "    `questionID` VARCHAR(45) NOT NULL,"     +
            "    PRIMARY KEY (`surveyID`, `questionID`)" +
            ");";

    private static final String SurveySubmission =
            "CREATE TABLE `surveySubmission` ("                      +
            "    `surveySubmissionID` INTEGER PRIMARY KEY NOT NULL," +
            "    `surveyID` VARCHAR(45) NOT NULL"                    +
             ");";

    private static final String SUBMISSION_ANSWER =
            "CREATE TABLE `submissionAnswer` (" +
            "        `submissionAnswerID` INTEGER PRIMARY KEY NOT NULL," +
            "        `surveySubmissionID` INT NOT NULL,"                 +
            "        `questionID`         VARCHAR(45) NOT NULL,"         +
            "        `answer`             INT NOT NULL"                  +
            ");";

   private static final String EVENT_SQL =
            " CREATE TABLE `event` ("                         +
            "    `eventID` VARCHAR(45) NOT NULL,"             +
            "    `userID`             VARCHAR(8)  NOT NULL,"  +
            "    `timestamp`          TIMESTAMP   NOT NULL,"  +
            "    `type`               VARCHAR(45) NULL,"      +
            "    `studyDay`           DATE        NULL,"      +
            "    `scheduledTS`        TIMESTAMP   NULL,"      +
            "    `startTS`            TIMESTAMP   NULL,"      +
            "    `endTS`              TIMESTAMP   NULL,"      +
            "    `surveySubmissionID` VARCHAR(45) NULL,"      +
            "    `locationDataID`     INT         NULL,"      +
            "    `hardwareInfoID`     INT         NULL,"      +
            "    PRIMARY KEY (`eventID`)" +
            ");";

    private static String[] TABLES_SQL = {
            LOCATION_DATA_SQL,
            HARDWARE_INFO_SQL,
            SURVEY_SQL,
            QUESTION_SQL,
            QUESTION_SURVEY,
            SurveySubmission,
            SUBMISSION_ANSWER,
            EVENT_SQL
    };

    private static String[] TABLE_NAMES = {
            "locationData",
            "hardwareInfo",
            "survey",
            "question",
            "questionOnSurvey",
            "surveySubmission",
            "submissionAnswer",
            "event"
    };

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if(databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }

        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            Log.d("DB", "Creating database");
            for (String createStatement : TABLES_SQL) {
                db.execSQL(createStatement);
            }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("DB", "Database is being upgraded");
        Log.d("DB", "Deleting tables");


        for(String table : TABLE_NAMES) {
            db.execSQL(String.format("DROP TABLE %s;", table));
        }

        Log.d("DB", "Creating tables");

        onCreate(db);
    }
}
