package com.samajackun.comcat.parser.coa;

public class ParseException extends Exception
{
	private static final long serialVersionUID=-2511105636850033956L;

	public ParseException()
	{
		super();
	}

	public ParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ParseException(String message)
	{
		super(message);
	}

	public ParseException(Throwable cause)
	{
		super(cause);
	}
}
