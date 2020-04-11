package com.samajackun.comcat.parser.coa;

import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

abstract class AbstractStatefulParser
{
	private final URL baseUrl;

	private final Document doc;

	private final XPathFactory xpathFactory=XPathFactory.newInstance();

	private final XPath xpath=this.xpathFactory.newXPath();

	protected AbstractStatefulParser(Document doc, URL baseUrl)
	{
		super();
		this.baseUrl=baseUrl;
		this.doc=doc;
	}

	protected URL getBaseUrl()
	{
		return this.baseUrl;
	}

	protected Document getDoc()
	{
		return this.doc;
	}

	protected XPathFactory getXpathFactory()
	{
		return this.xpathFactory;
	}

	protected XPath getXpath()
	{
		return this.xpath;
	}

}