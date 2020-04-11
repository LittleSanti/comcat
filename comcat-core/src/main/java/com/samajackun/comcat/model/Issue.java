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

	private final String code;

	private final String title;

	private final int pages;

	private Image cover;

	private boolean owned;

	private final List<Story> stories=new ArrayList<>();

	public Issue(Publisher publisher, Collection collection, String number, LocalDate date, String code, String title, int pages)
	{
		super();
		this.publisher=publisher;
		this.collection=collection;
		this.number=number;
		this.date=date;
		this.code=code;
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

	public String getNumber()
	{
		return this.number;
	}

	public String getCode()
	{
		return this.code;
	}

	public boolean isOwned()
	{
		return this.owned;
	}

	public void setOwned(boolean owned)
	{
		this.owned=owned;
	}

	@Override
	public String toString()
	{
		return "Issue [publisher=" + this.publisher + ", collection=" + this.collection + ", number=" + this.number + ", date=" + this.date + ", code=" + this.code + ", title=" + this.title + ", pages=" + this.pages + ", cover=" + this.cover + ", owned=" + this.owned + ", stories=" + this.stories + "]";
	}

}
