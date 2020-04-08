package com.samajackun.comcat.model;

public class NamedCharacterFactory extends AbstractCodedObjectFactory<NamedCharacter>
{
	@Override
	protected NamedCharacter create(String code, String name)
	{
		return new NamedCharacter(code, name);
	}
}
