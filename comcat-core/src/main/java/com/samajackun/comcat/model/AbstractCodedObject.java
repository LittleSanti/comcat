package com.samajackun.comcat.model;

public abstract class AbstractCodedObject implements Comparable<AbstractCodedObject>
{
	private final String code;

	private final String name;

	public AbstractCodedObject(String code, String name)
	{
		super();
		this.code=code;
		this.name=name;
	}

	public String getCode()
	{
		return this.code;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public int compareTo(AbstractCodedObject o)
	{
		return this.name.compareTo(o.name);
	}

	@Override
	public String toString()
	{
		return "[code=" + this.code + ", name=" + this.name + "]";
	}
}
