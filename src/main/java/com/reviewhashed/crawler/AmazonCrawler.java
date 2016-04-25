package com.reviewhashed.crawler;

import java.io.File;
import java.util.Set;

import org.slf4j.Logger;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import uk.org.lidalia.slf4jext.LoggerFactory;

public class AmazonCrawler extends WebCrawler {

	private static final Logger logger = LoggerFactory.getLogger(AmazonCrawler.class);
	private static File storageFolder;
	private static Set<String> allowedASINs;

	public static void configure(String storageFolderName) {

		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) {
			storageFolder.mkdirs();
		}
	}
	
	public static void setAllowedUrls(Set<String> allowedUrls){
		allowedASINs = allowedUrls;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		
		String urlString = url.getURL().toLowerCase();
		String asin = urlString.substring(urlString.lastIndexOf("/")+1, urlString.length());
		
		if(allowedASINs.contains(asin)){
			System.out.println("returning true");
			return true;
		}
		System.out.println("Returning false" + url.getURL().toLowerCase() + " " + asin);
		return false;
	}

	@Override
	public void visit(Page page) {
		System.out.println("Visited page "+ page.getWebURL());
		super.visit(page);
	}
}
