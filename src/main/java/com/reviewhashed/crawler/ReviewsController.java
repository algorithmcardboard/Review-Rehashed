package com.reviewhashed.crawler;

import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import au.com.bytecode.opencsv.CSVReader;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class ReviewsController {

  @Parameter(names = { "-location", "-l" }, required = true)
  private static String storageLocation;

  @Parameter(names = { "-seedUrls", "-s" }, required = true)
  private String seedUrlsFile;

  @Parameter(names = { "-ncrawl", "-n" }, required = true)
  private int numCrawlers;
  
  @Parameter(names = { "-crawlData", "-c" }, required = true)
  private static String crawlData;
  
  

  private String getReviewUrl(String asin, int pageNum) {
    return "http://www.amazon.com/asdf/product-reviews/"+asin+"/ref=undefined_2?filterbystar=one_star&pageNumber=1";
    //		return "http://www.amazon.com/product-reviews/" + asin + "/ref=undefined_" + pageNum + "?pageNumber=" + pageNum;
  }


  private void startCrawl() throws Exception {
    CrawlConfig config = new CrawlConfig();
    config.setCrawlStorageFolder(storageLocation);
    config.setUserAgentString("Reviewrehash - http://cs.nyu.edu/~ajr619/crawlbot.html");
    config.setIncludeBinaryContentInCrawling(true);

    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    Set<String> allowedUrls = new HashSet<>();

    CSVReader reader = new CSVReader(new FileReader(this.seedUrlsFile), ',','"');
    String[] newLine;

    while((newLine = reader.readNext()) != null){
      String asin = newLine[0];
      String seedUrl = getReviewUrl(asin,1);
      controller.addSeed(seedUrl);
      allowedUrls.add(asin.toLowerCase());
    }
    reader.close();
    ReviewsCrawler.configure(storageLocation,crawlData);
    ReviewsCrawler.setAllowedUrls(allowedUrls);

    controller.start(ReviewsCrawler.class, this.numCrawlers);
  }

  public static void main(String[] args) {
    ReviewsController c = new ReviewsController();
    new JCommander(c, args);
    try {
      c.startCrawl();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
