package com.reviewhashed.crawler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.parser.ExtractedUrlAnchorPair;
import edu.uci.ics.crawler4j.parser.HtmlContentHandler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.NotAllowedContentException;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;

public class ReviewsParser extends Parser {
  
  protected static final Logger logger = LoggerFactory.getLogger(ReviewsParser.class);

  
  private final HtmlParser htmlParser;
  private final ParseContext parseContext;

  public ReviewsParser(CrawlConfig config) {
    super(config);
    htmlParser = new HtmlParser();
    parseContext = new ParseContext();
    parseContext.set(HtmlMapper.class, new ReviewsHtmlMapper());
    
  }
  
  

//  public ReviewsParser(CrawlConfig config) {
//    super(config);
//    System.out.println("calling this config");
//  }

  @Override
  public void parse(Page page, String contextURL) throws NotAllowedContentException, ParseException {
    //		super.parse(page, contextURL);

    Metadata metadata = new Metadata();
    ReviewContentHandler contentHandler = new ReviewContentHandler();
    try (InputStream inputStream = new ByteArrayInputStream(page.getContentData())) {
      htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
    } catch (Exception e) {
      logger.error("{}, while parsing: {}", e.getMessage(), page.getWebURL().getURL());
      throw new ParseException();
    }

    if (page.getContentCharset() == null) {
      page.setContentCharset(metadata.get("Content-Encoding"));
    }

    HtmlParseData parseData = new HtmlParseData();
    parseData.setText(contentHandler.getBodyText().trim());
    parseData.setTitle(metadata.get(DublinCore.TITLE));
    parseData.setMetaTags(contentHandler.getMetaTags());
    // Please note that identifying language takes less than 10 milliseconds
    LanguageIdentifier languageIdentifier = new LanguageIdentifier(parseData.getText());
    page.setLanguage(languageIdentifier.getLanguage());

    Set<WebURL> outgoingUrls = new HashSet<>();

    String baseURL = contentHandler.getBaseUrl();
    if (baseURL != null) {
      contextURL = baseURL;
    }

    int urlCount = 0;
    for (ExtractedUrlAnchorPair urlAnchorPair : contentHandler.getOutgoingUrls()) {

      String href = urlAnchorPair.getHref();
      if ((href == null) || href.trim().isEmpty()) {
        continue;
      }

      String hrefLoweredCase = href.trim().toLowerCase();
      if (!hrefLoweredCase.contains("javascript:") && !hrefLoweredCase.contains("mailto:") &&
          !hrefLoweredCase.contains("@")) {
        String url = URLCanonicalizer.getCanonicalURL(href, contextURL);
        if (url != null) {
          WebURL webURL = new WebURL();
          webURL.setURL(url);
          webURL.setTag(urlAnchorPair.getTag());
          webURL.setAnchor(urlAnchorPair.getAnchor());
          outgoingUrls.add(webURL);
          urlCount++;
          if (urlCount > config.getMaxOutgoingLinksToFollow()) {
            break;
          }
        }
      }
    }
    parseData.setOutgoingUrls(outgoingUrls);

    try {
      if (page.getContentCharset() == null) {
        parseData.setHtml(new String(page.getContentData()));
      } else {
        parseData.setHtml(new String(page.getContentData(), page.getContentCharset()));
      }

      page.setParseData(parseData);
    } catch (UnsupportedEncodingException e) {
      logger.error("error parsing the html: " + page.getWebURL().getURL(), e);
      throw new ParseException();
    }
  }


  public String getReview(File Page){

    return null;

  }
}
