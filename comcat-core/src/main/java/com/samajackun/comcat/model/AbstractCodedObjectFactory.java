package com.samajackun.comcat.model;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCodedObjectFactory<T extends AbstractCodedObject>
{
	private final Map<String, T> mapByCode=new HashMap<>((int)(256 * 1.7d));

	private final Map<String, T> mapByName=new HashMap<>((int)(256 * 1.7d));

	public T getByCode(String code, String name)
	{
		return this.mapByCode.computeIfAbsent(code, x -> {
			T value=create(code, name);
			this.mapByName.put(name, value);
			return value;
		});
	}

	public T getByName(String code, String name)
	{
		return this.mapByName.computeIfAbsent(name, x -> {
			T value=create(code, name);
			this.mapByCode.put(code, value);
			return value;
		});
	}

	public T getByNameOnly(String name)
	{
		return this.mapByName.get(name);
	}

	protected abstract T create(String code, String name);
}
