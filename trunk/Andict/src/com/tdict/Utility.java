package com.tdict;

public class Utility {

public static String encodeContent(String content)
{
	return 	content.replace('\'', '"');
}
public static String decodeContent(String content)
{
	return 	content.replace('"','\'');
}	
	
}
