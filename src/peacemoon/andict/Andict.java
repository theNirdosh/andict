/*
 * TODO
 * - google translate
 * - tts
 */
package peacemoon.andict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableRow.LayoutParams;

public class Andict extends Activity {
	
	static final private String MAIN_TAG = "[Andict]";

	static final private int SHOW_CONTENT_CODE = 1;
	static final private int SHOW_PREFERENCE_CODE = 2;
	
	static final private int MENU_ITEM = Menu.FIRST;
//	static final private int VIEW_TEXTVIEW_ID = 200;
//	static final private int VIEW_CHECKBOX_ID = 300;
	static final private int VIEW_RADIO_ID = 400;

//	private DictionaryEngine mDBEngine;
	
	private String mDBExtension;
	private String mDBPath;

	//private int mSelectedDBIndex;
	
	private DatabaseFileList mDBList;
	private DatabaseFile mDBFile;
	
	private SharedPreferences prefs;
	private int mWaitingTime;
	private boolean mSaveHistory;
	
    Menu menu = null;
    
    private EditText edWord = null;
    private ListView lstWord = null;
    private ImageButton btnDictionaryManager = null;
    private ImageButton btnGoogleTranslate = null;
    private ImageButton btnPronounce = null;
    private ImageButton btnInfo = null;
    
	public ArrayList<String> mLSTCurrentWord = null;
	//public ArrayList<String> mLSTCurrentContent = null;
	public ArrayList<Integer> mLSTCurrentWordId = null;
	
    private ArrayAdapter<String> mAdapter = null;

    private Handler mHandler;
    private Runnable mUpdateTimeTask;
    
    InputMethodManager imm;
    // create Menu for Program
    @Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 	  super.onCreateOptionsMenu(menu);

 	  // Group ID
 	  int groupId = 0;
 	  
 	  // Unique menu item identifier. Used for event handling.
 	  int menuItemId = MENU_ITEM;

 	  // The order position of the item
 	  int menuItemOrder = Menu.NONE;
 	  this.menu=menu;

 	  menu.add(groupId, menuItemId+0, menuItemOrder, getString(R.string.menuPreference)).setIcon(R.drawable.preference);
 	  menu.add(groupId, menuItemId+1, menuItemOrder, getString(R.string.menuHelp)).setIcon(R.drawable.help);
 	  
 	 
 	  return true;
 	}
    
	// process event select Menu
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
				
		// Find which menu item has been selected
		switch (item.getItemId()) {
			case (MENU_ITEM+0): 
			{
				menuPreference();
				break;
			}
			case (MENU_ITEM+1): 
			{
				//menuDictionaryManager();
				menuHelp();
				break;
			}
		}
	    return true;
	}
 	
	public void menuHelp()
	{
		openAbout("file:///android_asset/about.html",true);
	}
	
	public void menuGoogleTranslate()
	{
		//Log.i(MAIN_TAG, "Start google translate");
		startActivity(new Intent(this, GoogleTranslateActivity.class));
	}
	
	public void menuPreference()
	{
		//Log.i(MAIN_TAG, "Start preference");
		startActivityForResult(new Intent(this, MainPreferenceActivity.class), SHOW_PREFERENCE_CODE);
	}
	
