package com.samajackun.comcat.parser.coa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalTest
{
	@Test
	public void containsNumberUnderLowerBound()
	{
		Interval<Integer> interval=new Interval<>(10, 20);
		assertFalse(interval.contains(9));
	}

	@Test
	public void containsNumberAtLowerBound()
	{
		Interval<Integer> interval=new Interval<>(10, 20);
		assertTrue(interval.contains(10));
	}

	@Test
	public void containsNumberOverLowerBound()
	{
		Interval<Integer> interval=new Interval<>(10, 20);
		assertTrue(interval.contains(15));
	}

	@Test
	public void containsNumberAtUpperBound()
	{
		Interval<Integer> interval=new Interval<>(10, 20);
		assertTrue(interval.contains(20));
	}

	@Test
	public void containsNumberAvobeUpperBound()
	{
		Interval<Integer> interval=new Interval<>(10, 20);
		assertFalse(interval.contains(21));
	}
}
