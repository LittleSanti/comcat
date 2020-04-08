package com.samajackun.comcat.export.excel;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.samajackun.comcat.export.Exporter;
import com.samajackun.comcat.model.Image;
import com.samajackun.comcat.model.Image.Format;
import com.samajackun.comcat.model.Issue;
import com.samajackun.comcat.model.Story;

public class ExcelExporter implements Exporter
{
	private static final Log LOG=LogFactory.getLog(ExcelExporter.class);

	private final double ratioRef=0.716796875d;

	private final double ratio=18.0d;

	private final int coverHeight=9;

	@Override
	public void export(Stream<Issue> issues, OutputStream output)
		throws IOException
	{
		try (HSSFWorkbook wb=new HSSFWorkbook())
		{
			HSSFSheet sheet=wb.createSheet("issues");
			issues.forEach(x -> export(x, sheet));
			wb.write(output);
		}
	}

	protected void export(Issue issue, HSSFSheet sheet)
	{
		int row=sheet.getLastRowNum();
		sheet.setColumnWidth(1, (int)(256 * this.ratio));
		sheet.setColumnWidth(2, (int)(256 * this.ratio));
		int cell=0;
		sheet.addMergedRegion(new CellRangeAddress(row, row + this.coverHeight - 1, cell, cell));
		sheet.getRow(row).getCell(cell++).setCellValue(issue.getNumber());

		sheet.addMergedRegion(new CellRangeAddress(row, row + this.coverHeight - 1, cell, cell));
		sheet.getRow(row).getCell(cell++).setCellValue(issue.getTitle());

		sheet.addMergedRegion(new CellRangeAddress(row, row + this.coverHeight - 1, cell, cell));
		if (issue.getCover() != null)
		{
			try
			{
				putImage(issue.getCover(), sheet, row, cell);
			}
			catch (IOException e)
			{
				LOG.error("Loading cover image data from issue " + issue);
			}
		}
		cell++;

		sheet.addMergedRegion(new CellRangeAddress(row, row + this.coverHeight - 1, cell, cell));
		if (issue.getBackCover() != null)
		{
			try
			{
				putImage(issue.getBackCover(), sheet, row, cell);
			}
			catch (IOException e)
			{
				LOG.error("Loading backcover image data from issue " + issue);
			}
		}
		cell++;

		RowIndex rowIndex=new RowIndex(row);
		final int currentCell=cell;
		issue.getStories().stream().filter(x -> x.getPages() > 1).forEach(x -> exportStory(x, sheet, rowIndex, currentCell));
	}

	private class RowIndex
	{
		private int index;

		public RowIndex(int index)
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

	protected void exportStory(Story story, HSSFSheet sheet, RowIndex rowIndex, int firstCell)
	{
		int row=rowIndex.getIndex();
		int cell=firstCell;
		sheet.getRow(row).getCell(cell++).setCellValue(story.getCode());
		sheet.getRow(row).getCell(cell++).setCellValue(story.getTitle());
		sheet.getRow(row).getCell(cell++).setCellValue(story.getHero().getName());
		sheet.getRow(row).getCell(cell++).setCellValue(story.getPages());
		sheet.getRow(row).getCell(cell++).setCellValue(story.getArt().getName());
		sheet.getRow(row).getCell(cell++).setCellValue(story.getInk().getName());
		sheet.getRow(row).getCell(cell++).setCellValue(story.getPencil().getName());
		sheet.getRow(row).getCell(cell++).setCellValue(story.getWriter().getName());
		rowIndex.inc();
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
		HSSFPicture picture=drawing.createPicture(clientAnchor, pictureIndex);
		// Redimensionar la imagen:
		// He observado que llamar a picture.getImageDimension().setSize() es tan útil como cantarle una saeta a una berenjena.
		// En cambio, sí funciona resize(sx,sy), pero ojo: la escala (sx y sy) no es respecto a las dimensiones original de la imagen, sino a las de UNA celda.
		Dimension imageDimension=picture.getImageDimension();
		int coverWidth=(int)((imageDimension.getWidth() / imageDimension.getHeight()) / this.ratioRef);
		picture.resize(coverWidth, this.coverHeight);
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
