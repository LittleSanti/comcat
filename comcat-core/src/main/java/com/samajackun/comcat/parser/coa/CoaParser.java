package com.samajackun.comcat.parser.coa;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import com.samajackun.comcat.model.Issue;

public class CoaParser
{
	public Issue parseIssue(InputStream input, Charset inputCharset, URL baseUrl, String number)
		throws IOException,
		ParseException
	{
		Tidy tidy=new Tidy();
		tidy.setInputEncoding(inputCharset.name());
		Document doc=tidy.parseDOM(input, null);
		return new CoaStatefulParser(doc, baseUrl, number).parseIssue();
	}

}
