package com.samajackun.comcat.parser.coa;

import static com.samajackun.comcat.parser.coa.TextProcessingUtils.findCollection;
import static com.samajackun.comcat.parser.coa.TextProcessingUtils.findPublisher;

import java.net.URL;
import java.time.LocalDate;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.Image;
import com.samajackun.comcat.model.Issue;
import com.samajackun.comcat.model.Publisher;

class CoaStatefulParser
{
	// private static final Log LOG=LogFactory.getLog(CoaStatefulParser.class);
	//
	private final URL baseUrl;

	private final Document doc;

	private final XPathFactory xpathFactory=XPathFactory.newInstance();

	private final XPath xpath=this.xpathFactory.newXPath();

	public CoaStatefulParser(Document doc, URL baseUrl)
	{
		super();
		this.doc=doc;
		this.baseUrl=baseUrl;
	}

	public Issue parseIssue()
		throws ParseException
	{
		try
		{
			String number=parseNumber();
			Publisher publisher=parsePublisher(number);
			Collection collection=parseCollection();
			LocalDate date=parseDate();
			String code=parseCode();
			String title=parseTitle();
			int pages=parsePages();
			Issue issue=new Issue(publisher, collection, number, date, code, title, pages);
			Image cover=parseCover(this.baseUrl);
			if (cover != null)
			{
				issue.setCover(cover);
			}
			// TODO Falta backcover
			parseStories(issue);
			return issue;
		}
		catch (XPathExpressionException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}

	private void parseStories(Issue issue)
	{
		// TODO Auto-generated method stub

	}

	private Image parseCover(URL url)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private int parsePages()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	private String parseTitle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private String parseCode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private LocalDate parseDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	String parseNumber()
		throws XPathExpressionException,
		ParseException
	{
		String title=(String)this.xpath.evaluate("//h1", this.doc, XPathConstants.STRING);
		int p=title.indexOf('#');
		if (p < 0)
		{
			throw new ParseException("Missing number");
		}
		return title.substring(1 + p).trim();
	}

	// URL parseBaseUrl()
	// throws MalformedURLException,
	// XPathExpressionException
	// {
	// return new URL((String)this.xpath.evaluate("/html/head/base@href", this.doc, XPathConstants.STRING));
	// }

	Publisher parsePublisher(String number)
		throws XPathExpressionException
	{
		// Element body=(Element)this.xpath.evaluate("/html/body", this.doc, XPathConstants.NODE);
		NodeList allTextChildren=(NodeList)this.xpath.evaluate("/html/body//text()", this.doc, XPathConstants.NODESET);
		Publisher publisher=null;
		for (int i=0; publisher == null && i < allTextChildren.getLength(); i++)
		{
			Text child=(Text)allTextChildren.item(i);
			String text=child.getNodeValue().toLowerCase();
			if (text.contains("publisher:"))
			{
				publisher=findPublisher(child, number);
			}
		}
		return publisher;
	}

	Collection parseCollection()
		throws XPathExpressionException,
		ParseException
	{
		NodeList allTextChildren=(NodeList)this.xpath.evaluate("/html/body//text()", this.doc, XPathConstants.NODESET);
		Collection collection=null;
		for (int i=0; collection == null && i < allTextChildren.getLength(); i++)
		{
			Text child=(Text)allTextChildren.item(i);
			String text=child.getNodeValue().toLowerCase();
			if (text.contains("publication:"))
			{
				collection=findCollection(child.getParentNode());
			}
		}
		return collection;
	}
}