/*	public void openAbout(String strAboutPath, boolean bType)
	{
		WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();

        Dialog dialog = new Dialog(Andict.this) {
		    public boolean onKeyDown(int keyCode, KeyEvent event)
		    {
		    	this.dismiss();
			    return true;
	    	}
	    };
	    
		dialog.setTitle("About");
		//dialog.addContentView(wvInfo, new LinearLayout.LayoutParams(width-10, height-10));
		dialog.setContentView(R.layout.about);
		
		
//		WebView wvInfo = new WebView(Andict.this);
	    WebView wvInfo = (WebView) dialog.findViewById(R.id.wvInfo);
		
		if (bType == false) // about of dictionary
		{
			String strLine;
			StringBuilder sbInfo = new StringBuilder();
			Log.i(MAIN_TAG,"About path = " + strAboutPath);
			File fInfo = new File(strAboutPath);
			if (fInfo.exists() && fInfo.length() < 8096)
			{
				try{
				    BufferedReader br = new BufferedReader(new FileReader(fInfo));
					//Read File Line By Line
					while ((strLine = br.readLine()) != null)   {
						sbInfo.append(strLine);
					}
					
					//Close the input stream
					br.close();
				}
				catch (Exception e)
				{//Catch exception if any
					sbInfo.append("<html><body><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
					sbInfo.append(mDBFile.fileName);
					sbInfo.append("</body></html>");
				}				
			}
			else
			{
				Log.i(MAIN_TAG,"About file doesn't exist!");
				sbInfo.append("<html><body><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
				sbInfo.append(mDBFile.fileName);
				sbInfo.append("</body></html>");
			}
			wvInfo.loadDataWithBaseURL (null, sbInfo.toString(), "text/html", "UTF-8","about:blank");
		}
		else
		{
			wvInfo.loadUrl(strAboutPath);
		}

		dialog.show();
	}*/

	public void openAbout(String strAboutPath, boolean bType)
	{
		setContentView(R.layout.about);
	    WebView wvInfo = (WebView) findViewById(R.id.wvInfo);
		
		if (bType == false) // about of dictionary
		{
			String strLine;
			StringBuilder sbInfo = new StringBuilder();
			Log.i(MAIN_TAG,"About path = " + strAboutPath);
			File fInfo = new File(strAboutPath);
			if (fInfo.exists() && fInfo.length() < 8096)
			{
				try{
				    BufferedReader br = new BufferedReader(new FileReader(fInfo));
					//Read File Line By Line
					while ((strLine = br.readLine()) != null)   {
						sbInfo.append(strLine);
					}
					
					//Close the input stream
					br.close();
				}
				catch (Exception e)
				{//Catch exception if any
					sbInfo.append("<html><body><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
					sbInfo.append(mDBFile.fileName);
					sbInfo.append("</body></html>");
				}				
			}
			else
			{
				Log.i(MAIN_TAG,"About file doesn't exist!");
				sbInfo.append("<html><body><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
				sbInfo.append(mDBFile.fileName);
				sbInfo.append("</body></html>");
			}
			wvInfo.loadDataWithBaseURL (null, sbInfo.toString(), "text/html", "UTF-8","about:blank");
		}
		else
		{
			wvInfo.loadUrl(strAboutPath);
		}

		Button btnOkAbout = (Button) findViewById(R.id.btnOkAbout);
        btnOkAbout.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
                setContentView(R.layout.main);
                if (mDBList.items.size() > 0)
                {
                    menuMain();
                }
        	}
        });
		
	}
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
        
        mDBExtension = getResources().getString(R.string.dbExtension);
        mDBPath = getResources().getString(R.string.dbPath);

        mDBList = new DatabaseFileList(mDBPath,mDBExtension);
        
        //mSelectedDB = new ArrayList<String>();
        
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mWaitingTime = Integer.parseInt(prefs.getString("waitingTime", "1"));
        
        loadPreferences();
        
        /*TODO :
         * - no database file -> message and go to downloader
         */
        setContentView(R.layout.main);
        if (mDBList.items.size() > 0)
        {
            menuMain();
        }
        else
        {
			new AlertDialog.Builder(this)
			.setMessage(R.string.errorNoData)
			.setTitle(getString(R.string.error))
			.setNeutralButton(getString(R.string.ok),
			   new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int whichButton){}
			   })
			.show();        
        }
    }
    
    @Override
    public void onPause()
    {
    	super.onDestroy();
    	if (mSaveHistory == false)
    	{
           	SharedPreferences.Editor editor = prefs.edit();
       		editor.remove("history");
    		editor.commit();
    	}
    }
