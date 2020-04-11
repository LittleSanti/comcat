package com.samajackun.comcat.parser.coa;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ServiceConfigurationError;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.CollectionFactory;
import com.samajackun.comcat.model.Publisher;
import com.samajackun.comcat.model.PublisherFactory;

final class TextProcessingUtils
{
	private TextProcessingUtils()
	{
	}

	public static Interval<Integer> parseInterval(String s)
	{
		int p1=s.indexOf('(');
		int p2=s.indexOf('-', p1);
		int p3=s.indexOf(')', p2);
		if (p1 >= 0 && p2 >= 0 && p3 >= 0)
		{
			String lowerBoundStr=s.substring(1 + p1, p2).trim();
			String upperBoundStr=s.substring(1 + p2, p3).trim();
			return new Interval<>(Integer.parseInt(lowerBoundStr), upperBoundStr.isEmpty()
				? Integer.MAX_VALUE
				: Integer.parseInt(upperBoundStr));
		}
		return null;
	}

	public static Publisher findPublisher(Node firstNode, String number)
	{
		Publisher publisher=null;
		boolean looping=true;
		Node sibling=firstNode;
		while (looping && publisher == null)
		{
			sibling=sibling.getNextSibling();
			String siblingName=sibling.getNodeName();
			if ("a".equalsIgnoreCase(siblingName))
			{
				Element a=(Element)sibling;
				String href=decodeUrl(a.getAttribute("href"));
				String publisherName=a.getFirstChild().getNodeValue();
				sibling=sibling.getNextSibling();
				if (sibling.getNodeType() == Node.TEXT_NODE)
				{
					Interval<Integer> interval=parseInterval(sibling.getNodeValue());
					Integer numberInteger=TextProcessingUtils.toInteger(number);
					if (numberInteger != null && interval.contains(numberInteger))
					{
						String publisherCode=parseCodeFromUrlArguments(href);
						publisher=PublisherFactory.getInstance().getByCode(publisherCode, publisherName);
					}
				}
				else
				{
					String publisherCode=parseCodeFromUrlArguments(href);
					publisher=PublisherFactory.getInstance().getByCode(publisherCode, publisherName);
				}
			}
			else
			{
				looping=false;
			}
		}
		return publisher;
	}

	public static Collection findCollection(Node container)
		throws ParseException
	{
		CodedItem publication=null;
		String country=null;
		String language=null;
		boolean looping=true;
		NodeList childNodes=container.getChildNodes();
		for (int i=0; looping && i < childNodes.getLength(); i++)
		{
			Node sibling=childNodes.item(i);
			if (sibling.getNodeType() == Node.TEXT_NODE)
			{
				String textContent=sibling.getNodeValue().toLowerCase();
				if (textContent.contains("country:"))
				{
					country=matchCode(sibling);
				}
				else if (textContent.contains("publication:"))
				{
					publication=skipAndMatchCodedItem(sibling);
				}
				else if (textContent.contains("language:"))
				{
					language=matchCode(sibling);
				}
			}
			looping=(country == null || language == null || publication == null);
		}
		return CollectionFactory.getInstance().getByCode(publication.getCode(), publication.getName(), country, language);
	}

	private static Integer toInteger(String number)
	{
		try
		{
			return Integer.parseInt(number);
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	public static String parseCodeFromUrlArguments(String href)
	{
		return getValueFromUrlArguments(href, "c");
	}

	public static String getValueFromUrlArguments(String href, String name)
	{
		href=decodeUrl(href);
		String prefix=name + "=";
		int p=href.indexOf(prefix);
		int p2=href.indexOf('&', p);
		if (p2 < 0)
		{
			p2=href.length();
		}
		return p < 0
			? null
			: href.substring(p + prefix.length(), p2);
	}

	public static String matchCode(Node child)
		throws ParseException
	{
		String code;
		Node sibling=child.getNextSibling();
		String siblingName=sibling.getNodeName();
		if ("a".equalsIgnoreCase(siblingName))
		{
			Element a=(Element)sibling;
			String href=decodeUrl(a.getAttribute("href"));
			code=parseCodeFromUrlArguments(href);
			// String name=a.getFirstChild().getNodeValue();
			// codedItem=new CodedItem(code, name);
		}
		else
		{
			throw new ParseException("Expected A instead of " + sibling.getNodeName());
		}
		return code;
	}

	public static CodedItem skipAndMatchCodedItem(Node child)
		throws ParseException
	{
		return matchCodedItem(child.getNextSibling());
	}

	public static CodedItem matchCodedItem(Node src)
		throws ParseException
	{
		CodedItem codedItem;
		String siblingName=src.getNodeName();
		if ("a".equalsIgnoreCase(siblingName))
		{
			Element a=(Element)src;
			String href=decodeUrl(a.getAttribute("href"));
			String code=parseCodeFromUrlArguments(href);
			String name=a.getFirstChild().getNodeValue();
			codedItem=new CodedItem(code, name);
		}
		else
		{
			throw new ParseException("Expected A instead of " + src.getNodeName());
		}
		return codedItem;
	}

	private static String decodeUrl(String s)
	{
		try
		{
			return URLDecoder.decode(s, "ISO-8859-1");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ServiceConfigurationError(e.toString(), e);
		}
	}

}
