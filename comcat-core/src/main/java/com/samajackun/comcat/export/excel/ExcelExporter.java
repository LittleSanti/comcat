package com.samajackun.comcat.export.excel;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.samajackun.comcat.export.Exporter;
import com.samajackun.comcat.model.AbstractCodedObject;
import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.Image;
import com.samajackun.comcat.model.Image.Format;
import com.samajackun.comcat.model.Issue;
import com.samajackun.comcat.model.Story;

public class ExcelExporter implements Exporter
{
	private static final Log LOG=LogFactory.getLog(ExcelExporter.class);

	private final int minIssueRows;

	private final int coverWidthInPixels;

	private final int coverHeightInPixels;

	private final List<ColumnSetup> columnSetups;

	private final String version=readVersion();

	public ExcelExporter(int minIssueRows, int coverWidthInPixels, int coverHeightInPixels, List<ColumnSetup> columnSetups)
	{
		super();
		this.minIssueRows=minIssueRows;
		this.coverWidthInPixels=coverWidthInPixels;
		this.coverHeightInPixels=coverHeightInPixels;
		this.columnSetups=columnSetups;
	}

	private String readVersion()
	{
		String fullClassName=getClass().getName() + ".class";
		String className=getClass().getSimpleName() + ".class";
		URL url=getClass().getResource(className);
		String classLocation=url.toString();
		String manifestLocation=classLocation.substring(0, classLocation.length() - fullClassName.length()) + "META-INF/MANIFEST.MF";
		try (InputStream input=getClass().getResourceAsStream(manifestLocation))
		{
			if (input != null)
			{
				Manifest manifest=new Manifest(input);
				return manifest.getMainAttributes().getValue("Implementation-Version");
			}
			else
			{
				return "";
			}
		}
		catch (IOException e)
		{
			return "";
		}
	}

	@Override
	public void export(Collection collection, OutputStream output, String title)
		throws IOException
	{
		export(collection.getIssues().values().stream(), output, title);
	}

	@Override
	public void export(Stream<Issue> issues, OutputStream output, String title)
		throws IOException
	{
		try (HSSFWorkbook wb=new HSSFWorkbook())
		{
			wb.createInformationProperties();
			wb.getSummaryInformation().setAuthor("Comcat " + this.version + ", Samajackun Software 2021");
			wb.getSummaryInformation().setSubject("colección de comics");
			wb.getSummaryInformation().setCreateDateTime(new Date());
			wb.getSummaryInformation().setTitle(title);
			HSSFSheet sheet=wb.createSheet(title);
			initSheet(sheet);
			issues.forEach(x -> export(x, sheet));
			wb.write(output);
		}
	}

	private void initSheet(HSSFSheet sheet)
	{
		HSSFCellStyle headerStyle=createHeaderStyle(sheet.getWorkbook());
		HSSFRow headerRow=sheet.createRow(0);
		for (int i=0; i < this.columnSetups.size(); i++)
		{
			ColumnSetup columnSetup=this.columnSetups.get(i);
			// Fila de encabezados:
			HSSFCell cell=headerRow.createCell(i);
			cell.setCellValue(columnSetup.getTitle());
			cell.setCellStyle(headerStyle);
			// Filas de datos:
			HSSFCellStyle style=createStyle(sheet.getWorkbook(), columnSetup);
			sheet.setColumnWidth(i, (int)(256 * columnSetup.getWidth()));
			sheet.setDefaultColumnStyle(i, style);
		}
	}

	private static HSSFCellStyle createHeaderStyle(HSSFWorkbook workbook)
	{
		HSSFCellStyle headerStyle=workbook.createCellStyle();
		headerStyle.setVerticalAlignment(VerticalAlignment.TOP);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setWrapText(true);
		headerStyle.setShrinkToFit(true);
		headerStyle.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		HSSFFont font=workbook.createFont();
		font.setBold(true);
		headerStyle.setFont(font);
		return headerStyle;
	}

	private static HSSFCellStyle createStyle(HSSFWorkbook workbook, ColumnSetup columnSetup)
	{
		HSSFCellStyle style=workbook.createCellStyle();
		style.setVerticalAlignment(columnSetup.getVerticalAlignment());
		style.setAlignment(columnSetup.getHorizontalAlignment());
		if (columnSetup.getFontSize() > 0)
		{
			HSSFFont font=workbook.createFont();
			font.setFontHeightInPoints((short)columnSetup.getFontSize());
			style.setFont(font);
		}
		if (columnSetup.getFormat() != null)
		{
			CreationHelper createHelper=workbook.getCreationHelper();
			style.setDataFormat(createHelper.createDataFormat().getFormat(columnSetup.getFormat()));
		}
		style.setWrapText(columnSetup.isWrapText());
		style.setShrinkToFit(columnSetup.isShrinkToFit());
		return style;
	}

