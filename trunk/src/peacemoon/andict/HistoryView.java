package peacemoon.andict;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class HistoryView extends Activity {
	private static final String HISTORY_TAG = "[Andict - HistoryView] ";

	private ListView mLSTHistory = null;
	private ArrayList<String> lstDict = null;
	private ArrayList<Integer> lstId = null;
	private ArrayAdapter<String> aptList = null;

	private ArrayList<String> mWordHistory = null;

	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		
		setContentView(R.layout.history);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getBoolean("saveHistory", true))
		{
			String strHistory = prefs.getString("history", "");
			Log.i(HISTORY_TAG, "History loaded");
			if (strHistory != null && !strHistory.equals(""))
			{
				mWordHistory = new ArrayList<String>(Arrays.asList(strHistory.split(",")));
			}
			else
			{
		        mWordHistory = new ArrayList<String>();
			}
		}
		else
		{
	        mWordHistory = new ArrayList<String>();
		}
		
		Log.d(HISTORY_TAG,"mWordHistory = " + mWordHistory.size());

		mLSTHistory = (ListView) findViewById(R.id.lstHistory); 

	    ImageButton btnClear = (ImageButton) findViewById(R.id.btnClear);
	    ImageButton btnBackToContent = (ImageButton) findViewById(R.id.btnBackToContent);

	    if (lstDict == null)
	    {
			lstDict = new ArrayList<String>();
			lstId = new ArrayList<Integer>();
			aptList = new ArrayAdapter<String>(getApplicationContext(),R.layout.customlist);
	    }
	    lstDict.clear();
	    lstId.clear();
	    aptList.clear();
		if (mWordHistory != null && mWordHistory.size() > 0)
		{
			try
			{
		        for (int i=0; i < mWordHistory.size(); i++)
		        {
		    		Log.i(HISTORY_TAG,"item = " + mWordHistory.get(i));
		    		String arrPart[] = mWordHistory.get(i).split("::");
		    		if (arrPart.length == 3)
		    		{
		    			//Log.i(CONTENT_TAG, "loaded content " +arrPart.length + ", wordId = " + arrPart[1]);
		    			//Log.i(CONTENT_TAG, "loaded 0");
		                lstDict.add(i,arrPart[0]);
		    			//Log.i(CONTENT_TAG, "loaded 1");
		                lstId.add(i,Integer.parseInt(arrPart[1]));
		    			//Log.i(CONTENT_TAG, "loaded 2");
		        		aptList.add(arrPart[2]);
		    		}
		    		else
		    		{
		    			Log.i(HISTORY_TAG,"Wrong entry: " + mWordHistory.get(i));
		    		}
		        } 
			}
			catch (Exception ex)
			{
				Log.i(HISTORY_TAG,"Wrong entry found!");
			}
	    }
	   
		//Log.i(CONTENT_TAG,"Adapter size = " + aptList.getCount());
		//assign result return
		mLSTHistory.setAdapter(aptList);

		mLSTHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3)
			{
/*				setContentView(R.layout.content);
				mSelectedDB = lstDict.get(arg2);
				String content = getContentById(lstId.get(arg2));
				initWebview();
				showContent(content);*/
				//mEDWord.setText(mAdapter.getItem(arg2));
				Intent i = new Intent();
				i.putExtra("dict", lstDict.get(arg2));
				i.putExtra("wordId", lstId.get(arg2));
				setResult(RESULT_OK,i);
				finish();
			}
		});
		
		btnClear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWordHistory.clear();
				aptList.clear();
				mLSTHistory.setAdapter(aptList);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("history", "");
				editor.commit();
				setResult(RESULT_OK);
				finish();
			}
		});
		
		btnBackToContent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
	}	
}
