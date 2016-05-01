package com.reviewrehashed.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.uci.ics.crawler4j.parser.ExtractedUrlAnchorPair;

public class ReviewContentHandler extends DefaultHandler {
  private static final int MAX_ANCHOR_LENGTH = 100;

  private enum Element {
    A,
    AREA,
    LINK,
    IFRAME,
    FRAME,
    EMBED,
    IMG,
    BASE,
    META,
    BODY
  }

  private static class HtmlFactory {
    private static final Map<String, Element> name2Element;

    static {
      name2Element = new HashMap<>();
      for (Element element : Element.values()) {
        name2Element.put(element.toString().toLowerCase(), element);
      }
    }

    public static Element getElement(String name) {
      return name2Element.get(name);
    }
  }

  private String base;
  private String metaRefresh;
  private String metaLocation;
  private final Map<String, String> metaTags = new HashMap<>();

  private boolean isWithinBodyElement;
  private final StringBuilder bodyText;

  private final List<ExtractedUrlAnchorPair> outgoingUrls;

  private ExtractedUrlAnchorPair curUrl = null;
  private boolean anchorFlag = false;
  private boolean isWithinPagination = false;
  private final StringBuilder anchorText = new StringBuilder();

  public ReviewContentHandler() {
    isWithinBodyElement = false;
    bodyText = new StringBuilder();
    outgoingUrls = new ArrayList<>();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    Element element = HtmlFactory.getElement(localName);
    if(localName.equalsIgnoreCase("li")){
      //System.out.println("Element is li "+ localName);
      if (attributes != null && "page-button".equalsIgnoreCase(attributes.getValue("class"))){
        isWithinPagination = true;
      }
    }
    else if (localName.equalsIgnoreCase("a")){
      if(isWithinPagination){
        String href = attributes.getValue("href");
        addToOutgoingUrls(href, localName);
      }
    }
    else if (element == Element.BODY) {
      isWithinBodyElement = true;
    }
  }

  private void addToOutgoingUrls(String href, String tag) {
    curUrl = new ExtractedUrlAnchorPair();
    curUrl.setHref(href);
    curUrl.setTag(tag);
    outgoingUrls.add(curUrl);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    Element element = HtmlFactory.getElement(localName);
    if ((element == Element.A) || (element == Element.AREA) || (element == Element.LINK)) {
      anchorFlag = false;
      if (curUrl != null) {
        String anchor = anchorText.toString().replaceAll("\n", " ").replaceAll("\t", " ").trim();
        if (!anchor.isEmpty()) {
          if (anchor.length() > MAX_ANCHOR_LENGTH) {
            anchor = anchor.substring(0, MAX_ANCHOR_LENGTH) + "...";
          }
          curUrl.setTag(localName);
          curUrl.setAnchor(anchor);
        }
        anchorText.delete(0, anchorText.length());
      }
      curUrl = null;
    }else if(isWithinPagination){
      isWithinPagination = false;
    } else if (element == Element.BODY) {
      isWithinBodyElement = false;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (isWithinBodyElement) {
      bodyText.append(ch, start, length);

      if (anchorFlag) {
        anchorText.append(new String(ch, start, length));
      }
    }
  }

  public String getBodyText() {
    return bodyText.toString();
  }

  public List<ExtractedUrlAnchorPair> getOutgoingUrls() {
    return outgoingUrls;
  }

  public String getBaseUrl() {
    return base;
  }

  public Map<String, String> getMetaTags() {
    return metaTags;
  }
}
