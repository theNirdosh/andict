package peacemoon.andict;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

public class ContentView extends Activity {

	static final private String CONTENT_TAG = "[Andict - Content]";

	static final private int SHOW_HISTORY_CODE = 0;
	static final private int SHOW_FAVOURITE_CODE = 1;

	static final private int MENU_BACK = Menu.FIRST;
	static final private int MENU_FOWARD = Menu.FIRST+1;
	static final private int MENU_LIST = Menu.FIRST+2;
	static final private int MENU_HISTORY = Menu.FIRST+3;
	
	private static final String MIMETYPE = "text/html";
    private static final String ENCODING = "UTF-8";

    private WebView wvContent = null;
    private ImageButton btnGoBack = null;
    private ImageButton btnGoForward = null;
    private ImageButton btnShowHistory = null;
    private ImageButton btnAddFavourite = null;
    
    private String mCurrentWord;
    private String mSelectedDB;
    //private String mSelectedDBName;
    private String mContentStyle;
    private int mCurrentWordId;
    private int mCurrentHistoryIndex = -1;
    
    Menu menu = null;

	private ArrayList<String> mWordHistory = null;

	private SharedPreferences prefs;
	
	private ProgressDialog pd = null;
	
	// create Menu for Program
    @Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 	  super.onCreateOptionsMenu(menu);

 	  Log.i(CONTENT_TAG, "menu drawed!!");
 	  // Group ID
 	  int groupId = 0;

 	  // The order position of the item
 	  int menuItemOrder = Menu.NONE;
 	  this.menu=menu;
 	  // Added extra items to make sure there's more than six to 
 	  // force the extended menu to appear.
	  menu.add(groupId, MENU_BACK, menuItemOrder, R.string.menuGoBack);
	  menu.add(groupId, MENU_FOWARD, menuItemOrder, R.string.menuGoForward);
 	  menu.add(groupId, MENU_LIST, menuItemOrder, R.string.menuList);
 	  menu.add(groupId, MENU_HISTORY, menuItemOrder, R.string.menuHistory);
 	  
 	  return true;
 	}

	// process event select Menu
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
				
		// Find which menu item has been selected
		switch (item.getItemId()) {
			case (MENU_BACK): 
			{	 
				goBack();
				break;
			}
			case (MENU_FOWARD): 
			{
				goForward();
				break;
			}
			case (MENU_LIST): 
			{
				//menuDictionaryManager();
				menuList();
				break;
			}
			case (MENU_HISTORY):
			{
				break;				
			}
		}
	    return true;
	}

	public void menuList()
	{
		/*
		 * TODO
		 * - save history list
		 */
		Intent i = new Intent();
		i.putExtra("word", mCurrentWord);
		setResult(RESULT_OK,i);
		finish();
	}
	
	public void goBack()
	{
		Log.i(CONTENT_TAG,"go back");
		String content = getHistoryContent("back");
		showContent(content);
	}

	public void goForward()
	{
		Log.i(CONTENT_TAG,"go foward");
		String content = getHistoryContent("back");
		showContent(content);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		saveHistoryToPreferences();
	}
	
	public void saveHistoryToPreferences()
	{
		if (prefs.getBoolean("saveHistory", true) && mWordHistory != null && mWordHistory.size() >= 1)
		{
			StringBuilder sbHistory = new StringBuilder();
			for (String item : mWordHistory)
			{
				sbHistory.append(item);
				sbHistory.append(",");
			}
			
			String strHistory = sbHistory.substring(0, sbHistory.length()-1);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("history", strHistory);
			editor.commit();
			//Log.i(CONTENT_TAG,"history = " + strHistory);
			Log.i(CONTENT_TAG,"History saved!");
		}
	}
	
	public void loadHistoryFromPreferences()
	{
        if (prefs.getBoolean("saveHistory", true))
		{
			String strHistory = prefs.getString("history", "");
			Log.i(CONTENT_TAG, "History loaded");
			if (strHistory != null && !strHistory.equals(""))
			{
				mWordHistory = new ArrayList<String>(Arrays.asList(strHistory.split(",")));
			}
			else
			{
				if (mWordHistory == null)
				{
			        mWordHistory = new ArrayList<String>();
				}
				else
				{
					mWordHistory.clear();
				}
			}
		}
		else
		{
			if (mWordHistory == null)
			{
		        mWordHistory = new ArrayList<String>();
			}
			else
			{
				mWordHistory.clear();
			}
		}
	}
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE); 
	    
	    setContentView(R.layout.content);
	    //Log.i(CONTENT_TAG,".................onCreate.................");
	    Intent i = this.getIntent();

	    int wordId = i.getIntExtra("id", -1);
	    mCurrentWord = i.getStringExtra("word");
	    mSelectedDB = i.getStringExtra("db");
	    mContentStyle = i.getStringExtra("style");
	    //Log.i(CONTENT_TAG,"Style from intent = " + mContentStyle);
	    //Log.d(CONTENT_TAG,"current word = " + mCurrentWord);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        loadHistoryFromPreferences();
        
        wvContent = (WebView) findViewById(R.id.wvContent);
    	initWebview();
    	String content = getContentById(wordId);
    	showContent(content);
    	
    	btnShowHistory = (ImageButton) findViewById(R.id.btnShowHistory);
    	btnShowHistory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(CONTENT_TAG, "Start showing history..");
