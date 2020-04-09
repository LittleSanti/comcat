package com.samajackun.comcat.model;

import java.util.HashMap;
import java.util.Map;

public final class CollectionFactory
{
	private static final CollectionFactory INSTANCE=new CollectionFactory();

	public static CollectionFactory getInstance()
	{
		return INSTANCE;
	}

	private CollectionFactory()
	{
	}

	private final Map<String, Collection> mapByCode=new HashMap<>((int)(256 * 1.7d));

	private final Map<String, Collection> mapByName=new HashMap<>((int)(256 * 1.7d));

	public Collection getByCode(String code, String name, String country, String language)
	{
		return this.mapByCode.computeIfAbsent(code, x -> {
			Collection value=create(code, name, country, language);
			this.mapByName.put(name, value);
			return value;
		});
	}

	public Collection getByName(String code, String name, String country, String language)
	{
		return this.mapByName.computeIfAbsent(name, x -> {
			Collection value=create(code, name, country, language);
			this.mapByCode.put(code, value);
			return value;
		});
	}

	protected Collection create(String code, String name, String country, String language)
	{
		return new Collection(code, name, country, language);
	}

}
