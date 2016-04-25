package com.reviewhashed.crawler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

public class Controller {
  @Parameter(names = { "-location", "-l" }, required = true)
  private static String storageLocation;

  @Parameter(names = { "-crawlData", "-c" }, required = true)
  private static String crawlData;

  @Parameter(names = { "-ncrawl", "-n" }, required = true)
  private int numCrawlers;

  @Parameter(names = { "-seedUrls", "-s" }, required = true)
  private String seedUrlsFile;

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
    CSVReader reader = new CSVReader(new FileReader(this.seedUrlsFile), '\t','"');
    String[] newLine;
    while((newLine = reader.readNext()) != null){
      String seedUrl = getAmazonUrl(newLine[0]);
      //System.out.println("Adding domain " + domain);
      controller.addSeed(seedUrl);
      allowedUrls.add(newLine[0].toLowerCase());
    }
    reader.close();
    AmazonCrawler.configure(storageLocation,crawlData);
    AmazonCrawler.setAllowedUrls(allowedUrls);

    controller.start(AmazonCrawler.class, this.numCrawlers);
  }

  private String getAmazonUrl(String domain) {
    return "http://www.amazon.com/nyu/dp/"+domain;
  }

  private void extractProductInfo() throws IOException {
    Parser parser = new Parser();
    ArrayList<File> files = new ArrayList<File>(); 
    files = getFiles(crawlData,files);
    for (File file : files){

      String productInfo = parser.getProductInfo(file);
      String filename = file.getName().replaceFirst("[.][^.]+$", "");

      FileWriter fw = new FileWriter(crawlData + "/" + filename + "/" + filename + ".pinfo");
      fw.write(productInfo);
      fw.close();

    }

  }

  private ArrayList<File> getFiles(String directoryName, ArrayList<File> files) {
    File directory = new File(directoryName);
    File[] fileList = directory.listFiles();
    for (File file : fileList) {
      if (file.isFile()) {
        files.add(file);
      }
      else{
        getFiles(file.getAbsolutePath(), files);
      }
    }
    return files;
  }

  public static void main(String[] args) {
    Controller c = new Controller();
    new JCommander(c, args);
    try {
      c.startCrawl();
      c.extractProductInfo();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
