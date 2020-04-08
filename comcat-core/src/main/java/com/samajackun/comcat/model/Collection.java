package com.samajackun.comcat.model;

import java.util.SortedMap;
import java.util.TreeMap;

public class Collection
{
	private final String name;

	private final String country;

	private final String language;

	private final SortedMap<String, Issue> issues=new TreeMap<>();

	public Collection(String name, String country, String language)
	{
		super();
		this.name=name;
		this.country=country;
		this.language=language;
	}

	public String getName()
	{
		return this.name;
	}

	public String getCountry()
	{
		return this.country;
	}

	public String getLanguage()
	{
		return this.language;
	}

	public SortedMap<String, Issue> getIssues()
	{
		return this.issues;
	}

	@Override
	public String toString()
	{
		return "Collection [name=" + this.name + ", country=" + this.country + ", language=" + this.language + "]";
	}

}
