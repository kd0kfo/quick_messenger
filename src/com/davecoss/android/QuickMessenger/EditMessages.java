/**
 * 
 */
package com.davecoss.android.QuickMessenger;

import com.davecoss.android.lib.Notifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * @author David Coss
 *
 */
public class EditMessages extends Activity {

	public static final String CLASS_NAME = "com.davecoss.android.QuickMessenger.EditMessages";
	private Notifier notifier;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.modify_db);
        
    	
    	MessageDB dbconn = new MessageDB(this.getApplicationContext());
    	notifier = new Notifier(this.getApplicationContext());
    	
    	try{
	    	String[] msgs = dbconn.getMessages();
	    	Log.d(CLASS_NAME,"Got " + msgs.length + "messages");
	    	Spinner msg_spinner = (Spinner) findViewById(R.id.msg_spinner);
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,msgs);
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    msg_spinner.setAdapter(adapter);
    	}
    	catch(Exception e)
    	{
    		Log.d(CLASS_NAME,"Database Error: " + e.getMessage());
    		notifier.toast_message("Database Error: " + e.getMessage());
    	}
    	
	}
	
	public void add_message(View view)
	{
		EditText new_msg = (EditText) findViewById(R.id.new_message);
		String strMessage = new_msg.getText().toString().trim();
		
		MessageDB dbconn = new MessageDB(this.getApplicationContext());
		try
		{
			dbconn.add_message(strMessage);
		}
		catch(Exception e)
		{
			notifier.toast_message("Could not add message. " + e.getLocalizedMessage());
		}
		
		finish();
		startActivity(getIntent());

	}
	
	public void remove_message(View view)
	{
		Spinner msg_spinner = (Spinner) findViewById(R.id.msg_spinner);
		String strMessage = msg_spinner.getSelectedItem().toString().trim();
		MessageDB dbconn = new MessageDB(this.getApplicationContext());
		
		try
		{
			dbconn.remove_message(strMessage);
		}
		catch(Exception e)
		{
			notifier.toast_message("Could not remove message. " + e.getLocalizedMessage());
		}
		
		finish();
		startActivity(getIntent());

	}
}
