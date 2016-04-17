package com.reviewhashed.crawler;

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

	private void startCrawl() throws Exception {
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlLocation);
		config.setUserAgentString("Reviewrehash - http://cs.nyu.edu/~ajr619/crawlbot.html");
		String[] crawlDomains = {"http://www.amazon.com/"};


		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		for (String domain : crawlDomains) {
      controller.addSeed(domain);
    }
		AmazonCrawler.configure(crawlDomains, crawlLocation);

		controller.start(AmazonCrawler.class, this.numCrawlers);
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
