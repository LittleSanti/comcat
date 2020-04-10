package com.samajackun.comcat.parser.coa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextProcessingUtilsTest
{
	@Test
	public void parseCodeSingle()
	{
		String href="publisher.php?c=wd";
		String code=TextProcessingUtils.parseCodeFromUrlArguments(href);
		assertEquals("wd", code);
	}

	@Test
	public void parseCodeWithOtherArguments()
	{
		String href="publisher.php?a=1&c=wd&b=2";
		String code=TextProcessingUtils.parseCodeFromUrlArguments(href);
		assertEquals("wd", code);
	}

	@Test
	public void parseIntervalComplete()
	{
		String text="(5-638),";
		Interval<Integer> interval=TextProcessingUtils.parseInterval(text);
		assertEquals(5, interval.getLowerBound().intValue());
		assertEquals(638, interval.getUpperBound().intValue());
	}

	@Test
	public void parseIntervalWithoutUpperBound()
	{
		String text="(5-),";
		Interval<Integer> interval=TextProcessingUtils.parseInterval(text);
		assertEquals(5, interval.getLowerBound().intValue());
		assertEquals(Integer.MAX_VALUE, interval.getUpperBound().intValue());
	}
}
