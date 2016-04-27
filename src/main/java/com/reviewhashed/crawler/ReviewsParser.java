package com.reviewhashed.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.parser.NotAllowedContentException;
import edu.uci.ics.crawler4j.parser.Parser;

public class ReviewsParser extends Parser {

	public ReviewsParser(CrawlConfig config) {
		super(config);
		System.out.println("calling this config");
	}
	
	@Override
	public void parse(Page page, String contextURL) throws NotAllowedContentException, ParseException {
//		super.parse(page, contextURL);
		System.out.println("Parse function of child");
		System.exit(0);
	}

}
