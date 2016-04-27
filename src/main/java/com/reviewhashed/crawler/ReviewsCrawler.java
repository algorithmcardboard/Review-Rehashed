package com.reviewhashed.crawler;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class ReviewsCrawler extends WebCrawler {
	private static File storageFolder;
	private static Set<String> allowedASINs;

	@Override
	public void init(int id, CrawlController crawlController) {
		super.init(id, crawlController);
		System.out.println("Calling the init here");
		try {
			Field declaredField = this.getClass().getSuperclass().getDeclaredField("parser");
			declaredField.setAccessible(true);
			declaredField.set(this, new ReviewsParser(crawlController.getConfig()));
			
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void configure(String storageFolderName) {

		storageFolder = new File(storageFolderName);
		if (!storageFolder.exists()) {
			storageFolder.mkdirs();
		}
	}

	public static void setAllowedUrls(Set<String> allowedUrls) {
		allowedASINs = allowedUrls;
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {

		String urlString = url.getURL().toLowerCase();
		String asin = urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length());

		if (allowedASINs.contains(asin)) {
			System.out.println("returning true " + urlString);
			System.exit(0);
			return true;
		}
		System.out.println("Returning false" + url.getURL().toLowerCase() + " " + asin);
		return false;
	}

	@Override
	public void visit(Page page) {
		super.visit(page);
	}
}
