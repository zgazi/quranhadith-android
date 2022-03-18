package com.greenledge.common;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.os.Environment;
import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class DatabaseHandler {

	private boolean pathCorrectionRequired = false;
	public final int DB_VERSION = 23;
	private static String DB_NAME = "quran.db";
	private static String DB_PATH = "";
	protected SQLiteDatabase mDB;
	private static Context mContext;
	private File mFile;
	private boolean restoredFromBackup;
	private boolean error = false;

	public DatabaseHandler(Context ctx) {
		DB_PATH = MainApplication.FEED_DIR;
		DB_NAME = MainApplication.APP_NAME;
		mContext = ctx != null ? ctx : MainApplication.getInstance();
		
	}
	
	public DatabaseHandler(String fullPath) throws SQLException {
		if (fullPath != null && fullPath !="") {
			DB_NAME = fullPath.substring(fullPath.lastIndexOf("/")+1);
			DB_PATH = fullPath.substring(1,fullPath.lastIndexOf("/"));
		} else {
			DB_PATH = MainApplication.FEED_DIR;
			DB_NAME = MainApplication.APP_NAME;
		}
		
	}
	
	public DatabaseHandler(String path, String name) throws SQLException {
		if (name != null && name !="") DB_NAME = name; else DB_NAME=MainApplication.APP_NAME;
		if (path != null && path !="") DB_PATH = path; else DB_PATH=MainApplication.FEED_DIR;
	}
	
	public boolean isValid(){
		return (mDB == null) ? false : mDB.isOpen();
	}
	
	public File getFile() {
		return mFile;
	}

	public boolean isOpen() {
		if (mDB != null && !error)
			return mDB.isOpen();
		return false;
	}

	private boolean copy(String fullPath) throws IOException {

		if (fullPath != null && fullPath != ""){
			DB_NAME = fullPath.substring(fullPath.lastIndexOf("/")+1);
		}
		
		// Open your local db as the input stream
		InputStream myInput=null;
		//Check resources
		if (mContext != null){
		int id = mContext.getResources().getIdentifier(DB_NAME.substring(1,DB_NAME.indexOf(".")), "raw", mContext.getPackageName());
		if (id > 0)  myInput = mContext.getResources().openRawResource(id);
		else {
			if (Arrays.asList(mContext.getResources().getAssets().list("")).contains(DB_NAME))
				 myInput = mContext.getAssets().open(DB_NAME);
			else return false;
		}
		// Path to the just created empty db
		String outFileName = fullPath;

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
		}
		//openDatabase();
		//creatVirtualDB();
		if (MainApplication.ONLINE)
			IOHelper.copy(MainApplication.FEED_URL+MainApplication.APP_NAME+".db", MainApplication.FEED_DIR);
		return true;
	}

	public boolean open(String file) {
		error = false;
		File dbFile = new File(file);
		Log.i("DataDB","opening DB " + dbFile);
		mFile = dbFile;
		if (!dbFile.exists()) try { copy(file); } catch (IOException e) {Log.e("DatabaseHandler",e.toString());}
		mDB = openDB(dbFile);
		if (mDB == null) {
			return false;
		}
		boolean res = checkSchema();
		if (!res) {
			Log.i("DataDB","Closing DB due error while upgrade of schema: " + dbFile.getAbsolutePath());
			close();
			IOHelper.moveCorruptedFileToBackup(dbFile);
			if (!restoredFromBackup)
				IOHelper.restoreFromBackup(dbFile);
			mDB = openDB(dbFile);
			res = checkSchema();
			if (!res)
				close();
		}
		if (mDB != null) {
			return true;
		}
		return false;
	}

	public boolean close() {
		if (mDB != null) {
			try {
				Log.i("DataDB","Closing database");
				flush();
				//clearCaches();
				mDB.close();
				mDB = null;
				return true;
			} catch (SQLiteException e) {
				Log.e("DataDB","Error while closing DB " + DB_NAME);
			}
			mDB = null;
		}
		return false;
	}

	protected boolean checkSchema() {
		try {
			upgradeSchema();
			return true;
		} catch (SQLiteException e) {
			return false;
		}
	}

	private SQLiteDatabase openDB(File dbFile) {
		restoredFromBackup = false;
		SQLiteDatabase db = null;
		try {
			db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
			//db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			return db;
		} catch (SQLiteException e) {
			Log.e("DataDB","Error while opening DB " + dbFile.getAbsolutePath());
			IOHelper.moveCorruptedFileToBackup(dbFile);
			restoredFromBackup = IOHelper.restoreFromBackup(dbFile);
			try {
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				return db;
			} catch (SQLiteException ee) {
				Log.e("DataDB","Error while opening DB " + dbFile.getAbsolutePath());
			}
		}
		return null;
	}

	public Cursor getCursor(String sql){
		if (!isValid()) return null;
		ensureOpened();
		SQLiteStatement stmt = null;
		try {
			stmt = mDB.compileStatement(sql);
			return mDB.rawQuery(sql,null);
		}	catch (SQLiteException e){
			    if (e.getMessage().toString().contains("no such table")){
			            Log.e("DatabaseHandler", "Creating table " + sql + "because it doesn't exist!" );
			            // create table
			            // re-run query, etc.
			    } return null;
		} catch ( Exception e ) {
			// not found or error
			return null;
		} finally {
			if (stmt != null)
				stmt.close();
		}

	}
	
	public Map<String, String> getMap(String query) {
		
		Map map = new HashMap<String,String>();
		Cursor c = getCursor(query);
		return map;
	}
	
	public void execSQLIgnoreErrors( String... sqls )
	{
		for ( String sql : sqls ) {
			try {
				mDB.execSQL(sql);
			} catch ( SQLException e ) {
				// ignore
				Log.e("DatabaseHandler", "query failed, ignoring: " + sql);
			}
		}
	}

	protected void ensureOpened() {
		if (!isOpen())
			throw new RuntimeException("DB is not opened");
	}

	public void execSQL( String... sqls )
	{
		ensureOpened();
		for ( String sql : sqls ) {
			try {
				mDB.execSQL(sql);
			} catch ( SQLException e ) {
				// ignore
				Log.e("BaseDB", "SQL failed: " + sql);
				throw e;
			}
		}
	}

	public Long longQuery( String sql )
	{
		ensureOpened();
		SQLiteStatement stmt = null;
		try {
			stmt = mDB.compileStatement(sql);
			return stmt.simpleQueryForLong();
		} catch ( Exception e ) {
			// not found or error
			return null;
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	public String stringQuery( String sql )
	{
		ensureOpened();
		SQLiteStatement stmt = null;
		try {
			stmt = mDB.compileStatement(sql);
			return stmt.simpleQueryForString();
		} catch ( Exception e ) {
			// not found or error
			return null;
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	public static String quoteSqlString(String src) {
		if (src == null)
			return "null";
		String s = src.replaceAll("\\'", "\\\\'");
		return "'" + s + "'";
	}

	private boolean changed = false;

	/**
	 * Begin transaction, if not yet started, for changes.
	 */
	public void beginChanges() {
		if (!mDB.inTransaction()) {
			Log.i("DataDB","starting writable transaction");
			mDB.beginTransaction();
		}
		if (!changed) {
			Log.i("DataDB","modify readonly transaction to writable");
			changed = true;
		}
	}

	/**
	 * Begin transaction, if not yet started, for faster reading.
	 */
	public void beginReading() {
		if (!mDB.inTransaction()) {
			Log.i("DataDB","starting readonly transaction");
			mDB.beginTransaction();
		}
	}

	/**
	 * Rolls back transaction, if writing is not started.
	 */
	public void endReading() {
		if (mDB.inTransaction() && !changed) {
			Log.i("DataDB","ending readonly transaction");
			mDB.endTransaction();
		}
	}

	/**
	 * Commits or rolls back transaction, if started, and frees DB resources.
	 * Will commit only if beginChanges() has been called. Otherwise will roll back.
	 */
	public void flush() {
		if (mDB != null && mDB.inTransaction()) {
			if (changed) {
				changed = false;
				mDB.setTransactionSuccessful();
				Log.i("DataDB","flush: committing changes");
			} else {
				Log.i("DataDB","flush: rolling back changes");
			}
			mDB.endTransaction();
		}
	}

	protected boolean upgradeSchema() {
		if (mDB.needUpgrade(DB_VERSION)) {

			//==============================================================
			// add more updates above this line

			// set current version
				mDB.setVersion(DB_VERSION);
		}

		dumpStatistics();

		return true;
	}

	private void dumpStatistics() {
		Log.i("DataDB","mainDB: " + longQuery("SELECT count(*) FROM dictionary") + " keys found "

		);
	}
	
	public class QueryHelper {
		String tableName;
		QueryHelper(String tableName)
		{
			this.tableName = tableName;
		}
		ArrayList<String> fields = new ArrayList<String>();
		ArrayList<Object> values = new ArrayList<Object>();
		QueryHelper add(String fieldName, int value, int oldValue )
		{
			if ( value!=oldValue ) {
				fields.add(fieldName);
				values.add(Long.valueOf(value));
			}
			return this;
		}
		QueryHelper add(String fieldName, Long value, Long oldValue )
		{
			if ( value!=null && (oldValue==null || !oldValue.equals(value))) {
				fields.add(fieldName);
				values.add(value);
			}
			return this;
		}
		QueryHelper add(String fieldName, String value, String oldValue)
		{
			if ( value!=null && (oldValue==null || !oldValue.equals(value))) {
				fields.add(fieldName);
				values.add(value);
			}
			return this;
		}
		QueryHelper add(String fieldName, Double value, Double oldValue)
		{
			if ( value!=null && (oldValue==null || !oldValue.equals(value))) {
				fields.add(fieldName);
				values.add(value);
			}
			return this;
		}
		Long insert()
		{
			if ( fields.size()==0 )
				return null;
			beginChanges();
			StringBuilder valueBuf = new StringBuilder();
			try {
				String ignoreOption = ""; //"OR IGNORE ";
				StringBuilder buf = new StringBuilder("INSERT " + ignoreOption + " INTO ");
				buf.append(tableName);
				buf.append(" (id");
				for ( String field : fields ) {
					buf.append(",");
					buf.append(field);
				}
				buf.append(") VALUES (NULL");
				for ( @SuppressWarnings("unused") String field : fields ) {
					buf.append(",");
					buf.append("?");
				}
				buf.append(")");
				String sql = buf.toString();
				Log.i("cr3db", "going to execute " + sql);
				SQLiteStatement stmt = null;
				Long id = null;
				try {
					stmt = mDB.compileStatement(sql);
					for ( int i=1; i<=values.size(); i++ ) {
						Object v = values.get(i-1);
						valueBuf.append(v!=null ? v.toString() : "null");
						valueBuf.append(",");
						if ( v==null )
							stmt.bindNull(i);
						else if (v instanceof String)
							stmt.bindString(i, (String)v);
						else if (v instanceof Long)
							stmt.bindLong(i, (Long)v);
						else if (v instanceof Double)
							stmt.bindDouble(i, (Double)v);
					}
					id = stmt.executeInsert();
					Log.i("DataDB", "added book, id=" + id + ", query=" + sql);
				} finally {
					if ( stmt!=null )
						stmt.close();
				}
				return id;
			} catch ( Exception e ) {
				Log.e("DataDB", "insert failed: " + e.getMessage());
				Log.e("DataDB", "values: " + valueBuf.toString());
				return null;
			}
		}
		boolean update( Long id )
		{
			if ( fields.size()==0 )
				return false;
			beginChanges();
			StringBuilder buf = new StringBuilder("UPDATE ");
			buf.append(tableName);
			buf.append(" SET ");
			boolean first = true;
			for ( String field : fields ) {
				if ( !first )
					buf.append(",");
				buf.append(field);
				buf.append("=?");
				first = false;
			}
			buf.append(" WHERE id=" + id );
			Log.i("DataDB","executing " + buf);
			mDB.execSQL(buf.toString(), values.toArray());
			return true;
		}
	}


}
