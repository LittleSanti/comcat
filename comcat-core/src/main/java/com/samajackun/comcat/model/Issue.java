package com.samajackun.comcat.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Issue
{
	private final Publisher publisher;

	private final Collection collection;

	private final String number;

	private final LocalDate date;

	private final String title;

	private final int pages;

	private final List<Story> stories=new ArrayList<>();

	private Image cover;

	private Image backCover;

	public Issue(Publisher publisher, Collection collection, String number, LocalDate date, String title, int pages)
	{
		super();
		this.publisher=publisher;
		this.collection=collection;
		this.number=number;
		this.date=date;
		this.title=title;
		this.pages=pages;
	}

	public Publisher getPublisher()
	{
		return this.publisher;
	}

	public LocalDate getDate()
	{
		return this.date;
	}

	public String getTitle()
	{
		return this.title;
	}

	public int getPages()
	{
		return this.pages;
	}

	public List<Story> getStories()
	{
		return this.stories;
	}

	public Collection getCollection()
	{
		return this.collection;
	}

	public Image getCover()
	{
		return this.cover;
	}

	public void setCover(Image cover)
	{
		this.cover=cover;
	}

	public Image getBackCover()
	{
		return this.backCover;
	}

	public void setBackCover(Image backCover)
	{
		this.backCover=backCover;
	}

	public String getNumber()
	{
		return this.number;
	}
}
