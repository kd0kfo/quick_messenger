package com.davecoss.android.QuickMessenger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

public class MessageDB extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "messagedb";
	private static final String MESSAGE_TABLE_NAME = "messages";
	public static final String NAME = "com.davecoss.android.QuickMessenger.MessageDB";
	
	private static final String CREATE_SQL =
            "CREATE TABLE " + MESSAGE_TABLE_NAME + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " message TEXT, frequency INTEGER);";
	
	private String[] default_messages;
	
	public MessageDB(Context context) 
	{
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
        default_messages = context.getResources().getStringArray(R.array.spinner_messages);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try
		{
			db.execSQL(CREATE_SQL);
			
			int len = default_messages.length;
			for(int i = 0;i<len;i++)
			{
				db.execSQL("insert into " + MESSAGE_TABLE_NAME + "(message, frequency) values ('" 
						+ default_messages[i] + "', 0);");
			}
		}
		catch(SQLException sqle)
		{
			Log.e(NAME,"SQL Error: " + sqle.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public String[] getMessages()
	{
		String[] columns = {"message","frequency"};
		Cursor rows = this.getReadableDatabase().query(MESSAGE_TABLE_NAME, columns, null, null, null, null, "frequency");
		
		int numrows = rows.getCount();
		String[] retval = new String[numrows];
		
		int i = 0;
		while(rows.moveToNext())
		{
			retval[i++] = rows.getString(0);
		}
//		for(int i = 0;i<numrows;i++,rows.moveToNext())
//		{
//			if(rows.isAfterLast())
//				break;
//			retval[i] = rows.getString(0);
//		}
		
		return retval;
	}

}
