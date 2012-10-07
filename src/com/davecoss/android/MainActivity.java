package com.davecoss.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.telephony.SmsManager;
import android.widget.Spinner;

public class MainActivity extends Activity {

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
}

