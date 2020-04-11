package com.samajackun.comcat.parser.coa;

import static com.samajackun.comcat.parser.coa.TextProcessingUtils.findCollection;
import static com.samajackun.comcat.parser.coa.TextProcessingUtils.findPublisher;
import static com.samajackun.comcat.parser.coa.TextProcessingUtils.parseCodeFromUrlArguments;
import static com.samajackun.comcat.parser.coa.TextProcessingUtils.skipAndMatchCodedItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.samajackun.comcat.model.Artist;
import com.samajackun.comcat.model.ArtistFactory;
import com.samajackun.comcat.model.Collection;
import com.samajackun.comcat.model.Image;
import com.samajackun.comcat.model.ImageFactory;
import com.samajackun.comcat.model.Issue;
import com.samajackun.comcat.model.NamedCharacter;
import com.samajackun.comcat.model.NamedCharacterFactory;
import com.samajackun.comcat.model.Publisher;
import com.samajackun.comcat.model.Story;

class CoaStatefulParser
{
	private static final Log LOG=LogFactory.getLog(CoaStatefulParser.class);

	private final String number;

	private final URL baseUrl;

	private final Document doc;

	private final XPathFactory xpathFactory=XPathFactory.newInstance();

	private final XPath xpath=this.xpathFactory.newXPath();

	public CoaStatefulParser(Document doc, URL baseUrl, String number)
	{
		super();
		this.doc=doc;
		this.baseUrl=baseUrl;
		this.number=number;
	}

