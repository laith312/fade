package com.messenger.fade.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;

import com.messenger.fade.MessageConstants;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.model.Message;
import com.messenger.fade.util.MLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Local message database
 *
 * @author kkawai
 *
 */
public final class LocalMessageManager {

	private static final String TAG = LocalMessageManager.class.getSimpleName();
	private static LocalMessageManager instance;
	private static final String MESSAGES_DATABASE_NAME = "messages.db";
	private static final String MESSAGE_TABLE_NAME = "message";
	private static final int CURRENT_VERSION = 6;

	private DbOpenHelper sqlHelper;

	private LocalMessageManager() {
		sqlHelper = new DbOpenHelper(FadeApplication.getInstance(), MESSAGES_DATABASE_NAME, null, CURRENT_VERSION);
	}

	public static LocalMessageManager getInstance() {
		if (instance == null) {
			instance = new LocalMessageManager();
			MLog.i(TAG, "MessageManager instantiated instance..");
		}
		return instance;
	}

	public static void closeDb() {
		if (instance != null && instance.sqlHelper != null) {
			instance.sqlHelper.close();
			instance = null;
			MLog.i(TAG, "MessageManager database closed..");
		}
	}

	public synchronized void insertOrUpdate(final Message mh) {
		try {
			final SQLiteDatabase db = sqlHelper.getWritableDatabase();
			final long rowId = db.replace(MESSAGE_TABLE_NAME, null,
					getContentValues(mh));
			MLog.i(TAG, "MessageManager.insertOrUpdate() ", mh.text, " insert/update at ", rowId);
		} catch (Throwable t) {
			MLog.e(TAG, "MessageManager.insertOrUpdate() Error in storing message holder: ", t);
		}
	}

	public synchronized int deleteAllMessages() {
		try {
			final SQLiteDatabase db = sqlHelper.getWritableDatabase();
			final int rowsDeleted = db.delete(MESSAGE_TABLE_NAME, "1", null);
			MLog.i(TAG, "MessageManager.deleteAllMessages() deleted ", rowsDeleted, " messages");
			return rowsDeleted;
		} catch (Throwable t) {
			MLog.e(TAG, "MessageManager.deleteAllMessage() failed: ", t);
		}
		return 0;
	}

	public synchronized void deleteMessagesByContainer(final String containerid) {
		
		final String split[] = containerid.split("_");
		
		int rowsDeleted;
		try {
			final SQLiteDatabase db = sqlHelper.getWritableDatabase();
			
			if (split.length == 1) {
				rowsDeleted = db.delete(MESSAGE_TABLE_NAME, DbColumns.CONTAINER_ID + "=" + containerid, null);
			} else {
				rowsDeleted = db.delete(MESSAGE_TABLE_NAME, DbColumns.CONTAINER_ID + "=" + split[1] + " OR " + DbColumns.CONTAINER_ID + "=" + containerid, null);
			}

			MLog.i(TAG, "MessageManager.deleteMessagesByContainer(", containerid, ")  deleted ", rowsDeleted, " messages");
		} catch (Throwable t) {
			MLog.i(TAG, "MessageManager.deleteMessagesByContainer(", containerid, ")  failed ",t);
		}
	}
	
