package com.samajackun.comcat.parser.coa;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.Issue;

public class CoaParser
{
	private Document parseHtml(InputStream input, Charset inputCharset)
	{
		Tidy tidy=new Tidy();
		tidy.setInputEncoding(inputCharset.name());
		tidy.setShowErrors(0);
		tidy.setShowWarnings(false);
		return tidy.parseDOM(input, null);
	}

	public Issue parseIssue(InputStream input, Charset inputCharset, URL baseUrl, String number)
		throws IOException,
		ParseException
	{
		Document doc=parseHtml(input, inputCharset);
		return new CoaStatefulParser(doc, baseUrl).parseIssue(number);
	}

	public Collection parseIndex(InputStream input, Charset inputCharset, URL baseUrl)
		throws IOException,
		ParseException
	{
		Document doc=parseHtml(input, inputCharset);
		return new CoaStatefulIndexParser(doc, baseUrl).parseIndex(this);
	}
}
