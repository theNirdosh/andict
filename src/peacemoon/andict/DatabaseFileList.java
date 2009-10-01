package peacemoon.andict;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;

import android.util.Log;

public class DatabaseFileList {
	static final private String FILELIST_TAG = "[Andict - DatabaseFileList]";
	
	public ArrayList<DatabaseFile> items;
	
	//private String mDBPath;
	//private String mDBExtension;

	public DatabaseFileList(String dbPath, String dbExtension)
	{
		//mDBPath = dbPath;
		//mDBExtension = dbExtension;
		items = new ArrayList<DatabaseFile>();
		
		getDatabaseFileList(dbPath,dbExtension);
	}

	private void getDatabaseFileList(String dbPath, String dbExtension)
	{
		
		items.clear();

		File dataDirectory = new File(dbPath);
		if (!dataDirectory.exists()) //Data directory doesn't exist, create it
		{
			if (!dataDirectory.mkdirs())
			{
				Log.i(FILELIST_TAG, "Can not create directory on sdcard");
			}
			else
			{
				Log.i(FILELIST_TAG, "Data directory was created on sdcard");
			}
		}
        FileFilter ffDir = new FileFilter()
        {
        	public boolean accept(File f)
        	{
        		return f.isDirectory();
        	}
        };

        File[] lstDirectory = dataDirectory.listFiles(ffDir);
        /*final String ext = dbExtension;
        
        FileFilter ffData = new FileFilter()
        {
        	public boolean accept(File f)
        	{
        		return f.getName().toLowerCase().endsWith(ext);
        	}
        };*/
        
        if (lstDirectory != null && lstDirectory.length > 0)
        {
	        for (File currentDirectory : lstDirectory)
	        {
	            DatabaseFile db = new DatabaseFile();
	        	String path = currentDirectory.getAbsolutePath() + "/" + currentDirectory.getName();
	        	//Log.i(FILELIST_TAG,"Filelist path = " + path);
		        	
          		db.fileName = currentDirectory.getName();
          		db.path = currentDirectory.getPath();
          		//Log.i(FILELIST_TAG,"fileName = " + db.fileName + " | path = " + db.path);
	          	File ifoFile = new File(path + ".ifo");
	          	
          		if (ifoFile.exists())
	          	{
	          		
	          		String data;
	          		String arrData[] = null;
	          		try
	          		{
			          	BufferedReader brIfoFile = new BufferedReader(new FileReader(ifoFile));
			          	while (brIfoFile.ready())
			          	{
				          	data = brIfoFile.readLine();
				          	arrData = data.split("=");
				          	arrData[0] = arrData[0].trim();
				          	if (arrData[0].equals("name"))
				          	{
				          		db.dictionaryName = arrData[1].trim();
				          		//Log.i(FILELIST_TAG, "dictionaryName = " + arrData[1]);
				          	}
				          	else if (arrData[0].equals("from"))
				          	{
				          		db.sourceLanguage = arrData[1].trim();
				          		//Log.i(FILELIST_TAG, "from = " + arrData[1]);
				          	}
				          	else if (arrData[0].equals("to"))
				          	{
				          		db.destinationLanguage= arrData[1].trim();
				          		//Log.i(FILELIST_TAG, "to = " + arrData[1]);
				          	}
				          	else if (arrData[0].equals("style"))
				          	{
				          		db.style= arrData[1].trim();
				          		//Log.i(FILELIST_TAG, "style = " + arrData[1]);
				          	}
			          	}
	          		}
	          		catch (Exception ex){
	          			db.dictionaryName = db.fileName;
	          			Log.e(FILELIST_TAG, "Can not read ifo file!");
	          		}
		          	
	          	}
          		else
          		{
          			db.dictionaryName = db.fileName;
          			Log.i(FILELIST_TAG, "No ifo file found, set dictionary name to file name");
          		}
          		//add to list
          		items.add(db);
	        }
	        Log.i(FILELIST_TAG,"Found " + items.size() + " dictionaries");
        }
        else
        {
        	Log.i(FILELIST_TAG,"Do not find any valid dictionary");
        }
	}
/*	public void _getDatabaseFileList(String dbPath, String dbExtension)
	{
		items.clear();
		names.clear();
		
		File dataDirectory = new File(dbPath);
        File[] listFile = dataDirectory.listFiles();
        if (listFile != null && listFile.length > 0)
	        for (File currentFile : listFile)
	        {
		        if (currentFile.isFile() && currentFile.getName().contains(dbExtension))
		        {
		        	String nameFullFile = currentFile.getName();
		          	int index = nameFullFile.indexOf(dbExtension);
		          	String nameFile = nameFullFile.substring(0, index);
		          	File ifoFile = new File(dbPath + nameFile + ".ifo");
		          	if (ifoFile.exists())
		          	{
		          		String data;
		          		String arrData[] = null;
		          		try
		          		{
				          	BufferedReader brIfoFile = new BufferedReader(new FileReader(ifoFile));
				          	data = brIfoFile.readLine();
				          	arrData = data.split("=");
				          	arrData[0] = arrData[0].trim();
				          	Log.d(FILELIST_TAG, arrData[0]);
				          	if (arrData[0].equals("name"));
				          	{
				          		names.add(arrData[1].trim());
				          	}
		          		}
		          		catch (Exception ex){
		          			names.add(nameFile);
		          			Log.e(FILELIST_TAG, "Can not read ifo file!");
		          		}
			          	
		          	}
		          	else
		          	{
		          		names.add(nameFile);
		          	}
		          	items.add(nameFile);
		        }
	        }
	}*/
}