	public Issue parseIssue()
		throws ParseException
	{
		try
		{
			Publisher publisher=parsePublisher(this.number);
			Collection collection=parseCollection();
			LocalDate date=parseDate();
			String code=parseCodeFromUrlArguments(this.baseUrl.getQuery());
			String title=parseTitle();
			int pages=parsePages();
			Issue issue=new Issue(publisher, collection, this.number, date, code, title, pages);
			Image cover=parseCover();
			if (cover != null)
			{
				issue.setCover(cover);
			}
			// TODO Falta backcover
			parseStories(issue);
			return issue;
		}
		catch (XPathExpressionException | MalformedURLException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}

	private void parseStories(Issue issue)
		throws XPathExpressionException,
		ParseException
	{
		Element th=(Element)this.xpath.evaluate("//th", this.doc, XPathConstants.NODE);
		Element tbody=(Element)th.getParentNode().getParentNode();
		NodeList trs=(NodeList)this.xpath.evaluate("tr[td[@bgcolor!=\"#ffcc33\"]]", tbody, XPathConstants.NODESET);
		for (int i=0; i < trs.getLength(); i++)
		{
			Element tr=(Element)trs.item(i);
			Story story=parseStory(tr);
			issue.getStories().add(story);
		}
	}

	private Story parseStory(Element tr)
		throws XPathExpressionException,
		ParseException
	{
		NodeList tds=(NodeList)this.xpath.evaluate("td", tr, XPathConstants.NODESET);
		String code=parseCodeFromUrlArguments((String)this.xpath.evaluate("a/@href", tds.item(0), XPathConstants.STRING));
		String title=(String)this.xpath.evaluate("i", tds.item(1), XPathConstants.STRING);
		Story story=new Story(code, title);
		String pagesStr=(String)this.xpath.evaluate("text()", tds.item(2), XPathConstants.STRING);
		double pages=parseStoryPages(pagesStr);
		story.setPages(pages);
		Element authorsElement=(Element)tds.item(3);
		parseAuthors(authorsElement, story);
		Element charactersElement=(Element)tds.item(5);
		parseAppearances(charactersElement, story);
		// Ahora que la caché de characterNames ya contiene todos los personajes, podemos buscar en ella al prota por nombre:
		String heroName=(String)this.xpath.evaluate("small", tds.item(1), XPathConstants.STRING);
		NamedCharacter hero=NamedCharacterFactory.getInstance().getByNameOnly(heroName);
		if (hero != null)
		{
			story.setHero(hero);
		}
		return story;
	}

	private double parseStoryPages(String pagesStr)
	{
		double pages;
		int p=pagesStr.indexOf(' ');
		if (p >= 0)
		{
			pagesStr=pagesStr.substring(0, p).trim();
		}
		if (pagesStr.isEmpty())
		{
			pages=-1;
		}
		else
		{
			int p2=pagesStr.indexOf('/');
			if (p2 >= 0)
			{
				int x1=Integer.parseInt(pagesStr.substring(0, p2));
				int x2=Integer.parseInt(pagesStr.substring(1 + p2));
				pages=((double)x1) / x2;
			}
			else
			{
				pages=Double.parseDouble(pagesStr);
			}
		}
		return pages;
	}

	private void parseAuthors(Element authorsElement, Story story)
		throws XPathExpressionException,
		ParseException
	{
		NodeList nodeList=(NodeList)this.xpath.evaluate("text()", authorsElement, XPathConstants.NODESET);
		for (int i=0; i < nodeList.getLength(); i++)
		{
			Text text=(Text)nodeList.item(i);
			String label=text.getNodeValue().toLowerCase();
			// TODO Falta cardinalidad>1
			if (label.contains("writing:"))
			{
				CodedItem codedItem=skipAndMatchCodedItem(text);
				Artist writer=ArtistFactory.getInstance().getByCode(codedItem.getCode(), codedItem.getName());
				story.setWriter(writer);
			}
			else if (label.contains("art:"))
			{
				CodedItem codedItem=skipAndMatchCodedItem(text);
				Artist artist=ArtistFactory.getInstance().getByCode(codedItem.getCode(), codedItem.getName());
				story.setArt(artist);
			}
			else if (label.contains("pencils:"))
			{
				CodedItem codedItem=skipAndMatchCodedItem(text);
				Artist pencil=ArtistFactory.getInstance().getByCode(codedItem.getCode(), codedItem.getName());
				story.setPencil(pencil);
			}
			else if (label.contains("ink:"))
			{
				CodedItem codedItem=skipAndMatchCodedItem(text);
				Artist ink=ArtistFactory.getInstance().getByCode(codedItem.getCode(), codedItem.getName());
				story.setInk(ink);
			}
		}
	}

	private void parseAppearances(Element appearancesElement, Story story)
		throws XPathExpressionException,
		ParseException
	{
		Node firstChild=appearancesElement.getFirstChild();
		if (firstChild != null && firstChild.getNodeValue() != null && firstChild.getNodeValue().toLowerCase().contains("appearances:"))
		{
			NodeList nodeList=(NodeList)this.xpath.evaluate("a|text()", appearancesElement, XPathConstants.NODESET);
			for (int i=1; i < nodeList.getLength(); i++)
			{
				Node child=nodeList.item(i);
				if ("a".equalsIgnoreCase(child.getNodeName()))
				{
					CodedItem codedItem=TextProcessingUtils.matchCodedItem(child);
					NamedCharacter namedCharacter=NamedCharacterFactory.getInstance().getByCode(codedItem.getCode(), codedItem.getName());
					story.getCharacters().add(namedCharacter);
				}
				else if (child.getNodeType() == Node.TEXT_NODE && child.getNodeValue() != null && child.getNodeValue().contains(","))
				{
					// Es una coma de separación de elementos; hay que dejar que el bucle siga iterando.
				}
				else
				{
					break;
				}
			}
		}
	}

	private Image parseCover()
		throws XPathExpressionException,
		MalformedURLException
	{
		String imgUri=(String)this.xpath.evaluate("/html/body/div[@role='main']//div[@class='image']/img/@src", this.doc, XPathConstants.STRING);
		URL url=new URL(this.baseUrl, imgUri);
		Image image=ImageFactory.getInstance().getImage(url);
		return image;
	}

	int parsePages()
		throws XPathExpressionException
	{
		int pages=-1;
		NodeList allTextChildren=(NodeList)this.xpath.evaluate("/html/body/div[@role='main']//text()", this.doc, XPathConstants.NODESET);
		for (int i=0; pages < 0 && i < allTextChildren.getLength(); i++)
		{
			Text child=(Text)allTextChildren.item(i);
			String text=child.getNodeValue().toLowerCase();
			int p=text.indexOf("pages:");
			if (p >= 0)
			{
				String pagesStr=text.substring(p + "pages:".length()).trim();
				try
				{
					pages=Integer.parseInt(pagesStr);
				}
				catch (NumberFormatException e)
				{
					LOG.error("Error parsing pages " + pagesStr);
				}
			}
		}
		return pages;
	}

	String parseTitle()
		throws XPathExpressionException
	{
		String h1=(String)this.xpath.evaluate("//h1", this.doc, XPathConstants.STRING);
		int p=h1.indexOf(':');
		int p2=h1.indexOf('#', p);
		return p2 >= 0
			? null
			: h1.substring(1 + p).trim();
	}

	LocalDate parseDate()
		throws XPathExpressionException
	{
		String date=(String)this.xpath.evaluate("//time/@datetime", this.doc, XPathConstants.STRING);
		return LocalDate.parse((date.length() >= 8)
			? date
			: date + "-01");
	}

	// String parseNumber()
	// throws XPathExpressionException,
	// ParseException
	// {
	// String title=(String)this.xpath.evaluate("//h1", this.doc, XPathConstants.STRING);
	// int p=title.indexOf('#');
	// if (p < 0)
	// {
	// throw new ParseException("Missing number");
	// }
	// return title.substring(1 + p).trim();
	// }

	// URL parseBaseUrl()
	// throws MalformedURLException,
	// XPathExpressionException
	// {
	// return new URL((String)this.xpath.evaluate("/html/head/base@href", this.doc, XPathConstants.STRING));
	// }

	Publisher parsePublisher(String number)
		throws XPathExpressionException
	{
		NodeList allTextChildren=(NodeList)this.xpath.evaluate("/html/body/div[@role='main']//text()", this.doc, XPathConstants.NODESET);
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
		NodeList allTextChildren=(NodeList)this.xpath.evaluate("/html/body/div[@role='main']//text()", this.doc, XPathConstants.NODESET);
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
