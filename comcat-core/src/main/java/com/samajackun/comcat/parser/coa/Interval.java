package com.samajackun.comcat.parser.coa;

public class Interval<T extends Comparable<T>>
{
	private final T lowerBound;

	private final T upperBound;

	public Interval(T lowerBound, T upperBound)
	{
		super();
		this.lowerBound=lowerBound;
		this.upperBound=upperBound;
	}

	public boolean contains(T value)
	{
		int x1=value.compareTo(this.lowerBound);
		int x2=value.compareTo(this.upperBound);
		return x1 >= 0 && x2 <= 0;
	}

	public T getLowerBound()
	{
		return this.lowerBound;
	}

	public T getUpperBound()
	{
		return this.upperBound;
	}
}