	public synchronized Message updateMessageRead(final String uniqueid, final Date read) {
		
		Message mh = null;
		Cursor cursor = null;
		try {
			final SQLiteDatabase db = sqlHelper.getReadableDatabase();
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(MESSAGE_TABLE_NAME);
			qb.appendWhere(DbColumns.UNIQUE_ID + "='" + uniqueid+"'" + " and " + DbColumns.READ + " is null");
			cursor = qb.query(db, null, null, null, null, null,
					DbColumns.DEFAULT_SORT_ORDER);
			final ContentValues contentValues = new ContentValues();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
				mh = new Message();
				final Integer idVal = contentValues.getAsInteger(DbColumns.ID);
				if (idVal != null) {
					mh.sqlliteid = idVal.intValue();
				}
				mh.touserid = contentValues.getAsString(DbColumns.CONTAINER_ID);
				mh.date = new Date(contentValues.getAsLong(DbColumns.DATELONG));
				mh.deviceid = contentValues.getAsString(DbColumns.DEVICEID);
				mh.filekey = contentValues.getAsString(DbColumns.FILEKEY);
				mh.text = contentValues.getAsString(DbColumns.TEXT);
				mh.uniqueid = contentValues.getAsString(DbColumns.UNIQUE_ID);
				mh.username = contentValues.getAsString(DbColumns.USERNAME);
				mh.read = read;
				MLog.i(TAG, "MessageManager updateMessageRead() mh.uniqueid="+mh.uniqueid);
				
				insertOrUpdate(mh);
				break;
			}
		} catch (Exception e) {
			MLog.e(TAG, "MessageManager getMessageByUniqueId() failed: ", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return mh;
	}
	
	public synchronized Message getMessage(final String uniqueid) {
		Message mh = null;
		Cursor cursor = null;
		try {
			final SQLiteDatabase db = sqlHelper.getReadableDatabase();
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(MESSAGE_TABLE_NAME);
			qb.appendWhere(DbColumns.UNIQUE_ID + "='" + uniqueid+"'");
			cursor = qb.query(db, null, null, null, null, null,
					DbColumns.DEFAULT_SORT_ORDER);
			final ContentValues contentValues = new ContentValues();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
				mh = new Message();
				final Integer idVal = contentValues.getAsInteger(DbColumns.ID);
				if (idVal != null) {
					mh.sqlliteid = idVal.intValue();
				} else {
					return null;
				}
				mh.touserid = contentValues.getAsString(DbColumns.CONTAINER_ID);
				mh.date = new Date(contentValues.getAsLong(DbColumns.DATELONG));
				mh.deviceid = contentValues.getAsString(DbColumns.DEVICEID);
				mh.filekey = contentValues.getAsString(DbColumns.FILEKEY);
				mh.text = contentValues.getAsString(DbColumns.TEXT);
				mh.uniqueid = contentValues.getAsString(DbColumns.UNIQUE_ID);
				mh.username = contentValues.getAsString(DbColumns.USERNAME);
				try {
					mh.read = new Date(contentValues.getAsLong(DbColumns.READ));
				}catch(final Exception e) {
					//TODO: logging
				}
				MLog.i(TAG, "MessageManager getMessage() mh.uniqueid="+mh.uniqueid);
				break;
			}
			
		} catch (Exception e) {
			MLog.e(TAG, "MessageManager getMessageByUniqueId() failed: ", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return mh;
	}
	
	public synchronized List<Message> getMessagesByContainer(final String containerid, final int floorid) {
		
		final String split[] = containerid.split("_");
		
		final long startTime = new Date().getTime();
		Cursor cursor = null;
		
		final ArrayList<Message> list = new ArrayList<Message>();
		try {
			final SQLiteDatabase db = sqlHelper.getReadableDatabase();
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(MESSAGE_TABLE_NAME);
			
			if (split.length == 1) {
				if (floorid != 0) {
					qb.appendWhere(String.format("%s ='%s' and %s < %d", DbColumns.CONTAINER_ID, containerid, DbColumns.ID, floorid));
				} else {
					qb.appendWhere(String.format("%s ='%s' ", DbColumns.CONTAINER_ID, containerid));
				}
			} else {
				MLog.i(TAG, "touserid: "+containerid);
				if (floorid != 0) {
					qb.appendWhere(String.format("( %s ='%s' OR %s ='%s') and %s < %d", DbColumns.CONTAINER_ID, split[1], DbColumns.CONTAINER_ID, containerid, DbColumns.ID, floorid));
				} else {
					qb.appendWhere(String.format("%s ='%s' OR %s ='%s'", DbColumns.CONTAINER_ID, split[1], DbColumns.CONTAINER_ID, containerid));
				}
			}
			
			cursor = qb.query(db, null, null, null, null, null, DbColumns.ID+" Desc", ""+ MessageConstants.MAX_LOCAL_MESSAGE_FETCH_SIZE);
			final ContentValues contentValues = new ContentValues();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
				final Message mh = new Message();
				final Integer idVal = contentValues.getAsInteger(DbColumns.ID);
				if (idVal != null) {
					mh.sqlliteid = idVal.intValue();
				}
				mh.touserid = contentValues.getAsString(DbColumns.CONTAINER_ID);
				mh.date = new Date(contentValues.getAsLong(DbColumns.DATELONG));
				
				try {
					final Long read = contentValues.getAsLong(DbColumns.READ);
					if (read != null) {
						mh.read = new Date(read);
					}
				}catch(final Exception e){
					//TODO: logging
				}
				
				mh.deviceid = contentValues.getAsString(DbColumns.DEVICEID);
				mh.filekey = contentValues.getAsString(DbColumns.FILEKEY);
				mh.text = contentValues.getAsString(DbColumns.TEXT);
				mh.uniqueid = contentValues.getAsString(DbColumns.UNIQUE_ID);
				mh.username = contentValues.getAsString(DbColumns.USERNAME);
				cursor.moveToNext();
				list.add(mh);
			}
			final long endTime = new Date().getTime();
			MLog.i(TAG, "MessageManager.getMessagesByContainer() got ",list.size(), " messages for container ", containerid, " total time=", ((endTime-startTime) / 1000), " seconds");
		} catch (Exception e) {
			MLog.e(TAG, "MessageManager.getMessagesByContainer(",containerid,")  failed: ", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		try {
			Collections.reverse(list);
		} catch(final Throwable t){}
		return list;
	}
	
	public synchronized void deleteMessage(final String uniqueid) {
		try {
			final SQLiteDatabase db = sqlHelper.getWritableDatabase();
			final int rowsDeleted = db.delete(
					MESSAGE_TABLE_NAME,
					DbColumns.UNIQUE_ID + "='"
							+ uniqueid +"'", null);
			MLog.i(TAG, "MessageManager.deleteMessage("+ uniqueid + ") rowsDeleted="+ rowsDeleted);
		} catch (Throwable t) {
			MLog.e(TAG, "Error in deleting user: ", t);
		}
	}
	
	private ContentValues getContentValues(final Message mh) {

		final ContentValues values = new ContentValues();
		if (mh.sqlliteid != 0)
			values.put(DbColumns.ID, mh.sqlliteid);
		values.put(DbColumns.CONTAINER_ID, mh.touserid);
		values.put(DbColumns.TEXT, mh.text);
		values.put(DbColumns.USERNAME, mh.username);
		values.put(DbColumns.DEVICEID, mh.deviceid);
		if (mh.date != null) {
			values.put(DbColumns.DATELONG, mh.date.getTime());
		}
		if (mh.read != null) {
			values.put(DbColumns.READ, mh.read.getTime());
		}
		values.put(DbColumns.FILEKEY, mh.filekey);
		values.put(DbColumns.UNIQUE_ID, mh.uniqueid);
		return values;
	}	

	public static final class DbColumns implements BaseColumns {

		// no instances please
		private DbColumns() {
		}

		public static final String ID = "sqlliteid";
		public static final String CONTAINER_ID = "touserid";
		public static final String TEXT = "text";
		public static final String USERNAME = "username";
		public static final String DEVICEID = "deviceid";
		public static final String DATE = "date";
		public static final String DATELONG = "datelong";
		public static final String READ = "read";
		public static final String FILEKEY = "filekey";
		public static final String UNIQUE_ID = "uniqueid";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "";
	}
	
	private void upgrade(final SQLiteDatabase db) {
		
		if (db == null) return;
		
		Cursor cursor = null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy EEE, d MMM hh:mm:ss a");
		
		try {
			final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(MESSAGE_TABLE_NAME);
			cursor = qb.query(db, null, null, null, null, null,
					DbColumns.DEFAULT_SORT_ORDER);
			final ContentValues contentValues = new ContentValues();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
				final Message mh = new Message();
				final Integer idVal = contentValues.getAsInteger(DbColumns.ID);
				if (idVal != null) {
					mh.sqlliteid = idVal.intValue();
				}
				mh.touserid = contentValues.getAsString(DbColumns.CONTAINER_ID);
			
				//old column
				final String dateString = contentValues.getAsString(DbColumns.DATE);
				//new column
				final Long dateLong = contentValues.getAsLong(DbColumns.DATELONG);

				//upgrade column, if not upgraded yet
				boolean doUpgradeColumn = false;
				if (dateLong == null) {
					doUpgradeColumn = true;
					if (dateString != null) {
						mh.date = dateFormat.parse("2012 " + dateString);
					} else {
						mh.date = new Date();
					}
				}
				final Long readLong = contentValues.getAsLong(DbColumns.READ);
				if (readLong != null) {
					mh.read = new Date(readLong);
				}
				mh.deviceid = contentValues.getAsString(DbColumns.DEVICEID);
				mh.filekey = contentValues.getAsString(DbColumns.FILEKEY);
				mh.text = contentValues.getAsString(DbColumns.TEXT);
				mh.uniqueid = contentValues.getAsString(DbColumns.UNIQUE_ID);
				mh.username = contentValues.getAsString(DbColumns.USERNAME);
				cursor.moveToNext();
				if (doUpgradeColumn) {
					db.replace(MESSAGE_TABLE_NAME, null,
							getContentValues(mh));
					MLog.i(TAG, "MessageManager updated ", mh.text, " mh.date=", mh.date.toString());
				}
			}
			MLog.i(TAG, "MessageManager upgraded ");
		} catch (Exception e) {
			MLog.e(TAG, "MessageManager upgrade failed.. ", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Do not extend android SQLiteOpenHelper since I want to specify my own location on the sdcard, if available.  So that messages are permanent, no uninstallable.
	 * @author kkawai
	 *
	 */
	private class DbOpenHelper  {
		
		private SQLiteDatabase database;
		DbOpenHelper(final Context context, final String name, final CursorFactory factory, final int currentVersion) {
			try {
				database = SQLiteDatabase.openOrCreateDatabase(context.getFilesDir() +"/" + name,  null);
			} catch(final Exception e) {
			}
			if (database == null) { 
				return;
			}
			if (database.getVersion() == 0) {
				onCreate(database);
				database.setVersion(1);
			}
			MLog.i(TAG, "MessageManager initialized database.. version="+database.getVersion());
			if (database.getVersion() < currentVersion) {
				onUpgrade(database, currentVersion);
			} else {
				MLog.i(TAG, "MessageManager database did not need to be upgraded..");
			}
		}
		
		void close() {
			if (database != null) {
				database.close();
			}
		}

		SQLiteDatabase getReadableDatabase() {
			return database;
		}

		SQLiteDatabase getWritableDatabase() {
			return database;
		}

		void onCreate(final SQLiteDatabase db) {
			if (db == null) {
				return;
			}
			try {
				db.execSQL("CREATE TABLE " + MESSAGE_TABLE_NAME + " ("
						+ DbColumns.ID
						+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ DbColumns.CONTAINER_ID + " TEXT, "
						+ DbColumns.TEXT + " TEXT, "
						+ DbColumns.USERNAME + " TEXT, " 
						+ DbColumns.DEVICEID + " TEXT, " 
						+ DbColumns.DATE + " TEXT, "
						+ DbColumns.FILEKEY + " TEXT, "
						+ DbColumns.UNIQUE_ID + " TEXT );");
				
				MLog.i(TAG, "MessageManager.onCreate() created message database..");  
				db.execSQL("CREATE INDEX containerid_index on message(touserid);");
				db.execSQL("CREATE INDEX uniqueid_index on message(uniqueid);");
				MLog.i(TAG, "MessageManager.onCreate() created indexes on message database..");

			} catch (Throwable t) {
				MLog.w(TAG, "Error in creating extension table: It may already exist which is fine..", t);
			}
		}
		
		void onUpgrade(final SQLiteDatabase db, final int newVersion) {
			if (db == null) {
				return;
			}
			try {
				db.execSQL("alter table message add datelong integer");
			}catch(final Exception e) {
			}
			
			try {
				db.execSQL("alter table message add read integer");
			}catch(final Exception e) {
			}			
			
			try {
				upgrade(db);
			}catch(final Exception e) {
				MLog.e(TAG, "MessageManager database upgrade failed..",e);
			}
			
			db.setVersion(newVersion);
		}
	}
}