	protected void export(Issue issue, HSSFSheet sheet)
	{
		int rowNum=1 + sheet.getLastRowNum();
		int issueRows=Math.max(this.minIssueRows, issue.getStories().size());
		HSSFRow row=sheet.createRow(rowNum);

		int cell=0;

		// Issue number
		row.createCell(cell).setCellValue(issue.getNumber() == null
			? ""
			: issue.getNumber());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// Issue cover
		if (issue.getCover() != null)
		{
			try
			{
				putImage(issue.getCover(), sheet, rowNum, cell);
			}
			catch (IOException e)
			{
				LOG.error("Error loading cover image data from issue " + issue);
			}
		}
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// // Issue backcover
		// if (issue.getBackCover() != null)
		// {
		// try
		// {
		// putImage(issue.getBackCover(), sheet, rowNum, cell);
		// }
		// catch (IOException e)
		// {
		// LOG.error("Error loading backcover image data from issue " + issue);
		// }
		// }
		// sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		// cell++;

		// Issue date
		row.createCell(cell).setCellValue(issue.getDate());
		if (issue.getDate() == null)
		{
			row.getCell(cell).setBlank();
		}
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// Issue code
		row.createCell(cell).setCellValue(issue.getCode());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// Publisher
		row.createCell(cell).setCellValue(issue.getPublisher().getName());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// Collection
		row.createCell(cell).setCellValue(issue.getCollection().getName());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// Owned
		row.createCell(cell).setCellValue(issue.isOwned());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// Issue title
		setCellValueStringNoBlank(row.createCell(cell), issue.getTitle() == null
			? ""
			: issue.getTitle());
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + issueRows - 1, cell, cell));
		cell++;

		// Issue stories
		Counter rowIndex=new Counter(rowNum);
		final int currentCell=cell;
		// issue.getStories().stream().filter(x -> x.getPages() > 1).forEach(x -> exportStory(x, sheet, rowIndex, currentCell));
		issue.getStories().stream().forEach(x -> exportStory(x, sheet, rowIndex, currentCell));
		for (int i=rowIndex.getIndex(); i < rowNum + issueRows; i++)
		{
			HSSFRow rowBlank=sheet.createRow(i);
			for (int j=0; j < this.columnSetups.size(); j++)
			{
				rowBlank.createCell(j).setBlank();
			}
		}
	}

	private class Counter
	{
		private int index;

		public Counter(int index)
		{
			super();
			this.index=index;
		}

		public void inc()
		{
			this.index++;
		}

		public int getIndex()
		{
			return this.index;
		}
	}

	protected void exportStory(Story story, HSSFSheet sheet, Counter rowIndex, int firstCell)
	{
		int rowNum=rowIndex.getIndex();
		int cell=firstCell;
		HSSFRow row=sheet.getRow(rowNum) != null
			? sheet.getRow(rowNum)
			: sheet.createRow(rowNum);
		row.createCell(cell++).setCellValue(story.getCode());
		setCellValueStringNoBlank(row.createCell(cell++), story.getTitle());
		setCellValueStringNoBlank(row.createCell(cell++), story.getHero() == null
			? ""
			: story.getHero().getName());
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getCharacters()));
		row.createCell(cell++).setCellValue(story.getPages());
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getArts()));
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getInks()));
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getPencils()));
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getWriters()));
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getPlots()));
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getColours()));
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getScripts()));
		setCellValueStringNoBlank(row.createCell(cell++), serialize(story.getIdeas()));
		rowIndex.inc();
	}

	private static void setCellValueStringNoBlank(HSSFCell cell, String value)
	{
		if (value == null || value.isEmpty())
		{
			cell.setBlank();
		}
		else
		{
			cell.setCellValue(value);
		}
	}

	private static String serialize(Set<? extends AbstractCodedObject> characters)
	{
		StringBuilder s=new StringBuilder(20 * characters.size());
		characters.stream().forEach(x -> {
			if (s.length() > 0)
			{
				s.append(", ");
			}
			s.append(x.getName());
		});
		return s.toString();
	}

	protected void putImage(Image image, HSSFSheet sheet, int row, int column)
		throws IOException
	{
		byte[] imageBytes=image.getData();
		int pictureIndex=sheet.getWorkbook().addPicture(imageBytes, toWorkbookFormat(image.getFormat()));
		HSSFPatriarch drawing=sheet.createDrawingPatriarch();
		ClientAnchor clientAnchor=new HSSFClientAnchor();
		clientAnchor.setRow1(row);
		clientAnchor.setCol1(column);
		clientAnchor.setRow2(row + this.minIssueRows);
		clientAnchor.setCol2(column);
		clientAnchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
		HSSFPicture picture=drawing.createPicture(clientAnchor, pictureIndex);
		// Redimensionar la imagen:
		// He observado que llamar a picture.getImageDimension().setSize() es tan útil como cantarle una saeta a una berenjena.
		// En cambio, sí funciona resize(sx,sy), pero ojo: la escala (sx y sy) no es respecto a las dimensiones original de la imagen, sino a las de UNA celda.
		Dimension imageDimension=picture.getImageDimension();
		double coverRatio=imageDimension.getHeight() / imageDimension.getWidth();
		int width;
		// int height;
		if (coverRatio * this.coverWidthInPixels > this.coverHeightInPixels)
		{
			// Hay que reducir a lo ancho:
			width=(int)(this.coverHeightInPixels / coverRatio);
			// height=this.coverHeightInPixels;
		}
		else
		{
			width=this.coverWidthInPixels;
			// height=(int)(width * coverRatio);
		}
		double scale=(double)width / this.coverWidthInPixels;
		// System.out.println("width=" + width + ", height=" + height + ", coverWidthInPixels=" + this.coverWidthInPixels + ", coverHeightInPixels=" + this.coverHeightInPixels + ", scale=" + scale);
		picture.resize(scale);
	}

	protected static int toWorkbookFormat(Format formatName)
	{
		int type;
		switch (formatName)
		{
			case JPG:
				type=Workbook.PICTURE_TYPE_JPEG;
				break;
			case PNG:
				type=Workbook.PICTURE_TYPE_PNG;
				break;
			default:
				throw new IllegalArgumentException("Unsupported image type " + formatName);
		}
		return type;
	}

}
