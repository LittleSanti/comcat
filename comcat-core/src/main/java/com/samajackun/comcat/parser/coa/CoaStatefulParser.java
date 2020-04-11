package com.samajackun.comcat.parser.coa;

import static com.samajackun.comcat.parser.coa.TextProcessingUtils.findCollection;
import static com.samajackun.comcat.parser.coa.TextProcessingUtils.findPublisher;
import static com.samajackun.comcat.parser.coa.TextProcessingUtils.parseCodeFromUrlArguments;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

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

class CoaStatefulParser extends AbstractStatefulParser
{
	private static final Log LOG=LogFactory.getLog(CoaStatefulParser.class);

	public CoaStatefulParser(Document doc, URL baseUrl)
	{
		super(doc, baseUrl);
	}

	public Issue parseIssue(String number)
		throws ParseException
	{
		try
		{
			Publisher publisher=parsePublisher(number);
			Collection collection=parseCollection();
			LocalDate date=parseDate();
			String code=parseCodeFromUrlArguments(getBaseUrl().getQuery());
			String title=parseTitle();
			int pages=parsePages();
			Issue issue=new Issue(publisher, collection, number, date, code, title, pages);
			boolean owned=parseOwned();
			issue.setOwned(owned);
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

	private boolean parseOwned()
		throws XPathExpressionException
	{
		Element element=(Element)getXpath().evaluate("/html/body//header//img[contains(@src,'owned')]", getDoc(), XPathConstants.NODE);
		return element != null;
	}

	private void parseStories(Issue issue)
		throws XPathExpressionException,
		ParseException
	{
		Element th=(Element)getXpath().evaluate("//th", getDoc(), XPathConstants.NODE);
		Element tbody=(Element)th.getParentNode().getParentNode();
		NodeList trs=(NodeList)getXpath().evaluate("tr[td[@bgcolor!=\"#ffcc33\"]]", tbody, XPathConstants.NODESET);
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
		NodeList tds=(NodeList)getXpath().evaluate("td", tr, XPathConstants.NODESET);
		String code=parseCodeFromUrlArguments((String)getXpath().evaluate("a/@href", tds.item(0), XPathConstants.STRING));
		String title=(String)getXpath().evaluate("i", tds.item(1), XPathConstants.STRING);
		Story story=new Story(code, title);
		String pagesStr=(String)getXpath().evaluate("text()", tds.item(2), XPathConstants.STRING);
		double pages=parseStoryPages(pagesStr);
		story.setPages(pages);
		Element authorsElement=(Element)tds.item(3);
		parseAuthors(authorsElement, story);
		Element charactersElement=(Element)tds.item(5);
		parseAppearances(charactersElement, story);
		// Ahora que la caché de characterNames ya contiene todos los personajes, podemos buscar en ella al prota por nombre:
		String heroName=(String)getXpath().evaluate("small", tds.item(1), XPathConstants.STRING);
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

	private enum AuthorState {
		INITIAL, WRITER, ART, PENCIL, INK, PLOT, COLOUR, SCRIPT, IDEA
	};

	private void parseAuthors(Element authorsElement, Story story)
		throws XPathExpressionException,
		ParseException
	{
		NodeList nodeList=(NodeList)getXpath().evaluate("a|text()", authorsElement, XPathConstants.NODESET);
		AuthorState state=AuthorState.INITIAL;
		for (int i=0; i < nodeList.getLength(); i++)
		{
			Node node=nodeList.item(i);
			switch (node.getNodeType())
			{
				case Node.TEXT_NODE:
					Text text=(Text)nodeList.item(i);
					String label=text.getNodeValue().toLowerCase();
					if (label.contains(","))
					{
						// Es un separador de elementos: Ignorar.
					}
					else if (label.contains("writing:"))
					{
						state=AuthorState.WRITER;
					}
					else if (label.contains("art:"))
					{
						state=AuthorState.ART;
					}
					else if (label.contains("pencils:"))
					{
						state=AuthorState.PENCIL;
					}
					else if (label.contains("ink:"))
					{
						state=AuthorState.INK;
					}
					else if (label.contains("plot:"))
					{
						state=AuthorState.PLOT;
					}
					else if (label.contains("colours:"))
					{
						state=AuthorState.COLOUR;
					}
					else if (label.contains("script:"))
					{
						state=AuthorState.SCRIPT;
					}
					else if (label.contains("idea:"))
					{
						state=AuthorState.IDEA;
					}
					else
					{
						LOG.error("Unrecognized author label: " + label);
					}
					break;
				case Node.ELEMENT_NODE:
					CodedItem codedItem=TextProcessingUtils.matchCodedItem(node);
					if ("?".equals(codedItem.getName()))
					{
						// Artista desconocido: Ignorar.
					}
					else
					{
						Artist artist=ArtistFactory.getInstance().getByCode(codedItem.getCode(), codedItem.getName());
						switch (state)
						{
							case INK:
								story.getInks().add(artist);
								break;
							case ART:
								story.getArts().add(artist);
								break;
							case PENCIL:
								story.getPencils().add(artist);
								break;
							case WRITER:
								story.getWriters().add(artist);
								break;
							case PLOT:
								story.getPlots().add(artist);
								break;
							case COLOUR:
								story.getColours().add(artist);
								break;
							case SCRIPT:
								story.getScripts().add(artist);
								break;
							case IDEA:
								story.getIdeas().add(artist);
								break;
							case INITIAL:
								LOG.error("artist=" + artist + " with no label specified");
								break;
							default:
								throw new IllegalStateException();
						}
					}
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
			NodeList nodeList=(NodeList)getXpath().evaluate("a|text()", appearancesElement, XPathConstants.NODESET);
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
		String imgUri=(String)getXpath().evaluate("/html/body/div[@role='main']//div[@class='image']/img/@src", getDoc(), XPathConstants.STRING);
		String imgUriId=TextProcessingUtils.getValueFromUrlArguments(imgUri, "image");
		if (imgUriId == null)
		{
			imgUriId=imgUri.toString();
		}
		imgUriId=toFileName(imgUriId);
		URL url=new URL(getBaseUrl(), imgUri);
		Image image=ImageFactory.getInstance().getImage(url, imgUriId);
		return image;
	}

	private static String toFileName(String src)
	{
		return src.replaceAll("[\\:/\\\\\\?\\&]+", "_");
	}

	int parsePages()
		throws XPathExpressionException
	{
		int pages=-1;
		NodeList allTextChildren=(NodeList)getXpath().evaluate("/html/body/div[@role='main']//text()", getDoc(), XPathConstants.NODESET);
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
		String h1=(String)getXpath().evaluate("//h1", getDoc(), XPathConstants.STRING);
		int p=h1.indexOf(':');
		int p2=h1.indexOf('#', p);
		return p2 >= 0
			? null
			: h1.substring(1 + p).trim();
	}

	LocalDate parseDate()
		throws XPathExpressionException
	{
		String date=((String)getXpath().evaluate("//time/@datetime", getDoc(), XPathConstants.STRING)).trim();
		return date.isEmpty()
			? null
			: LocalDate.parse((date.length() >= 8)
				? date
				: date + "-01");
	}

	// String parseNumber()
	// throws XPathExpressionException,
	// ParseException
	// {
	// String title=(String)getXpath().evaluate("//h1", getDoc(), XPathConstants.STRING);
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
	// return new URL((String)getXpath().evaluate("/html/head/base@href", getDoc(), XPathConstants.STRING));
	// }

	Publisher parsePublisher(String number)
		throws XPathExpressionException
	{
		NodeList allTextChildren=(NodeList)getXpath().evaluate("/html/body/div[@role='main']//text()", getDoc(), XPathConstants.NODESET);
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
		NodeList allTextChildren=(NodeList)getXpath().evaluate("/html/body/div[@role='main']//text()", getDoc(), XPathConstants.NODESET);
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
