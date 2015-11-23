package data;

import java.util.HashMap;

public abstract class DbObject
{
	private HashMap<String, Object> _properties = new HashMap<String, Object>();

	public DbObject()
	{
	}

	public void putValue(String paramString, Object paramObject)
	{
		this._properties.put(paramString, paramObject);
	}

}