package com.davecoss.android.QuickMessenger;

import com.davecoss.android.QuickMessenger.R;

import com.davecoss.android.lib.Notifier;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.telephony.SmsManager;
import android.widget.Spinner;
import android.provider.ContactsContract.Contacts; 


public class MainActivity extends Activity {

	static final int PICK_CONTACT_REQUEST = 1;  // The request code
	static final int EDIT_MESSAGE_REQUEST = 2; // code for editing message DB
	public final static String EXTRA_MESSAGE = "com.davecoss.android.QuickMessenger.MESSAGE";
	public static final String PREFS_NAME = "TestAppPrefsFile";
	
	private Notifier notifier;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifier = new Notifier(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String last_number = settings.getString("last_number", "");
        EditText editText = (EditText) findViewById(R.id.editText1);
    	editText.setText(last_number);
    	
    	populate_spinner();
    	
    }

    private void populate_spinner()
    {
    	MessageDB dbconn = new MessageDB(this.getApplicationContext());
    	Spinner msg_spinner = (Spinner) findViewById(R.id.spinner1);
    	try{
	    	String[] msgs = dbconn.getMessages();
	    	Log.d(EXTRA_MESSAGE,"Got " + msgs.length + "messages");
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,msgs);
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    msg_spinner.setAdapter(adapter);
    	}
    	catch(Exception e)
    	{
    		Log.d(EXTRA_MESSAGE,"Database Error: " + e.getMessage());
    		notifier.toast_message("Database Error: " + e.getMessage());
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /** Called when the user clicks the Send button */
    public void displayMessage(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.editText1);
    	Spinner msg_spinner = (Spinner) findViewById(R.id.spinner1);
    	String number = editText.getText().toString();
    	String message = "MSG: " + msg_spinner.getSelectedItem().toString();
    	message = message + "\nSent to: " + number;
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
    
    public void sendMessage(View view){
    	SmsManager sm = SmsManager.getDefault();
    	// here is where the destination of the text should go
    	EditText editText = (EditText) findViewById(R.id.editText1);
    	Spinner msg_spinner = (Spinner) findViewById(R.id.spinner1);
    	String number = editText.getText().toString();
    	String msg = msg_spinner.getSelectedItem().toString();
    	sm.sendTextMessage(number, null, msg, null, null);
    	EditText send_status = (EditText) findViewById(R.id.send_status);
    	send_status.setText("Sent to " + number);
    	
    	// Save last number used
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    	SharedPreferences.Editor editor = settings.edit();
        editor.putString("last_number", number);
        editor.commit();
    }
    
 
    public void pickContact(View view) {
    	Uri contact_uri = Contacts.CONTENT_URI;
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, contact_uri);
        pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        switch(requestCode)
        {
        case PICK_CONTACT_REQUEST:
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {Phone.NUMBER};

                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(Phone.NUMBER);
                String number = cursor.getString(column);
                EditText editText = (EditText) findViewById(R.id.editText1);
            	editText.setText(number);
               
            }
        	break;
        case EDIT_MESSAGE_REQUEST:
        	populate_spinner();
        	break;
        	default:
        		break;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_settings:
            	Intent settings_activity = new Intent(getBaseContext(), EditMessages.class);
            	startActivityForResult(settings_activity,EDIT_MESSAGE_REQUEST);
            	return true;
            case R.id.menu_version:
            	Resources res = getResources();
            	String version = "";
            	try
            	{
            		version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            	}
            	catch (PackageManager.NameNotFoundException e)
                {
                    Log.e(EXTRA_MESSAGE, e.getMessage());
                }
            	notifier.toast_message(res.getString(R.string.menu_version) + ": " + version);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
}

