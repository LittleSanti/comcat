package com.samajackun.comcat.export.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Test;

import com.samajackun.comcat.export.Exporter;
import com.samajackun.comcat.model.ArtistFactory;
import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.CollectionFactory;
import com.samajackun.comcat.model.ImageFactory;
import com.samajackun.comcat.model.Issue;
import com.samajackun.comcat.model.NamedCharacter;
import com.samajackun.comcat.model.NamedCharacterFactory;
import com.samajackun.comcat.model.Publisher;
import com.samajackun.comcat.model.PublisherFactory;
import com.samajackun.comcat.model.Story;

public class ExcelExporterTest
{

	@Test
	public void exportOneIssues()
		throws IOException
	{
		Exporter exporter=DefaultExporterFactory.getInstance().createDefaultExporter();
		Issue[] issues=new Issue[] {
			createIssue(1),
		};
		File file=new File("target/x1.xls");
		try (OutputStream output=new FileOutputStream(file))
		{
			exporter.export(Arrays.asList(issues).stream(), output);
		}
	}

	@Test
	public void exportTwoIssues()
		throws IOException
	{
		Exporter exporter=DefaultExporterFactory.getInstance().createDefaultExporter();
		Issue[] issues=new Issue[] {
			createIssue(1),
			createIssue(2),
		};
		File file=new File("target/x2.xls");
		try (OutputStream output=new FileOutputStream(file))
		{
			exporter.export(Arrays.asList(issues).stream(), output);
		}
	}

	@Test
	public void exportLengthyIssue()
		throws IOException
	{
		Exporter exporter=DefaultExporterFactory.getInstance().createDefaultExporter();
		Issue[] issues=new Issue[] {
			createIssue(6),
			createIssue(3),
		};
		File file=new File("target/x3.xls");
		try (OutputStream output=new FileOutputStream(file))
		{
			exporter.export(Arrays.asList(issues).stream(), output);
		}
	}

	@Test
	public void exportEmptyFieldsIssue()
		throws IOException
	{
		Exporter exporter=DefaultExporterFactory.getInstance().createDefaultExporter();
		Publisher publisher=PublisherFactory.getInstance().getByCode("Dell", "Dell Comics");
		Collection collection=CollectionFactory.getInstance().getByCode("us/CAS", "comics and stories", "en", "us");
		String number=null;
		LocalDate date=null;
		String title=null;
		int pages=99;
		String code=collection.getCode() + "-0";
		Issue issue=new Issue(publisher, collection, number, date, code, title, pages);
		for (int j=0; j < 1; j++)
		{
			Story story=new Story("a/" + j, "Story no. " + j);
			issue.getStories().add(story);
		}

		Issue[] issues=new Issue[] {
			issue,
		};
		File file=new File("target/x4.xls");
		try (OutputStream output=new FileOutputStream(file))
		{
			exporter.export(Arrays.asList(issues).stream(), output);
		}
	}

	private Issue createIssue(int i)
	{
		Publisher publisher=PublisherFactory.getInstance().getByCode("Dell", "Dell Comics");
		Collection collection=CollectionFactory.getInstance().getByCode("us/CAS", "comics and stories", "en", "us");
		String number=Integer.toString(160 + i);
		LocalDate date=LocalDate.of(1981, 2, 23);
		String title="Adventures of issue " + i;
		int pages=99;
		String code=collection.getCode() + "-" + i;
		Issue issue=new Issue(publisher, collection, number, date, code, title, pages);
		issue.setCover(ImageFactory.getInstance().getImage(new File("target/test-classes/0" + i + ".png")));
		issue.setOwned(i % 2 == 0);
		for (int j=0; j < 2 * (1 + i); j++)
		{
			issue.getStories().add(createStory(j));
		}
		return issue;
	}

	private Story createStory(int j)
	{
		Story story=new Story("a/" + j, "Story no. " + j);
		story.getArts().add(ArtistFactory.getInstance().getByCode("cb", "Carl Barks"));
		story.getInks().add(ArtistFactory.getInstance().getByCode("gc", "Giorgio Cavazzano"));
		story.getPencils().add(ArtistFactory.getInstance().getByCode("pm", "Paul Murry"));
		story.getWriters().add(ArtistFactory.getInstance().getByCode("martina", "Guido Martina"));
		story.setHero(NamedCharacterFactory.getInstance().getByCode("dd", "Donald Duck"));
		story.setPages(20 + j);
		if (j > 0)
		{
			story.getCharacters().add(new NamedCharacter("us", "uncle scrooge"));
		}
		if (j > 1)
		{
			story.getCharacters().add(new NamedCharacter("hdl", "huey dewey and louie"));
		}
		if (j > 2)
		{
			story.getCharacters().add(new NamedCharacter("da", "daisy"));
		}
		return story;
	}

}
