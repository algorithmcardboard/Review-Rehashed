package com.reviewhashed.crawler;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class AmazonCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");
	private static File storageFolder;
  private static String[] crawlDomains;
  
	 public static void configure(String[] domain, String storageFolderName) {
	    crawlDomains = domain;

	    storageFolder = new File(storageFolderName);
	    if (!storageFolder.exists()) {
	      storageFolder.mkdirs();
	    }
	  }
	 
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
    String href = url.getURL().toLowerCase();
    if (FILTERS.matcher(href).matches()) {
      return false;
    }
    
    for (String domain : crawlDomains) {
      if (href.startsWith(domain)) {
        return true;
      }
    }
    return false;
  }

	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		System.out.println("URL: " + url);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			String hashedName = UUID.randomUUID().toString();
			String filename = storageFolder.getAbsolutePath() + "/" + hashedName;
	    try {
	      Files.write(page.getContentData(), new File(filename));
	      logger.info("Stored: {}", url);
	    } catch (IOException iox) {
	      logger.error("Failed to write file: " + filename, iox);
	    }
			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());
		}
	}
}
