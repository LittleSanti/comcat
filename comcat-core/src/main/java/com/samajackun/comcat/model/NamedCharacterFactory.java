package com.samajackun.comcat.model;

public final class NamedCharacterFactory extends AbstractCodedObjectFactory<NamedCharacter>
{
	private static final NamedCharacterFactory INSTANCE=new NamedCharacterFactory();

	public static NamedCharacterFactory getInstance()
	{
		return INSTANCE;
	}

	private NamedCharacterFactory()
	{
	}

	@Override
	protected NamedCharacter create(String code, String name)
	{
		return new NamedCharacter(code, name);
	}
}
