package com.samajackun.comcat.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class Story
{
	private final String code;

	private final String title;

	private double pages;

	private NamedCharacter hero;

	private Artist writer;

	private Artist ink;

	private Artist art;

	private Artist pencil;
	// Idea
	// Plot

	private Image cover;

	private final Set<NamedCharacter> characters=new LinkedHashSet<>();

	public Story(String code, String title)
	{
		super();
		this.code=code;
		this.title=title;
	}

	public double getPages()
	{
		return this.pages;
	}

	public void setPages(double pages)
	{
		this.pages=pages;
	}

	public Artist getWriter()
	{
		return this.writer;
	}

	public void setWriter(Artist writer)
	{
		this.writer=writer;
	}

	public Artist getInk()
	{
		return this.ink;
	}

	public void setInk(Artist ink)
	{
		this.ink=ink;
	}

	public Artist getArt()
	{
		return this.art;
	}

	public void setArt(Artist art)
	{
		this.art=art;
	}

	public Artist getPencil()
	{
		return this.pencil;
	}

	public void setPencil(Artist pencil)
	{
		this.pencil=pencil;
	}

	public String getCode()
	{
		return this.code;
	}

	public String getTitle()
	{
		return this.title;
	}

	public Image getCover()
	{
		return this.cover;
	}

	public void setCover(Image cover)
	{
		this.cover=cover;
	}

	public NamedCharacter getHero()
	{
		return this.hero;
	}

	public void setHero(NamedCharacter hero)
	{
		this.hero=hero;
	}

	public Set<NamedCharacter> getCharacters()
	{
		return this.characters;
	}

	@Override
	public String toString()
	{
		return "Story [code=" + this.code + ", title=" + this.title + ", pages=" + this.pages + ", hero=" + this.hero + ", writer=" + this.writer + ", ink=" + this.ink + ", art=" + this.art + ", pencil=" + this.pencil + ", cover=" + this.cover + ", characters=" + this.characters + "]";
	}

}