/*				Intent i = new Intent(v.getContext(), HistoryView.class);
				HistoryList hl = new HistoryList(mWordHistory);
				i.putExtra("history", hl);*/
		        startActivityForResult(new Intent(v.getContext(),HistoryView.class), SHOW_HISTORY_CODE);
				//showHistory();
			}
		});
    	
    	btnGoBack = (ImageButton) findViewById(R.id.btnGoBack);
    	btnGoBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(CONTENT_TAG, "Start going back");
				goBack();
			}
		});
    	
    	btnGoForward = (ImageButton) findViewById(R.id.btnGoForward);
    	btnGoForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(CONTENT_TAG, "Start going forward");
				goForward();
			}
		});
    	
    	btnAddFavourite = (ImageButton) findViewById(R.id.btnAddFavourite);
    	btnAddFavourite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast toast = Toast.makeText(ContentView.this, R.string.messageWordAddedToFarvourite, Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	switch (requestCode)
    	{
    		case SHOW_HISTORY_CODE:
            	Log.i(CONTENT_TAG,"resultCode = " + resultCode);
            	if (resultCode == RESULT_OK) // cleared
            	{
            		if (data == null)
            		{
            			//loadHistoryFromPreferences();
            			mWordHistory.clear();
            			Log.i(CONTENT_TAG,"History cleared");
            		}
            		else
            		{
            			int id = data.getIntExtra("wordId", 0);
            			String dict = data.getStringExtra("dict");
            			Log.i(CONTENT_TAG,"id = " + id + " | dict = " + dict);
            			if (id > 0 && dict != null)
            			{
            				mSelectedDB = dict;
            				String content = getContentById(id);
            				//initWebview();
            				showContent(content);
            			}
            		}
            	}
        		break;
    		case SHOW_FAVOURITE_CODE:
    			break;
    	}
    }
    
