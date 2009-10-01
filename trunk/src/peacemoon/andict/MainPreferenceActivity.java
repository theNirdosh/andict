package peacemoon.andict;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MainPreferenceActivity extends PreferenceActivity
{
	SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		setResult(RESULT_OK);
		//prefs = getSharedPreferences("preferences",MODE_PRIVATE);
		//prefs.registerOnSharedPreferenceChangeListener();
	}
	
/*	@Override
	protected void onPause()
	{
		
	}*/
}