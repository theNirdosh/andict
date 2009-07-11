package com.tdict;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

public class TDict extends Activity {
	
	static final private int MENU_ITEM = Menu.FIRST;

	private static final String TAG = "[TDict]";
    private static final String mimetype = "text/html";
    private static final String encoding = "UTF-8";
	
	private SQLiteDatabase db;
	
	private ImageButton btnClear = null;
	private ImageButton btnSpeak = null;

	private EditText input = null;
	private WebView contentView = null;
	private ListView listView = null;
	private RadioGroup radiogroup=null;
	private LinearLayout layout=null;
	
	ArrayList<String> listCurrentWord = null;
	ArrayList<String> listCurrentContent = null;
	ArrayList<String> listFileDb = null;

	static String selectedDb = null;
	
	ArrayAdapter<String> adapter = null;
	//SeparatedListAdapter adapter = null;
	
    Menu menu = null;	

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
 	  // Added extra items to make sure there's more than six to 
 	  // force the extended menu to appear.
 	  menu.add(groupId, menuItemId+0, menuItemOrder, "Dictionary").setIcon(R.drawable.dictmenu);
 	  menu.add(groupId, menuItemId+2, menuItemOrder, "Google Translate").setIcon(R.drawable.google);
 	  menu.add(groupId, menuItemId+1, menuItemOrder, "Options").setIcon(R.drawable.manage);
 	  menu.add(groupId, menuItemId+3, menuItemOrder, "About").setIcon(R.drawable.info);
 	 
