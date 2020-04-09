package com.samajackun.comcat.model;

public final class PublisherFactory extends AbstractCodedObjectFactory<Publisher>
{
	private static final PublisherFactory INSTANCE=new PublisherFactory();

	public static PublisherFactory getInstance()
	{
		return INSTANCE;
	}

	private PublisherFactory()
	{
	}

	@Override
	protected Publisher create(String code, String name)
	{
		return new Publisher(code, name);
	}
}
