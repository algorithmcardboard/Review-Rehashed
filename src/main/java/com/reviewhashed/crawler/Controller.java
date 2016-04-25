package com.reviewhashed.crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	@Parameter(names = { "-rootFolder", "-r" }, required = true)
	private String rootFolder;

	@Parameter(names = { "-location", "-l" }, required = true)
	private static String crawlLocation;

	@Parameter(names = { "-ncrawl", "-n" }, required = true)
	private int numCrawlers;

	@Parameter(names = { "-seedUrls", "-s" }, required = true)
	private String seedUrlsFile;

	private void startCrawl() throws Exception {
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlLocation);
		config.setUserAgentString("Reviewrehash - http://cs.nyu.edu/~ajr619/crawlbot.html");
		config.setIncludeBinaryContentInCrawling(true);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		Set<String> allowedUrls = new HashSet<>();

		try (BufferedReader br = new BufferedReader(new FileReader(this.seedUrlsFile))) {
			for (String domain; (domain = br.readLine()) != null;) {
				String seedUrl = getAmazonUrl(domain);
				System.out.println("Adding domain " + domain);
				controller.addSeed(seedUrl);
				allowedUrls.add(domain.toLowerCase());
			}
		}
		AmazonCrawler.configure(crawlLocation);
		AmazonCrawler.setAllowedUrls(allowedUrls);

		controller.start(AmazonCrawler.class, this.numCrawlers);
	}

	private String getAmazonUrl(String domain) {
		return "http://www.amazon.com/test-crawling/dp/"+domain;
	}

	public static void main(String[] args) {
		Controller c = new Controller();
		new JCommander(c, args);
		try {
			c.startCrawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
