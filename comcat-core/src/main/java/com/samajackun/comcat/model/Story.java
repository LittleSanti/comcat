package com.samajackun.comcat.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class Story
{
	private final String code;

	private final String title;

	private double pages;

	private NamedCharacter hero;

	private final Set<Artist> writers=new TreeSet<>();

	private final Set<Artist> inks=new TreeSet<>();

	private final Set<Artist> arts=new TreeSet<>();

	private final Set<Artist> pencils=new TreeSet<>();

	private final Set<Artist> plots=new TreeSet<>();

	private final Set<Artist> colours=new TreeSet<>();

	private final Set<Artist> scripts=new TreeSet<>();

	private final Set<Artist> ideas=new TreeSet<>();
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

	public Set<Artist> getWriters()
	{
		return this.writers;
	}

	public Set<Artist> getInks()
	{
		return this.inks;
	}

	public Set<Artist> getArts()
	{
		return this.arts;
	}

	public Set<Artist> getPencils()
	{
		return this.pencils;
	}

	public Set<Artist> getPlots()
	{
		return this.plots;
	}

	public Set<Artist> getColours()
	{
		return this.colours;
	}

	public Set<Artist> getScripts()
	{
		return this.scripts;
	}

	public Set<Artist> getIdeas()
	{
		return this.ideas;
	}

	@Override
	public String toString()
	{
		return "Story [code=" + this.code + ", title=" + this.title + ", pages=" + this.pages + ", hero=" + this.hero + ", writers=" + this.writers + ", inks=" + this.inks + ", arts=" + this.arts + ", pencils=" + this.pencils + ", plots=" + this.plots + ", colours=" + this.colours + ", script=" + this.scripts + ", idea=" + this.ideas + ", cover=" + this.cover + ", characters=" + this.characters + "]";
	}

}
