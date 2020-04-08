package com.samajackun.comcat.model;

public class ArtistFactory extends AbstractCodedObjectFactory<Artist>
{

	@Override
	protected Artist create(String code, String name)
	{
		return new Artist(code, name);
	}

}
