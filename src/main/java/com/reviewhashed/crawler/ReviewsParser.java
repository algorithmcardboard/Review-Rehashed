package com.reviewhashed.crawler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.xml.sax.ContentHandler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.parser.HtmlContentHandler;
import edu.uci.ics.crawler4j.parser.NotAllowedContentException;
import edu.uci.ics.crawler4j.parser.Parser;

public class ReviewsParser extends Parser {
	
	private final HtmlParser htmlParser;
	private final ParseContext parseContext;

	public ReviewsParser(CrawlConfig config) {
		super(config);
		htmlParser = new HtmlParser();
		parseContext = new ParseContext();
		System.out.println("calling this config");
	}
	
	@Override
	public void parse(Page page, String contextURL) throws NotAllowedContentException, ParseException {
		System.out.println("Parse function of child");
		Metadata metadata = new Metadata();
		ContentHandler contentHandler = new ReviewContentHandler();
		
		try (InputStream inputStream = new ByteArrayInputStream(page.getContentData())) {
	        htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
	      } catch (Exception e) {
	        logger.error("{}, while parsing: {}", e.getMessage(), page.getWebURL().getURL());
	        throw new ParseException();
	      }
	}

}
