package com.service.main;

public class AppProps {
	public String appName;
	public String packageName;
	
	public AppProps( String a, String p )
	{
		appName = a;
		packageName = p;
	}
	
	public String toString()
	{
		return appName;
	}
}
