package peacemoon.andict;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class GoogleTranslateActivity extends Activity {
	
	static final private String GOOGLETRANSLATE_TAG = "[Andict - Google]";
	
	//private Translate translate;
	
	private String mLanguageNames[];
	private String mLanguageCodes[];
	private ArrayList<String> lstLanguageNames;
	private ArrayList<String> lstLanguageCodes;
	private ArrayAdapter<String> aaLanguageNames;
	
	private ImageButton btnTranslate;
	private ImageButton btnChangeDirection;
	private EditText edInput;
	private EditText edOutput;
	private Spinner spnSourceLanguages;
	private Spinner spnDestinationLanguages;
	
	private ProgressDialog dlgProgress;
	private Runnable tTranslate;
	private Handler hShowProgress;
	private String strResult;
	
	private Language from;
	private Language to;

	/*	private TTS tts;
	private TTS.InitListener ttsInitListener;*/

	SharedPreferences prefs;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.googletranslate);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        String fromLanguage = prefs.getString("sourceLanguage", Language.ENGLISH.toString());
        String toLanguage = prefs.getString("destinationLanguage", Language.GERMAN.toString());
        
        mLanguageNames = (String[]) getResources().getStringArray(R.array.lstLanguageNames);
        mLanguageCodes = (String[]) getResources().getStringArray(R.array.lstLanguageCodes);
        
        btnTranslate = (ImageButton) findViewById(R.id.btnTranslate);
        btnChangeDirection = (ImageButton) findViewById(R.id.btnChangeDirection);
        edInput = (EditText) findViewById(R.id.edInput);
        edOutput = (EditText) findViewById(R.id.edOuput);
        
        lstLanguageNames = new ArrayList<String>(Arrays.asList(mLanguageNames));
        lstLanguageCodes = new ArrayList<String>(Arrays.asList(mLanguageCodes));
        
        spnSourceLanguages = (Spinner) findViewById(R.id.spnSourceLanguages);
        spnDestinationLanguages = (Spinner) findViewById(R.id.spnDestinationLanguages);
        
        aaLanguageNames = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstLanguageNames);
        aaLanguageNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spnSourceLanguages.setAdapter(aaLanguageNames);
        spnDestinationLanguages.setAdapter(aaLanguageNames);
        
        spnSourceLanguages.setSelection(lstLanguageCodes.indexOf(fromLanguage));
        spnDestinationLanguages.setSelection(lstLanguageCodes.indexOf(toLanguage));

/*        ttsInitListener = new TTS.InitListener() {
			@Override
			public void onInit(int arg0) {
				// TODO Auto-generated method stub
				try
				{
					tts.setLanguage("en");
					tts.speak("Hello World", 0, null);
				}
				catch (Exception ex)
				{
					Log.e(GOOGLETRANSLATE_TAG, "Error = " + ex.toString());
				}
			}
		};

		tts = new TTS(this,ttsInitListener,true);*/
		
        edInput.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edInput.selectAll();
			}
		});
       
        btnChangeDirection.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final int temp = spnSourceLanguages.getSelectedItemPosition();
				spnSourceLanguages.setSelection(spnDestinationLanguages.getSelectedItemPosition());
				spnDestinationLanguages.setSelection(temp);
		        /*spnSourceLanguages.setSelection(lstLanguageCodes.indexOf(fromLanguage));
		        spnDestinationLanguages.setSelection(lstLanguageCodes.indexOf(toLanguage));*/
				
			}
		});
        
        btnTranslate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(GOOGLETRANSLATE_TAG,"Start translating...");
				final String input = edInput.getText().toString();
				
				final int sourceLanguage = spnSourceLanguages.getSelectedItemPosition();
				final int destinationLanguage = spnDestinationLanguages.getSelectedItemPosition();
				
				dlgProgress = ProgressDialog.show(GoogleTranslateActivity.this, "Translating...", "Please wait!");
				hShowProgress = new Handler()
				{
					public void handleMessage(Message msg)
					{
						edOutput.setText(strResult);
						Log.i(GOOGLETRANSLATE_TAG, msg.toString());
					}
				};

				for (Language l : Language.values())
				{
					if (l.toString().equals(lstLanguageCodes.get(sourceLanguage)))
					{
						from = l;
						Log.i(GOOGLETRANSLATE_TAG,"source = " + from.toString());
					}
					if (l.toString().equals(lstLanguageCodes.get(destinationLanguage)))
					{
						to = l;
						Log.i(GOOGLETRANSLATE_TAG,"dest = " + to.toString());
					}
				}
				tTranslate = new Runnable()
				{
					public void run()
					{
						try
						{
							strResult = Translate.translate(input, from, to);
							//Log.d(GOOGLETRANSLATE_TAG,"Result = " + strResult);
							hShowProgress.sendEmptyMessage(0);
							dlgProgress.dismiss();
						}
						catch (Exception ex)
						{
							Log.e(GOOGLETRANSLATE_TAG, "Error = " + ex.toString());
							hShowProgress.sendEmptyMessage(0);
							dlgProgress.dismiss();
						}
					}
				};
				tTranslate.run();
			}
		});
    }
    
    @Override
    public void onPause()
    {
    	super.onPause();
       	SharedPreferences.Editor editor = prefs.edit();
       	editor.putString("sourceLanguage", lstLanguageCodes.get(spnSourceLanguages.getSelectedItemPosition()));
       	editor.putString("destinationLanguage", lstLanguageCodes.get(spnDestinationLanguages.getSelectedItemPosition()));
		editor.commit();
    }
    
	
}