 	  return true;
 	}

    // process event select Menu
 	public boolean onOptionsItemSelected(MenuItem item) {
 		  super.onOptionsItemSelected(item);
 				
 		  // Find which menu item has been selected
 		  switch (item.getItemId()) {
 			  case (MENU_ITEM+0): 
 		      {	 
 		    	// setting for Dictionary
 	         	loadForDictionary(false);
 		    	break;
 		      }
 		      case (MENU_ITEM+1): 
 		      {
 		    	  // setting for MangeDictinary
 		    	  loadForManageDictionary();
 		    	  break;
 		      }
 		      case (MENU_ITEM+2): 
 		      {
 		    	  // load for GoogleTranslate	
 			  	  //loadForGoogleTranslate();
 			  	  break;
 		      }
 		
 		      case (MENU_ITEM+3): 
 		      {	 
 		    	  // load for About
 		    	  //loadForAbout();
 		    	  break;
 		      }
 		  }
 		  
 		   return true;
 		}
 		
 	// create menu context
 	@Override
 	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
 	  super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Select a dictionary");  
		ArrayList<String> listFileDb=getDataFileArray();
		for (int i=0;i<listFileDb.size();i++)
		{	  
			menu.add(0, 0, i, listFileDb.get(i));
		}

 	}
 	
 	// process for event select item of Manage Dictionary 
 	@Override  
       public boolean onContextItemSelected(MenuItem aItem) {  
            input.setText(aItem.getTitle());
            return true; /* true means: "we handled the event". */  
         
     }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        loadForDictionary(true);
    }
	// binding data for ListView
	public void setDataForListView(String nameDictionary)
	{
		Log.d(TAG,"nameDictionary = " + nameDictionary);
		
		String stringQuery ="SELECT Content,Word FROM "+nameDictionary+" LIMIT 0,15";
		
	 	listView.setVisibility(View.VISIBLE);
	 	contentView.setVisibility(View.INVISIBLE);

	   	Cursor result = db.rawQuery(stringQuery,null);

	   	int indexWordColumn = result.getColumnIndex("Word");
        int indexContentColumn = result.getColumnIndex("Content");

		if (result != null)
        {
        	int countRow=result.getCount();
        	
        	Log.d(TAG,"countRow = " + countRow);
        	
        	if (countRow >= 1)
        	{
        		adapter.clear();
        		listCurrentContent = new ArrayList<String>();
    	    	listCurrentWord = new ArrayList<String>();
        	    result.moveToFirst();
        		String word = Utility.decodeContent(result.getString(indexWordColumn));
        		String content = Utility.decodeContent(result.getString(indexContentColumn));
        		
                while (result.moveToNext()) 
                {
                	word = Utility.decodeContent(result.getString(indexWordColumn));
                	content = Utility.decodeContent(result.getString(indexContentColumn));
                    listCurrentWord.add(word);
                    listCurrentContent.add(content);
                    adapter.add(word);
                } 
                
                Log.d(TAG,"listCurrentWord.size() = " + listCurrentWord.size());
                Log.d(TAG,"listCurrentContent.size() = " + listCurrentContent.size());

                listView.setAdapter(adapter);
            }
           
       }
	 }

	public void setDataForListViewChangeWord(String nameDictinary,String valueInput)
	{
		// encode input  
		String valueEncode=Utility.encodeContent(valueInput);
		// query in database	
		String stringQuery=null;
		if (valueInput!=null&&!valueInput.equals(""))
		{
			
		    stringQuery="SELECT Content,Word FROM "+nameDictinary+" WHERE  word>='"+valueEncode+"' and word<='"+valueEncode+"zzzz' LIMIT 0,15";
	 	  
		}
		else
		{
			stringQuery="SELECT Content,Word FROM "+nameDictinary+" LIMIT 0,15" ;
		}
		//assign result return
	   	Cursor result = db.rawQuery(stringQuery,null);
	   	int indexWordColumn = result.getColumnIndex("Word");
        int indexContentColumn = result.getColumnIndex("Content");
        if (result != null)
        {
        	int countRow=result.getCount();
        	if (countRow>=1)
        	{
        		adapter.clear();
        		
        		listCurrentContent = new ArrayList<String>();
    	    	listCurrentWord = new ArrayList<String>();
        	    result.moveToFirst();
        		String word = Utility.decodeContent(result.getString(indexWordColumn));
        		String content = Utility.decodeContent(result.getString(indexContentColumn));
        	    listCurrentWord.add(0,word);
                listCurrentContent.add(0,content);
                int i = 0;
                    while (result.moveToNext()) 
                    {
                    	word = Utility.decodeContent(result.getString(indexWordColumn));
                    	content = Utility.decodeContent(result.getString(indexContentColumn));
                        listCurrentWord.add(i,word);
                        listCurrentContent.add(i,content);
                        adapter.add(word);
                        i++;
                    } 
               
            }
            listView.setAdapter(adapter);
			Log.d(TAG,"adapter.size = " + adapter.getCount());
       }
	 }
	
	// Method to get Array File Database
	public ArrayList<String> getDataFileArray()
	{
		ArrayList<String> list=new ArrayList<String>();
		String pathFileData=getResources().getString(R.string.path_file_data);
		String formatFileData=getResources().getString(R.string.format_file_data);
		
		File dataDirectory = new File(pathFileData);
        File[] listFile=dataDirectory.listFiles();
        if (listFile!=null && listFile.length > 0)
	        for (File currentFile : listFile)
	        {
		        if (currentFile.isFile()&&currentFile.getName().contains(formatFileData))
		        {
		        	String nameFullFile=currentFile.getName();
		          	int index=nameFullFile.indexOf(formatFileData);
		          	String nameFile=nameFullFile.substring(0, index);
		          	list.add(nameFile);
		        }
	        }
        
        return list;			
	}
	
	// process event click button clear 
	public void clearData() 
    {
    	input.setText("");

    	contentView.setVisibility(View.INVISIBLE);
    	
    	btnSpeak.setEnabled(false);
    	
   		setDataForListView(selectedDb);
    }

	public void loadForDictionary(boolean isInit)
	{
		 	Log.i(TAG,"Start loadForDictionary ...");

		 	setContentView(R.layout.main);

		 	contentView = (WebView) findViewById(R.id.contentView);
		 	contentView.setVisibility(View.INVISIBLE);
		 	
		 	input = (EditText) findViewById(R.id.word);
		 	
		 	listView = (ListView) findViewById(R.id.listResult);

	    	//adapter = new SeparatedListAdapter(this);
        	adapter = new ArrayAdapter<String>(getApplicationContext(),	R.layout.todolist_item);
		 	
	    	btnClear = (ImageButton) findViewById(R.id.btnClear);
	        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

	       	// Open Database
	       	ArrayList<String> listFileDb = getDataFileArray();
	       	if (listFileDb != null && listFileDb.size() > 0)
	       	{
	       		Log.d(TAG, "listFileDb.size = " + listFileDb.size());
	   	       	String defaultDbName = listFileDb.get(0);

	   	       	Log.d(TAG, "defaultDbName = " + defaultDbName);
	   	       	
	   	       	if(isInit)
	   	       	{
	   	       		selectedDb=defaultDbName;
		   	       	Log.d(TAG, "selectedDb = " + selectedDb);
	   	       	}
		       	String pathFileData = getResources().getString(R.string.path_file_data);
		       	String fullPathFile = pathFileData+"/"+selectedDb+".db";

		       	Log.d(TAG, "fullPathFile = " + fullPathFile);
		       	
		       	db = SQLiteDatabase.openDatabase(fullPathFile, null, 0);
		        // Initialize for List View
		    	setDataForListView(selectedDb);
	       	}
	       	else
	       	{
	       		input.setEnabled(false);
	       		contentView.setVisibility(View.VISIBLE);
	       		String message=getResources().getString(R.string.no_database);
	       		contentView.loadData(message,mimetype,encoding);
	       		
	       	}
		 	Log.i(TAG,"listFileDb initialized....");
	       	
		 	
		 	//PROCESS EVENTS
		 	//1 ... process event enter word
	    	input.addTextChangedListener(new TextWatcher()
	    	{ 
	    		public void afterTextChanged(Editable s) 
	    		{
	    		   String valueInput=input.getText().toString();
	    		   // Search addresses
	    		   setDataForListViewChangeWord(selectedDb,valueInput);
	    					
	    		   listView.setVisibility(View.VISIBLE);
	    		   contentView.setVisibility(View.INVISIBLE);
	    		  // currentWord=input.getText().toString();
	    		} 
	    		public void beforeTextChanged(CharSequence s, int start, int count, int after) 
	    		{;} 
	    	
	    	    public void onTextChanged(CharSequence s, int start, int before, int count) 
	    		{;}
	    	});

	    	//2........ process event select  item in ListView
	    	listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
	    		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
    				String word=(String) adapter.getItem(arg2);
    				
					Log.d(TAG, "word in Adapter " + arg2 + "= "+ adapter.getItem(arg2));
					Log.d(TAG, "word in Array " + arg2 + "= "+ listCurrentWord.get(arg2));
    				
    				String htmldata = "<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n";
    				htmldata += "<head><style type=\"text/css\">* {margin:0px; padding:0px;}\n ul {padding:1px; margin-left:20px;}\n li{padding:0px;}</style></head>\n";
    				htmldata += "<body><font face=\"Arial\">";

    				Log.d(TAG,"size of listCurrentContent = " + listCurrentContent.size());
    				htmldata += listCurrentContent.get(arg2).toString();
					
    				htmldata += "</font></body></html>";

    				input.setText(word);

	    	        contentView.setVisibility(View.VISIBLE);
	    	        listView.setVisibility(View.INVISIBLE);

	    	        contentView.loadDataWithBaseURL (null, htmldata, mimetype, encoding,"about:blank");
	    		}
	        });

	    	//3...........event click button clear data
	        btnClear.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View view)
				{
					clearData();
				}
			});
	    	
	}
	// Load For ManageDictionary
	public void loadForManageDictionary()
	{
		 // create layout for display 
		 ScrollView sv = new ScrollView(this); 
		 layout=new LinearLayout(getApplicationContext());
	     //add content
     	 radiogroup= new RadioGroup(this);
     	 listFileDb=getDataFileArray();
     	 int indexSelected=-1;
     	 if (listFileDb!=null && listFileDb.size()>0)
     	 {
	      	  for (int i=0;i<listFileDb.size();i++)
	      	  {
	      		  
	      		  RadioButton radio=new RadioButton(this);
	      		  radio.setText(listFileDb.get(i).toString());
	      		  radio.setId(i);
				  if (selectedDb!=null&&selectedDb.equals(listFileDb.get(i)))
					  indexSelected=i;
				  radiogroup.addView(radio, i);
	      		  
	          }
	      	  radiogroup.check(indexSelected);
	      	  //layout
         	  layout.addView(radiogroup);
     	 }
     	 sv.setVerticalScrollBarEnabled(true); 
     	 sv.addView(layout); 
     	 AlertDialog.Builder dialog=new AlertDialog.Builder(TDict.this).setIcon(
	  				R.drawable.manage).setTitle("Select a dictionary")
	  				.setView(sv).setPositiveButton("OK",
	  						new DialogInterface.OnClickListener() {
	  							public void onClick(DialogInterface dialog,
	  									int whichButton) {
	  								if (listFileDb!=null && listFileDb.size()>0)
	  								{
		  								/* User clicked Yes so do some stuff */
		  								int selectedIndex = radiogroup.getCheckedRadioButtonId();
		  								//selectedDb
		  								if(selectedIndex >= 0)
		  								{
		  									selectedDb = listFileDb.get(selectedIndex);
		  									input.setText("");
		  									String pathFileData = getResources().getString(R.string.path_file_data);
		  							       	String fullPathFile = pathFileData+"/"+selectedDb+".db";
		  							       	db = SQLiteDatabase.openDatabase(fullPathFile, null, 0);
		  									setDataForListView(selectedDb);
		  								}
	  								}
	  								
	  							}
	  						}).setNegativeButton("Cancel",
	  						new DialogInterface.OnClickListener() {
	  							public void onClick(DialogInterface dialog,
	  									int whichButton) {
	  								/* User clicked No so do some stuff */
	  				
	  							}
	  						}).setOnCancelListener(new OnCancelListener() {

	  					public void onCancel(DialogInterface arg0) {

	  						;
	  					}
	  				});
   	  dialog.show();
   	  
	}	 
}