/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
	        // When the user center presses, let them pick a contact.
	    	Log.d(CONTENT_TAG,"Backkey pressed !!!");
	    	if (mWordHistory != null && mWordHistory.size() > 1 && mCurrentHistoryIndex != 0)
	    	{
	    		String content = getHistoryContent("back");
	    		if (content == null) // end Activity now
	    		{
	    	    	menuList();
	    		}
	    		else // go back to previous word
	    		{
	    			showContent(content);
	    		}
	    	}
	    	else
	    	{
	    		menuList();
	    	}
	    	return true;
        }
        return false;
    }*/
    

    public void initWebview()
    {
    	setContentView(R.layout.content);
    	wvContent = (WebView) findViewById(R.id.wvContent);
	 	wvContent.setBackgroundColor(Color.argb(255, 0, 0, 0));

        wvContent.setWebViewClient(new WebViewClient() 
        {
        	public void onPageFinished(WebView view, String url)
        	{
        		if (pd != null)
        		{
        			pd.dismiss();
        			pd = null;
        		}
        	}
        	
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url)
        	{
        		Log.i(CONTENT_TAG,"WebView link clicked; url = " + url);
        		try
        		{
            		String arrUrlPart[] = url.split("://");
            		
            		if (arrUrlPart[0].equals("entry"))
            		{
            			String content = getContentByWord(arrUrlPart[1]);
            			showContent(content);
            		}
            		else if (arrUrlPart[0].equals("http"))
            		{
            	         try {
                             /*Intent i = new Intent();

                             ComponentName comp = new ComponentName(
                                              "com.google.android.browser",
                                                     "com.google.android.browser.BrowserActivity");
                             i.setComponent(comp);
                             i.setAction("android.intent.action.VIEW");
                             i.addCategory("android.intent.category.BROWSABLE");
                             ContentURI uri = new ContentURI(url);
                             i.setData(uri);
                             startSubActivity(i, 2);*/
            	        	 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));             	        	 
            	         } catch (Exception ex) {
                             // TODO Auto-generated catch block
                             ex.printStackTrace();
            	         }             			
            		}
        		}
        		catch (Exception ex)
        		{
        			ex.printStackTrace();
        		}
        		return true;
        	}
        });
    }
    
    public String getHistoryContent(String type)
    {
		String currentTerm = mSelectedDB + ":" + mCurrentWordId + ":" + mCurrentWord;

		Log.i(CONTENT_TAG,"currentTerm = " +currentTerm);
		if (mWordHistory == null || mWordHistory.isEmpty())
		{
			return null;
		}
		int pos = mWordHistory.indexOf(currentTerm);
		Log.i(CONTENT_TAG,"pos = " + pos);
		if (pos <= 0)
		{
			pos = mWordHistory.size();
		}
		String item = null;
		String searchTerm;
		
		if (type.equals("back"))
		{
			try
			{
				for (int i = pos-1; i >= 0 ; i--)
				{
					searchTerm = mWordHistory.get(i);
					searchTerm = searchTerm.substring(0,searchTerm.indexOf("::"));
					Log.i(CONTENT_TAG,"item = " + mWordHistory.get(i) + " - searchTerm = " + searchTerm);
					if (searchTerm.equals(mSelectedDB))
					{
						item = mWordHistory.get(i);  
						mCurrentHistoryIndex = i;
						break;
					}
				}
			}
			catch (Exception ex)
			{
				Log.i(CONTENT_TAG,"Error in finding history entry");
			}
			
		}
		else
		{
			try
			{
				for (int i = pos; i < mWordHistory.size() ; i++)
				{
					searchTerm = mWordHistory.get(i);
					searchTerm = searchTerm.substring(0,searchTerm.indexOf("::"));
					Log.i(CONTENT_TAG,"item = " + mWordHistory.get(i) + " - searchTerm = " + searchTerm);
					if (searchTerm.equals(mSelectedDB))
					{
						item = mWordHistory.get(i);  
						mCurrentHistoryIndex = i;
						break;
					}
				}
			}
			catch (Exception ex)
			{
				Log.i(CONTENT_TAG,"Error in finding history entry");
			}
		}
		if (item != null) // found previous item
		{
			Log.i(CONTENT_TAG,"item index = " + mCurrentHistoryIndex);
			String arrPart[] = item.split(":");
			
			Uri uri = Uri.parse("content://peacemoon.andict.AndictProvider/dict/" + arrPart[0] + "/contentId/" + arrPart[1]);

			Log.i(CONTENT_TAG,"History uri = " + uri.toString());
			Cursor result = managedQuery(uri,null,null,null,null);
			
	    	String content;
	        if (result != null)
	        {
	        	result.moveToFirst();
	        	content = Utility.decodeContent(result.getString(result.getColumnIndex("content")));
	        	
		    	content = formatContent(content);

		    	mSelectedDB = arrPart[0];
		    	mCurrentWordId = Integer.parseInt(arrPart[1]);
		    	mCurrentWord = arrPart[2];
	    		return content;
	        }
	        else
	        {
	        	return null;
	        }
		}
		else
		{
			return null;
		}
    }
    
    public String getContentById(int id)
    {
		Uri uri = Uri.parse("content://peacemoon.andict.AndictProvider/dict/" + mSelectedDB + "/contentId/" + id);

		Cursor result = managedQuery(uri,null,null,null,null);
		
    	String content;
        if (result != null)
        {
        	result.moveToFirst();
        	content = Utility.decodeContent(result.getString(result.getColumnIndex("content")));
        	mCurrentWordId = result.getInt(result.getColumnIndex("id"));
        	mCurrentWord = result.getString(result.getColumnIndex("word"));
        }
        else // Word not found
        {
        	content = getString(R.string.errorWordNotFound);
        	mCurrentWordId = -1;
        	mCurrentWord = "";
        }
        content = formatContent(content);
                
        return content;
    }
    
    public String getContentByWord(String word)
    {
		Uri uri = Uri.parse("content://peacemoon.andict.AndictProvider/dict/" + mSelectedDB + "/contentWord/" + word);

		Log.i(CONTENT_TAG,"uri = " + uri.toString());
		Cursor result = managedQuery(uri,null,null,null,null);
		
    	String content;
        if (result != null && result.getCount() > 0)
        {
        	result.moveToFirst();
        	content = Utility.decodeContent(result.getString(result.getColumnIndex("content")));
        	mCurrentWordId = result.getInt(result.getColumnIndex("id"));
        	mCurrentWord = result.getString(result.getColumnIndex("word"));
        }
        else
        {
        	content = getString(R.string.errorWordNotFound) + word;
        	mCurrentWordId = -1;
        	mCurrentWord = "";
        }
        content = formatContent(content);
        
        return content;
    }

    public void saveHistory()
    {
		String item = mSelectedDB + "::" + mCurrentWordId + "::" + mCurrentWord;
		if (mWordHistory.indexOf(item) == -1 && mCurrentWordId != -1) // new item
		{
			mWordHistory.add(item);
			mCurrentHistoryIndex = mWordHistory.size();
			
			if (menu != null)
			{
				menu.findItem(MENU_FOWARD).setEnabled(false);
				if (mWordHistory.size() == 1)
				{
					menu.findItem(MENU_BACK).setEnabled(false);
				}
			}
			//Log.i(CONTENT_TAG,"new item added " + item);
		}
    }
        
    public String formatContent(String content)
    {
		StringBuilder htmlData = new StringBuilder();
		htmlData.append("<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n");
		if (mContentStyle != null && !mContentStyle.equals(""))
		{
			htmlData.append("<head><style type=\"text/css\">"+mContentStyle+"</style></head>\n");
		}
		htmlData.append("<body><font face=\"Arial\">");

		htmlData.append(content);
		
		htmlData.append("</font></body></html>");
		
		return htmlData.toString();
    }
    
    public void showContent(String content)
    {
    	if (content != null)
    	{
    		pd = ProgressDialog.show(this, "Working..", "Loading content", true,false);    		
    		saveHistory();
            wvContent.loadDataWithBaseURL (null, content, MIMETYPE, ENCODING,"about:blank");
    	}
    }


}
