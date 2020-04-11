package com.samajackun.comcat.parser.coa;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.CollectionFactory;
import com.samajackun.comcat.model.Issue;

class CoaStatefulIndexParser extends AbstractStatefulParser
{
	private static final Log LOG=LogFactory.getLog(CoaStatefulIndexParser.class);

	public CoaStatefulIndexParser(Document doc, URL baseUrl)
	{
		super(doc, baseUrl);
	}

	public Collection parseIndex(CoaParser issueParser)
		throws IOException,
		ParseException
	{
		try
		{
			// Country:
			Element countryAnchor=(Element)getXpath().evaluate("//a[contains(@href,'country.php?')]", getDoc(), XPathConstants.NODE);
			String country=TextProcessingUtils.parseCodeFromUrlArguments(countryAnchor.getAttribute("href"));

			// Language:
			Element languageAnchor=(Element)getXpath().evaluate("//a[contains(@href,'language.php?')]", getDoc(), XPathConstants.NODE);
			String language=TextProcessingUtils.parseCodeFromUrlArguments(languageAnchor.getAttribute("href"));

			// Collection code:
			String indexCode=TextProcessingUtils.parseCodeFromUrlArguments(getBaseUrl().getQuery());

			// Collection name:
			String h1=(String)getXpath().evaluate("//h1", getDoc(), XPathConstants.STRING);
			int p=h1.indexOf(':');
			String indexName=(p >= 0)
				? h1.substring(1 + p).trim()
				: h1;

			Collection collection=CollectionFactory.getInstance().getByCode(indexCode, indexName, country, language);

			// Issues:
			NodeList nodeList=(NodeList)getXpath().evaluate("//a[contains(@href,'issue.php?')]", getDoc(), XPathConstants.NODESET);
			for (int i=0; i < nodeList.getLength(); i++)
			{
				Element anchor=(Element)nodeList.item(i);
				String issueCode=TextProcessingUtils.parseCodeFromUrlArguments(anchor.getAttribute("href"));
				Issue issue=loadIssue(anchor, issueParser);
				collection.getIssues().put(issueCode, issue);
				LOG.info("Loaded issue " + issue.getNumber());
			}
			return collection;
		}
		catch (XPathExpressionException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	private Issue loadIssue(Element anchor, CoaParser issueParser)
		throws IOException,
		ParseException
	{
		String href=anchor.getAttribute("href");
		String number=anchor.getFirstChild().getNodeValue();
		URL issueUrl=new URL(getBaseUrl(), href);
		URLConnection connection=issueUrl.openConnection();
		connection.addRequestProperty("Accept-Language", "es-ES;q=0.5,es;q=0.3");
		String contentType=connection.getHeaderField("Content-Type");
		if (contentType.startsWith("text/html"))
		{
			int p=contentType.indexOf("charset=");
			Charset inputCharset=(p >= 0)
				? Charset.forName(contentType.substring(p + "charset=".length()).trim())
				: StandardCharsets.ISO_8859_1;
			try (InputStream input=connection.getInputStream())
			{
				Issue issue=issueParser.parseIssue(input, inputCharset, issueUrl, number);
				return issue;
			}
		}
		else
		{
			throw new IllegalArgumentException("Unsupported content type " + contentType + " when loading issue.");
		}
	}

}
