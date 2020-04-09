package com.samajackun.comcat.export.excel;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class ColumnSetup
{
	private final String title;

	private final double width;

	private final HorizontalAlignment horizontalAlignment;

	private final VerticalAlignment verticalAlignment;

	private final int fontSize;

	private final boolean wrapText;

	private final boolean shrinkToFit;

	private final String format;

	public ColumnSetup(String title, double width, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int fontSize, boolean wrapText, boolean shrinkToFit, String format)
	{
		super();
		this.title=title;
		this.width=width;
		this.horizontalAlignment=horizontalAlignment;
		this.verticalAlignment=verticalAlignment;
		this.fontSize=fontSize;
		this.wrapText=wrapText;
		this.shrinkToFit=shrinkToFit;
		this.format=format;
	}

	public String getTitle()
	{
		return this.title;
	}

	public double getWidth()
	{
		return this.width;
	}

	public HorizontalAlignment getHorizontalAlignment()
	{
		return this.horizontalAlignment;
	}

	public VerticalAlignment getVerticalAlignment()
	{
		return this.verticalAlignment;
	}

	public int getFontSize()
	{
		return this.fontSize;
	}

	public boolean isWrapText()
	{
		return this.wrapText;
	}

	public boolean isShrinkToFit()
	{
		return this.shrinkToFit;
	}

	public String getFormat()
	{
		return this.format;
	}

	@Override
	public String toString()
	{
		return "ColumnSetup [title=" + this.title + ", width=" + this.width + ", horizontalAlignment=" + this.horizontalAlignment + ", verticalAlignment=" + this.verticalAlignment + ", fontSize=" + this.fontSize + ", wrapText=" + this.wrapText + ", shrinkToFit=" + this.shrinkToFit + ", format=" + this.format + "]";
	}

}
