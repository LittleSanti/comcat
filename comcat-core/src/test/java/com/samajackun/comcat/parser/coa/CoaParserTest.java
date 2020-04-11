package com.samajackun.comcat.parser.coa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.samajackun.comcat.export.Exporter;
import com.samajackun.comcat.export.excel.DefaultExporterFactory;
import com.samajackun.comcat.model.Collection;

public class CoaParserTest
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

	@Test
	public void parseIndex()
		throws IOException
	{
		CoaParser coaParser=new CoaParser();
		try (InputStream input=new FileInputStream(new File(testDir, "dbm-index.html")))
		{
			URL baseUrl=new URL("https://inducks.org/publication.php?c=es/DBM");
			Collection collection=coaParser.parseIndex(input, StandardCharsets.UTF_8, baseUrl);
			assertNotNull(collection);
			assertEquals("es/DBM", collection.getCode());
			assertEquals("Dumbo (Montena)", collection.getName());
			assertEquals("es", collection.getCountry());
			assertEquals("es", collection.getLanguage());
			assertEquals(47, collection.getIssues().size());
			System.out.println("issue 5=" + collection.getIssues().values().iterator().next());
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@Test
	public void parseAndExportToExcel()
		throws IOException
	{
		CoaParser coaParser=new CoaParser();
		try (InputStream input=new FileInputStream(new File(testDir, "dbm-index.html")))
		{
			URL baseUrl=new URL("https://inducks.org/publication.php?c=es/DBM");
			Collection collection=coaParser.parseIndex(input, StandardCharsets.UTF_8, baseUrl);
			assertNotNull(collection);
			assertEquals(47, collection.getIssues().size());
			Exporter exporter=DefaultExporterFactory.getInstance().createDefaultExporter();
			File file=new File("target/dbm.xls");
			try (OutputStream output=new FileOutputStream(file))
			{
				exporter.export(collection.getIssues().values().stream(), output);
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
