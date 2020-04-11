package com.samajackun.comcat.parser.coa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.Issue;
import com.samajackun.comcat.model.Publisher;

public class CoaStatefulParserTest
{
	private static final File testDir=new File(getSystemProperty("test.dir"));

	private static String getSystemProperty(String propertyName)
	{
		String value=System.getProperty(propertyName);
		if (value == null)
		{
			throw new IllegalArgumentException("Missing system property '" + propertyName + "'");
		}
		return value;
	}

	private static org.w3c.dom.Document parseFile(String fileName)
		throws org.xml.sax.SAXException,
		java.io.IOException
	{
		try (InputStream input=new FileInputStream(new File(testDir, fileName)))
		{
			Tidy tidy=new Tidy();
			tidy.setInputEncoding(StandardCharsets.UTF_8.name());
			Document doc=tidy.parseDOM(input, null);
			return doc;
		}
	}

	@Test
	public void parsePublisherWithIntervals()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("dm150.html");
		try
		{
			Publisher publisher=new CoaStatefulParser(doc, null).parsePublisher("150");
			assertNotNull(publisher);
			assertEquals("Montena", publisher.getCode());
			assertEquals("Montena", publisher.getName());
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parsePublisherUnique()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("wdc150.html");
		try
		{
			Publisher publisher=new CoaStatefulParser(doc, null).parsePublisher("150");
			assertNotNull(publisher);
			assertEquals("Dl", publisher.getCode());
			assertEquals("Dell", publisher.getName());
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	// @Test
	// public void parseNumber()
	// throws SAXException,
	// IOException
	// {
	// Document doc=parseFile("wdc150.html");
	// try
	// {
	// String number=new CoaStatefulParser(doc, null, "150").parseNumber();
	// assertNotNull(number);
	// assertEquals("150", number);
	// }
	// catch (XPathExpressionException | ParseException e)
	// {
	// e.printStackTrace();
	// fail(e.toString());
	// }
	// }

	@Test
	public void parseCollection()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("wdc150.html");
		try
		{
			Collection collection=new CoaStatefulParser(doc, null).parseCollection();
			assertNotNull(collection);
			assertEquals("us/WDC", collection.getCode());
			assertEquals("Walt Disney's Comics and Stories", collection.getName());
			assertEquals("us", collection.getCountry());
			assertEquals("en", collection.getLanguage());
		}
		catch (XPathExpressionException | ParseException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parseDateWithYearMonthDay()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("dm150.html");
		try
		{
			LocalDate date=new CoaStatefulParser(doc, null).parseDate();
			assertNotNull(date);
			assertEquals("1979-08-23", date.toString());
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parseDateWithYearMonth()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("wdc150.html");
		try
		{
			LocalDate date=new CoaStatefulParser(doc, null).parseDate();
			assertNotNull(date);
			assertEquals("1953-03-01", date.toString());
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parseDefaultTitle()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("dm150.html");
		try
		{
			String title=new CoaStatefulParser(doc, null).parseTitle();
			assertNull(title);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parseExplicitTitle()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("d132.html");
		try
		{
			String title=new CoaStatefulParser(doc, null).parseTitle();
			assertEquals("Examen de conciencia", title);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parsePagesPresent()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("dm150.html");
		try
		{
			int pages=new CoaStatefulParser(doc, null).parsePages();
			assertEquals(100, pages);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parsePagesNotPresent()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("wdc150.html");
		try
		{
			int pages=new CoaStatefulParser(doc, null).parsePages();
			assertEquals(-1, pages);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parseIssueNotOwned()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("dm150.html");
		try
		{
			Issue issue=new CoaStatefulParser(doc, new URL("https://inducks.org/issue.php?c=es/DM++150")).parseIssue("150");
			assertEquals(7, issue.getStories().size());
			assertFalse(issue.isOwned());
			System.out.println(issue.getStories());
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parseIssueOwned()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("dm154.html");
		try
		{
			Issue issue=new CoaStatefulParser(doc, new URL("https://inducks.org/issue.php?c=es/DM++154")).parseIssue("154");
			assertEquals(6, issue.getStories().size());
			assertTrue(issue.isOwned());
			System.out.println(issue.getStories());
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
