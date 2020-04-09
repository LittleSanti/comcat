package com.samajackun.comcat.model;

public final class ArtistFactory extends AbstractCodedObjectFactory<Artist>
{
	private static final ArtistFactory INSTANCE=new ArtistFactory();

	public static ArtistFactory getInstance()
	{
		return INSTANCE;
	}

	private ArtistFactory()
	{
	}

	@Override
	protected Artist create(String code, String name)
	{
		return new Artist(code, name);
	}

}
