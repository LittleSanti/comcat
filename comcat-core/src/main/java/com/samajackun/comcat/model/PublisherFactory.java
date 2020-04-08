package com.samajackun.comcat.model;

public class PublisherFactory extends AbstractCodedObjectFactory<Publisher>
{
	@Override
	protected Publisher create(String code, String name)
	{
		return new Publisher(code, name);
	}
}
