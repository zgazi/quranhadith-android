package com.greenledge.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.os.Environment;


public class DatabaseHelper extends SQLiteOpenHelper {

	public interface DatabaseHelperInterface {
		public void onRequestCompleted();
	}

	private DatabaseHelperInterface mCallback;

	private static DatabaseHelper instance;
	
	private static String DB_PATH = "";
	private static String DB_NAME = "quran.db";
	private static final int DB_VERSION = 1;
	private SQLiteDatabase myDatabase;
	private final Context myContext;
	private int searchTopResult = 30;
	
	String myPath;

	public DatabaseHelper() {
		this(null, Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.greenledge/quran/" + DB_NAME, null, DB_VERSION);
	}
	
	public DatabaseHelper(Context context) {
		this(context, Environment.getExternalStorageDirectory()
			    + File.separator + DB_NAME, null, DB_VERSION);

	}

	public DatabaseHelper(Context context, String name, CursorFactory factory, int version)
	{
		super(context, name, factory, version);
		DB_NAME = name;
		this.myContext = context;
	}
	
	public DatabaseHelper(Context context, DatabaseHelperInterface callback) {

		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
		mCallback = callback;
		myPath = myContext.getFilesDir().getAbsolutePath()
				.replace("files", "databases")
				+ File.separator + DB_NAME;
	}

    public static DatabaseHelper getInstance(Context context){
        if(instance==null){
            instance=new DatabaseHelper(context);
        }
        return instance;
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			copyDatabase(db.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if (oldVersion == 1 && newVersion >= 2) {
			// execute upgrade queries
			oldVersion = 2;
		}
		if (oldVersion == 2 && newVersion >= 3) {
			// execute database upgrade queries
			oldVersion = 3;
		}
		
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			copyDatabase(db.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	public void initDatabase(String fullPath)// Call when the application Run
	{
		try {

			boolean result = createDatabase(fullPath);
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {

			openDatabase(fullPath);

		} catch (SQLException sqle) {

			throw sqle;
		}
		if (mCallback != null)
			mCallback.onRequestCompleted();
	}
   
	public boolean createDatabase(String fullPath) throws IOException {
		myDatabase = null;
		if (fullPath != null && fullPath != "") myPath = fullPath;
		boolean dbExist = checkDatabase(myPath);// check if we DB SQLlite Exist or not

		if (dbExist) {
			return false;
		} else {
			try {
				myDatabase = this.getReadableDatabase();
				myDatabase.close();
				copyDatabase(myPath);// //Copy the External DB to the application

				return true;
			} catch (IOException e) {
				return false;

			}
		}

	}

	public boolean checkDatabase(String fullPath) {
		if (fullPath != null && fullPath != "") myPath = fullPath;
		try {
			File dbFile = new File(myPath);
			return dbFile.exists();
		} catch (SQLiteException e) {
		}
		return false;
	}

	public boolean deleteDatabase(String fullPath)// Delere on Upgrade
	{
		if (fullPath != null && fullPath != "") myPath = fullPath;
		else myPath = DB_PATH + DB_NAME;
		try {
			File dbFile = new File(myPath);
			if (dbFile.exists()) {
				dbFile.delete();
			}
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	private void copyDatabase(String fullPath) throws IOException {

		if (fullPath != null && fullPath != ""){
			DB_NAME = fullPath.substring(fullPath.lastIndexOf("/")+1);
			myPath = fullPath;
		}
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = myPath;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
		//openDatabase();
		// creatVirtualDB();
	}

	public void openDatabase(String fullPath) throws SQLException { // Open the DB

		if (fullPath != null && fullPath != "") myPath = fullPath;
		// myDataBase = this.getReadableDatabase();
		if (myDatabase != null) {
			if (myDatabase.isOpen()) return;
			else myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);

		} else myDatabase=SQLiteDatabase.openDatabase(myPath, null,  SQLiteDatabase.OPEN_READWRITE);
		return;
	}

	@Override
	public synchronized void close() {
		Log.e(this.toString(), "close DB");
		if (myDatabase != null)	myDatabase.close();
		SQLiteDatabase db = this.getReadableDatabase();
		db.close();
		super.close();
	}
}