package peacemoon.andict;

public class DatabaseFile {
	public String fileName;
	public String dictionaryName;
	public String sourceLanguage;
	public String destinationLanguage;
	public String style;
	public String path;
	
	public DatabaseFile()
	{
		reset();
	}
	
	
	public DatabaseFile(String _fileName, String _dictionaryName, String _sourceLanguage, String _destinationLanguage)
	{
		this.fileName = _fileName;
		this.dictionaryName = _dictionaryName;
		this.sourceLanguage = _sourceLanguage;
		this.destinationLanguage = _destinationLanguage;
	}
	
	public void reset()
	{
		this.fileName = "";
		this.dictionaryName = "";
		this.sourceLanguage = "";
		this.destinationLanguage = "";
		this.style = "";
	}
}
