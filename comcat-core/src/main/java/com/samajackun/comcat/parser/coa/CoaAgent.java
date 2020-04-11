package com.samajackun.comcat.parser.coa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.samajackun.comcat.export.Exporter;
import com.samajackun.comcat.export.excel.DefaultExporterFactory;
import com.samajackun.comcat.model.Collection;

public final class CoaAgent
{
	private CoaAgent()
	{
	}

	public static void importIndexAndExportToExcel(URL indexUrl, File outputFile)
		throws IOException,
		ParseException
	{
		CoaParser coaParser=new CoaParser();
		URLConnection connection=indexUrl.openConnection();
		try (InputStream input=connection.getInputStream())
		{
			URL baseUrl=indexUrl;
			Charset inputCharset=TextProcessingUtils.getHtmlEncoding(connection);
			Collection collection=coaParser.parseIndex(input, inputCharset, baseUrl);
			Exporter exporter=DefaultExporterFactory.getInstance().createDefaultExporter();
			try (OutputStream output=new FileOutputStream(outputFile))
			{
				exporter.export(collection, output, collection.getName());
			}
		}
	}

}
