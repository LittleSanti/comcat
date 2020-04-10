package com.samajackun.comcat.parser.coa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import com.samajackun.comcat.model.Collection;
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
			assertEquals("Mont", publisher.getCode());
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

	@Test
	public void parseNumber()
		throws SAXException,
		IOException
	{
		Document doc=parseFile("wdc150.html");
		try
		{
			String number=new CoaStatefulParser(doc, null).parseNumber();
			assertNotNull(number);
			assertEquals("150", number);
		}
		catch (XPathExpressionException | ParseException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

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
}
