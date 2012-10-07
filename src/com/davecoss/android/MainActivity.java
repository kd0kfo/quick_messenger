package com.davecoss.android;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.EditText;
import android.telephony.SmsManager;
import android.widget.Spinner;
import android.provider.ContactsContract.Contacts;  


public class MainActivity extends Activity {

	static final int PICK_CONTACT_REQUEST = 1;  // The request code
	public final static String EXTRA_MESSAGE = "com.davecoss.android.MESSAGE";
	public static final String PREFS_NAME = "TestAppPrefsFile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String last_number = settings.getString("last_number", "");
        EditText editText = (EditText) findViewById(R.id.editText1);
    	editText.setText(last_number);
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
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(Phone.NUMBER);
                String number = cursor.getString(column);
                EditText editText = (EditText) findViewById(R.id.editText1);
            	editText.setText(number);
               
            }
        }
    }
    
}

