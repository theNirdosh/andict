package peacemoon.andict;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DictionaryEngine {
	static final private String SQL_TAG = "[Andict - DictionaryEngine]";
	
	private SQLiteDatabase mDB = null;

	private String mDBName;
	private String mDBPath;
	//private String mDBExtension;
	public ArrayList<String> lstCurrentWord = null;
	public ArrayList<String> lstCurrentContent = null;
	//public ArrayAdapter<String> adapter = null;

	public DictionaryEngine()
	{
		lstCurrentContent = new ArrayList<String>();
    	lstCurrentWord = new ArrayList<String>();
	}
	
	public DictionaryEngine(String basePath, String dbName, String dbExtension)
	{
		//mDBExtension = getResources().getString(R.string.dbExtension);
		//mDBExtension = dbExtension;
		lstCurrentContent = new ArrayList<String>();
    	lstCurrentWord = new ArrayList<String>();
    	
		this.setDatabaseFile(basePath, dbName, dbExtension);
	}
	
	public boolean setDatabaseFile(String basePath, String dbName, String dbExtension)
	{
		if (mDB != null)
		{
			if (mDB.isOpen() == true) // Database is already opened
			{
				if (basePath.equals(mDBPath) && dbName.equals(mDBName)) // the opened database has the same name and path -> do nothing
				{
					Log.i(SQL_TAG, "Database is already opened!");
					return true;
				}
				else
				{
					mDB.close();
				}
			}
		}
		
		String fullDbPath="";
		
		try
		{
			fullDbPath = basePath + dbName + "/" + dbName + dbExtension;
			mDB = SQLiteDatabase.openDatabase(fullDbPath, null, SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}
		catch (SQLiteException ex)
		{
			ex.printStackTrace();
			Log.i(SQL_TAG, "There is no valid dictionary database " + dbName +" at path " + basePath);
			return false; 
		}
		
		if (mDB == null)
		{
			return false;
		}
		
		this.mDBName = dbName;
		this.mDBPath = basePath;
		
		Log.i(SQL_TAG,"Database " + dbName + " is opened!");
		
		return true;
	}
	
	public void getWordList(String word)
	{
		String query;
		// encode input
		String wordEncode = Utility.encodeContent(word);

		if (word.equals("") || word == null)
		{
			query = "SELECT id,word FROM " + mDBName + " LIMIT 0,15" ;
		}
		else
		{
		    query = "SELECT id,word FROM " + mDBName + " WHERE  word >= '"+wordEncode+"' LIMIT 0,15";
		}
		//Log.i(SQL_TAG, "query = " + query);
		
	   	Cursor result = mDB.rawQuery(query,null);
	   	
	   	int indexWordColumn = result.getColumnIndex("Word");
        int indexContentColumn = result.getColumnIndex("Content");
        
        if (result != null)
        {
        	int countRow=result.getCount();
    		Log.i(SQL_TAG, "countRow = " + countRow);
    		lstCurrentWord.clear();
    		lstCurrentContent.clear();
        	if (countRow >= 1)
        	{
        	    result.moveToFirst();
        		String strWord = Utility.decodeContent(result.getString(indexWordColumn));
        		String strContent = Utility.decodeContent(result.getString(indexContentColumn));
        	    lstCurrentWord.add(0,strWord);
                lstCurrentContent.add(0,strContent);
                int i = 0;
                while (result.moveToNext()) 
                {
                	strWord = Utility.decodeContent(result.getString(indexWordColumn));
                	strContent = Utility.decodeContent(result.getString(indexContentColumn));
                    lstCurrentWord.add(i,strWord);
                    lstCurrentContent.add(i,strContent);
                    i++;
                } 
               
            }
           
            result.close();
        }
		
	}

	public Cursor getCursorWordList(String word)
	{
		String query;
		// encode input
		String wordEncode = Utility.encodeContent(word);

		if (word.equals("") || word == null)
		{
			query = "SELECT id,word FROM " + mDBName + " LIMIT 0,15" ;
		}
		else
		{
		    query = "SELECT id,content,word FROM " + mDBName + " WHERE  word >= '"+wordEncode+"' LIMIT 0,15";
		}
		//Log.i(SQL_TAG, "query = " + query);
		
	   	Cursor result = mDB.rawQuery(query,null);
	   	
	   	return result;		
	}

	public Cursor getCursorContentFromId(int wordId)
	{
		String query;
		// encode input
		if (wordId <= 0)
		{
			return null;
		}
		else
		{
			query = "SELECT id,content,word FROM " + mDBName + " WHERE Id = " + wordId ;
		}
		//Log.i(SQL_TAG, "query = " + query);
	   	Cursor result = mDB.rawQuery(query,null);
	   	
	   	return result;		
	}

	public Cursor getCursorContentFromWord(String word)
	{
		String query;
		// encode input
		if (word == null || word.equals(""))
		{
			return null;
		}
		else
		{
			query = "SELECT id,content,word FROM " + mDBName + " WHERE word = '" + word + "' LIMIT 0,1";
		}
		//Log.i(SQL_TAG, "query = " + query);
		
	   	Cursor result = mDB.rawQuery(query,null);
	   	
	   	return result;		
	}
	
	public void closeDatabase()
	{
		mDB.close();
	}
	
	public boolean isOpen()
	{
		return mDB.isOpen();
	}
	
	public boolean isReadOnly()
	{
		return mDB.isReadOnly();
	}

}