/*	public void menuSearchDictionary()
	{
		setContentView(R.layout.dbmanager);
		TableLayout tl = (TableLayout) findViewById(R.id.lstDb);
		
		for (int i=0; i < dbList.names.size(); i++)
		{
            // Create a TableRow and give it an ID
            TableRow tr = new TableRow(this);
            tr.setId(100+i);
            tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));   

            // Create a TextView to house the name of the province
            TextView tv = new TextView(this);
            tv.setId(VIEW_TEXTVIEW_ID + i);
            tv.setText(dbList.names.get(i));
            //labelTV.setTextColor(Color.BLACK);
            tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            tr.addView(tv);

            // Create a TextView to house the value of the after-tax income
            final CheckBox cb = new CheckBox(this);
            cb.setId(VIEW_CHECKBOX_ID + i);
            if (mSelectedDB.contains(dbList.items.get(i)))
            {
            	cb.setChecked(true);
            }
            cb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            cb.setOnClickListener(new OnClickListener()
            {
            	public void onClick(View v)
            	{
            		if (cb.isChecked())
            		{
            			mSelectedDBGroup.add(dbList.names.get(cb.getId()-VIEW_CHECKBOX_ID));
            		}
            		else
            		{
            			mSelectedDBGroup.remove(dbList.names.get(cb.getId()-VIEW_CHECKBOX_ID));
            		}
            		Log.i(MAIN_TAG, "Size = " + mSelectedDBGroup.size());
            	}
            });
            tr.addView(cb);
            // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            
            Button btnOk = (Button) findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new OnClickListener()
            {
            	public void onClick(View v)
            	{
            		savePreferences(1);
            		setContentView(R.layout.main);
            	}
            });
		}
	}*/
    
    public void menuMain()
    {
		//Log.i(MAIN_TAG, "Start menuMain");
        
    	edWord = (EditText) findViewById(R.id.edWord);
    	lstWord = (ListView) findViewById(R.id.lstWord);
    	
    	btnDictionaryManager = (ImageButton) findViewById(R.id.btnDictionaryManager);
    	btnPronounce = (ImageButton) findViewById(R.id.btnPronounce);
    	btnInfo = (ImageButton) findViewById(R.id.btnInfo);
    	btnGoogleTranslate = (ImageButton) findViewById(R.id.btnGoogleTranslate);
    	
		//mLSTCurrentContent = new ArrayList<String>();
    	mLSTCurrentWordId = new ArrayList<Integer>();
    	mLSTCurrentWord = new ArrayList<String>();
    	
    	mAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.customlist);
    	/*
    	 * TODO:
    	 * - no data file -> catch Error;
    	 */
    	showWordlist();
    	edWord.requestFocus();
    	
    	btnGoogleTranslate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuGoogleTranslate();
			}
    	});
    	
    	btnDictionaryManager.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				manageDictionary();
			}
		});
    	
    	btnPronounce.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Log.i(MAIN_TAG,"Start pronounciation ...");
				btnPronounce.setEnabled(false);
		       	String currentWord = edWord.getText().toString().toLowerCase();
		       	String fullPathFilePro = getString(R.string.soundPath) + mDBFile.sourceLanguage + "/" + currentWord + ".wav";
		       	
				Log.d(MAIN_TAG,"fullPathFilePro = " + fullPathFilePro);
				
				File f = new File(fullPathFilePro);
				try {
					if (f.exists())
					{
						Log.i(MAIN_TAG,"Audio file found!");
						MediaPlayer mp = new MediaPlayer();
						if (mp != null)
						{
							mp.setDataSource(fullPathFilePro);
							mp.prepare();
							mp.setLooping(false);
							mp.start();
							while (mp.getCurrentPosition() < mp.getDuration());
							mp.stop();
							mp.release();
							Log.i(MAIN_TAG,"Pronounciation finished!");
						}
						else
						{
							Log.i(MAIN_TAG,"MediaPlayer = null");
						}
					}
					else
					{
						Log.i(MAIN_TAG,"File doesn't exist!!");
					}
				}
				catch (IOException e)
				{
					Log.i(MAIN_TAG,e.toString());
				}
				btnPronounce.setEnabled(true);
			}
		});
    	
    	btnInfo = (ImageButton) findViewById(R.id.btnInfo);
    	btnInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openAbout(getString(R.string.dbPath) + mDBFile.fileName + "/about.html",false);
			}
		});
    	
    	mHandler = new Handler();
    	
    	mUpdateTimeTask = new Runnable()
    	{
    		public void run()
    		{
    			Log.i(MAIN_TAG, "update word list now");
    			edWord.setEnabled(false);
    			showWordlist();
    			edWord.setEnabled(true);
    		}
    	};
    	
	 	//1 ... process event enter word
    	edWord.addTextChangedListener(new TextWatcher()
    	{ 
    		public void afterTextChanged(Editable s) 
    		{
    		    //showWordlist();
                mHandler.removeCallbacks(mUpdateTimeTask);
                mHandler.postDelayed(mUpdateTimeTask, mWaitingTime*1000);
		
    		}
    		
    		public void beforeTextChanged(CharSequence s, int start, int count, int after) 
    		{;} 
    	
    	    public void onTextChanged(CharSequence s, int start, int before, int count) 
    		{;}
    	});

    	lstWord.setOnItemClickListener(new AdapterView.OnItemClickListener() 
    	{
    		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3)
    		{
    			imm.hideSoftInputFromWindow(edWord.getWindowToken(), 0);
    			Intent i = new Intent(v.getContext(), ContentView.class);
    			i.putExtra("word", mAdapter.getItem(arg2));
    			i.putExtra("id",mLSTCurrentWordId.get(arg2));
    			i.putExtra("db", mDBFile.fileName);
    			i.putExtra("dbName",mDBFile.dictionaryName);
    			i.putExtra("style", mDBFile.style);
                startActivityForResult(i, SHOW_CONTENT_CODE);
    			//mEDWord.setText(mAdapter.getItem(arg2));
/*    			if (mStartTime == -1)
    			{
    				mStartTime = SystemClock.currentThreadTimeMillis();
    			}
    			else
    			{
    				
    			}*/
    		}
		});
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (requestCode)
    	{
    		case SHOW_CONTENT_CODE:
            	Log.i(MAIN_TAG,"resultCode = " + resultCode);
        		if (resultCode == RESULT_OK)
        		{
        			/*
        			 * TODO
        			 * what if the dictionary was changed by history viewing????
        			 */
            		String word = data.getStringExtra("word");
            		if (word != null)
            		{
                    	edWord.setText(word);
                    	edWord.setSelection(0, edWord.length());
            		}
        		}
        		break;
    		case SHOW_PREFERENCE_CODE:
    			if (resultCode == RESULT_OK)
    			{
        			mWaitingTime = Integer.parseInt(prefs.getString("waitingTime", "1"));
        			mSaveHistory = prefs.getBoolean("saveHistory", true);
        			Log.i(MAIN_TAG,"Waiting time changed to " + mWaitingTime);
    			}
    			break;
    	}
    }

	public void manageDictionary()
	{
		setContentView(R.layout.dbmanager);
		//LinearLayout ll = (LinearLayout) findViewById(R.id.lstDb);
		ScrollView ll = (ScrollView) findViewById(R.id.lstDb);
		final RadioGroup rg = new RadioGroup(this);
		rg.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
		rg.setOrientation(RadioGroup.VERTICAL);
		//Log.d(MAIN_TAG, "Size of mDBList = " + mDBList.items.size());
		for (int i=0; i < mDBList.items.size(); i++)
		{
            // Create a TextView to house the value of the after-tax income
            RadioButton rb = new RadioButton(this);
            rb.setId(VIEW_RADIO_ID + i);
            rb.setText(mDBList.items.get(i).dictionaryName);
            rb.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            
            rg.addView(rb,i);
            if (mDBFile != null && mDBFile.fileName.equals(mDBList.items.get(i).fileName))
            {
            	rg.check(VIEW_RADIO_ID + i);
            }
            //Log.d(MAIN_TAG,mDBList.items.get(i).path + " |  " + mDBList.items.get(i).fileName);
		}
        ll.addView(rg);
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		setContentView(R.layout.main);
        		int selectedIndex = rg.getCheckedRadioButtonId();
        		if (selectedIndex < 0)
        		{
        			selectedIndex = 0;
        		}
        		//mDBFile.reset();
        		mDBFile = mDBList.items.get(selectedIndex - VIEW_RADIO_ID);
        		//Log.i(MAIN_TAG,"Loaded dictionary " + mDBFile.path + " | " + mDBFile.fileName);
        		savePreferences();
                setContentView(R.layout.main);
        		menuMain();
        	}
        });
        
	}
	
	public void savePreferences()
	{
		Log.d(MAIN_TAG, "Saving default dictionary = " + mDBFile.path + "-" + mDBFile.fileName);
       	SharedPreferences.Editor editor = prefs.edit();
       	editor.putString("defaultDictionary", mDBFile.fileName);
       	editor.putString("defaultDictionaryPath", mDBFile.path);
		editor.commit();
	}
	
	public void loadPreferences()
	{
		boolean found = false;

		mSaveHistory = prefs.getBoolean("saveHistory", true);
		
		String savedDB = prefs.getString("defaultDictionary", ""); //Default dictionary is the first dictionary in the list
		String savedDBPath = prefs.getString("defaultDictionaryPath", ""); //Default dictionary is the first dictionary in the list
		if (savedDB.trim().equals("") || savedDBPath.trim().equals(""))
		{
			Log.i(MAIN_TAG,"Error in loading default dictionary");
			if (mDBList != null && mDBList.items.size() > 0)
			{
				mDBFile = mDBList.items.get(0);
				//Log.d(MAIN_TAG,"Use the first item = " + mDBFile.path + " | filename = " + mDBFile.fileName);
			}
			else
			{
				mDBFile = null;
			}
		}
		else
		{
			//Log.i(MAIN_TAG,"Loaded default dictionary = " + savedDBPath + " - " + savedDB);
			if (mDBList != null && mDBList.items.size() > 0)
			{
				for (DatabaseFile d : mDBList.items)
				{
					if (d.fileName.equals(savedDB) && d.path.equals(savedDBPath))
					{
						mDBFile = d;
						found = true;
						break;
					}
				}
				if (!found)
				{
					//when the database is deleted, set mSelectedDB to the first element of database list
					mDBFile = mDBList.items.get(0);
					Log.i(MAIN_TAG, "Database file is not in list anymore, use the first one of list");
				}
			}
			else
			{
				Log.d(MAIN_TAG,"No database found");
				mDBFile = null;
				/*
				 * TODO
				 * - show "no dictionary" page
				 */
			}
		}
		if (mDBFile != null)
		{
			Log.d(MAIN_TAG,"default path = " + mDBFile.path + " | filename = " + mDBFile.fileName);
		}
		else
		{
			Log.i(MAIN_TAG,"No database found!");
		}
		
		
	}
    public void showWordlist()
    {
		//Log.i(MAIN_TAG, "Start showWordList");

		edWord.setEnabled(false);
		String word = edWord.getText().toString();
		Uri uri = Uri.parse("content://peacemoon.andict.AndictProvider/dict/" + mDBFile.fileName + "/list/" + word);
		try
		{
			Cursor result = managedQuery(uri,null,null,null,null);
			
	        if (result != null)
	        {
	        	int countRow=result.getCount();
	    		Log.i(MAIN_TAG, "countRow = " + countRow);
	    		mLSTCurrentWord.clear();
	    		//mLSTCurrentContent.clear();
	    		mLSTCurrentWordId.clear();
	    		mAdapter.clear();
	        	if (countRow >= 1)
	        	{
	        	   	int indexWordColumn = result.getColumnIndex("word");
	        	   	int indexIdColumn = result.getColumnIndex("id");
	                //int indexContentColumn = result.getColumnIndex("Content");

	                result.moveToFirst();
	        		String strWord;
	        		int intId;

	        		int i = 0;
	                do
	                {
	                	strWord = Utility.decodeContent(result.getString(indexWordColumn));
	                	intId = result.getInt(indexIdColumn);
	                	//strContent = Utility.decodeContent(result.getString(indexContentColumn));
	                    mLSTCurrentWord.add(i,strWord);
	                    mLSTCurrentWordId.add(i,intId);
	                    //mLSTCurrentContent.add(i,strContent);
	            		mAdapter.add(strWord);
	                    i++;
	                } while (result.moveToNext()); 
	            }
	           
	            result.close();
	        }
			//assign result return
			lstWord.setAdapter(mAdapter);
		}
		catch (Exception ex)
		{
			Log.e(MAIN_TAG, "Error = " + ex.toString());
		}
		edWord.setEnabled(true);
    }
}