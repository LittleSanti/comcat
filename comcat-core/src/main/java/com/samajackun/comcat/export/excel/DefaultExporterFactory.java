package com.samajackun.comcat.export.excel;

import java.util.Arrays;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public final class DefaultExporterFactory
{
	private static final DefaultExporterFactory INSTANCE=new DefaultExporterFactory();

	public static DefaultExporterFactory getInstance()
	{
		return INSTANCE;
	}

	private DefaultExporterFactory()
	{
	}

	public ExcelExporter createDefaultExporter()
	{
		double ratio=18.0d;
		int minIssueRows=10;
		int coverWidthInPixels=190;
		int coverHeightInPixels=330;
		ColumnSetup[] columnSetups= {
			new ColumnSetup("editorial", 12, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // publisher
			new ColumnSetup("colección", 12, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // collection
			new ColumnSetup("número", 12, HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 24, false, false, null), // issue number
			new ColumnSetup("portada", ratio, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // issue cover
			new ColumnSetup("contraportada", ratio, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // issue back cover
			new ColumnSetup("fecha", 10, HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 0, false, false, "dd/MM/yyyy"), // issue date
			new ColumnSetup("código", 8, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // issue code
			new ColumnSetup("título", 30, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, true, false, null), // issue title
			new ColumnSetup("código", 8, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // story code
			new ColumnSetup("historia", 25, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // story title
			new ColumnSetup("protagonista", 20, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // story hero
			new ColumnSetup("personajes", 20, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // story characters
			new ColumnSetup("páginas", 5, HorizontalAlignment.RIGHT, VerticalAlignment.TOP, 0, false, false, null), // pages
			new ColumnSetup("dibujo", 20, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // art
			new ColumnSetup("tinta", 20, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // ink
			new ColumnSetup("lápices", 20, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // pencils
			new ColumnSetup("guión", 20, HorizontalAlignment.LEFT, VerticalAlignment.TOP, 0, false, false, null), // plot
		};
		return new ExcelExporter(minIssueRows, coverWidthInPixels, coverHeightInPixels, Arrays.asList(columnSetups));
	}
}
