package peacemoon.andict;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AndictProvider extends ContentProvider {

	public static final String PROVIDER_TAG = "[AndictProvider]";
	public static final String PROVIDER_NAME = "peacemoon.andict.AndictProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);
	
	public static final String _ID = "id";
	public static final String COLUMN_WORD = "Word";
	public static final String COLUMN_CONTENT = "Content";

	private static final int CODE_LIST_EMPTY = 0;
	private static final int CODE_LIST = 1;
	private static final int CODE_CONTENT_FROM_ID = 2;
	private static final int CODE_CONTENT_FROM_WORD = 3;
	private static final int CODE_GO_BACK = 4;
	private static final int CODE_GO_FOWARD = 5;
	
	private String mDBExtension;
	private String mDBPath;
	private String mCurrentDB = null;
	
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "dict/*/list/", CODE_LIST_EMPTY);
		uriMatcher.addURI(PROVIDER_NAME, "dict/*/list/*", CODE_LIST);
		uriMatcher.addURI(PROVIDER_NAME, "dict/*/contentId/#", CODE_CONTENT_FROM_ID);
		uriMatcher.addURI(PROVIDER_NAME, "dict/*/contentWord/*", CODE_CONTENT_FROM_WORD);
		uriMatcher.addURI(PROVIDER_NAME, "dict/*/back/*", CODE_GO_BACK);
		uriMatcher.addURI(PROVIDER_NAME, "dict/*/foward/*", CODE_GO_FOWARD);
	}

	private DictionaryEngine mDBEngine;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(uri))
		{
			case CODE_LIST:
			case CODE_LIST_EMPTY:
				return "vnd.android.cursor.dir/vnd.andict.wordlist";
			case CODE_GO_BACK:
			case CODE_GO_FOWARD:
			case CODE_CONTENT_FROM_WORD:
			case CODE_CONTENT_FROM_ID:
				return "vnd.android.cursor.item/vnd.andict.wordcontent";
	        default:
	            throw new IllegalArgumentException("AndictProvider - Unsupported URI: " + uri);        
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
//		String word = uri.getPathSegments().get(3);

        mDBExtension = getContext().getResources().getString(R.string.dbExtension);
        mDBPath = getContext().getResources().getString(R.string.dbPath);

        mDBEngine = new DictionaryEngine();
	
		Log.i(PROVIDER_TAG,">>> AndictProvider is ready <<<");
        return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		String strDB = uri.getPathSegments().get(1);

		if (mCurrentDB == null || !mCurrentDB.equals(strDB))
		{
			mCurrentDB = strDB;
			mDBEngine.setDatabaseFile(mDBPath, strDB, mDBExtension);
		}
		if (mDBEngine == null)
		{
        	Log.e(PROVIDER_TAG,"Can not create database engine");
			return null;
		}
		
		String word;
		int wordId;
		Cursor c;
		
		switch (uriMatcher.match(uri))
		{
			case CODE_LIST_EMPTY:
				//Log.d(PROVIDER_TAG,"LIST_EMPTY");
				
				c = mDBEngine.getCursorWordList("");
				c.setNotificationUri(getContext().getContentResolver(), uri);
				
				return c;
				
			case CODE_LIST:
				word = uri.getPathSegments().get(3);
				
				//Log.d(PROVIDER_TAG,"LIST word = " + word);
				
				c = mDBEngine.getCursorWordList(word);
				c.setNotificationUri(getContext().getContentResolver(), uri);
				
				return c;
				
			case CODE_CONTENT_FROM_ID:
				wordId = Integer.parseInt(uri.getPathSegments().get(3));
				
				//Log.d(PROVIDER_TAG,"CONTENT_FROM_ID wordId = " + wordId);
				
				c = mDBEngine.getCursorContentFromId(wordId);
				c.setNotificationUri(getContext().getContentResolver(), uri);
				
				return c;
				
			case CODE_CONTENT_FROM_WORD:
				word = uri.getPathSegments().get(3);
					
				//Log.d(PROVIDER_TAG,"CONTENT_FROM_WORD word = " + word);
				
				c = mDBEngine.getCursorContentFromWord(word);
				c.setNotificationUri(getContext().getContentResolver(), uri);
				
				return c;
				
	        default:
	            //throw new IllegalArgumentException("AndictProvider - Unsupported URI: " + uri);
	        	Log.e(PROVIDER_TAG,"AndictProvider - Unsupported URI: " + uri);
	        	return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
