package com.samajackun.comcat.parser.coa;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import com.samajackun.comcat.model.Issue;

public class CoaParser
{
	public Issue parseIssue(InputStream input, URL baseUrl)
		throws IOException,
		ParseException
	{
		Tidy tidy=new Tidy();
		Document doc=tidy.parseDOM(input, null);
		return new CoaStatefulParser(doc, baseUrl).parseIssue();
	}

}
