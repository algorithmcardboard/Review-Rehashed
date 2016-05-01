package com.reviewrehashed.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.slf4j.Logger;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import uk.org.lidalia.slf4jext.LoggerFactory;

public class AmazonCrawler extends WebCrawler {

  private static final Logger logger = LoggerFactory.getLogger(AmazonCrawler.class);
  private static File storageFolder;
  private static File crawlDataFolder;
  private static Set<String> allowedASINs;

  public static void configure(String storageFolderName, String crawlDataLocation) {

    storageFolder = new File(storageFolderName);
    if (!storageFolder.exists()) {
      storageFolder.mkdirs();
    }
    
    crawlDataFolder = new File(crawlDataLocation);
    if (!crawlDataFolder.exists()) {
      crawlDataFolder.mkdirs();
    }
  }

  public static void setAllowedUrls(Set<String> allowedUrls){
    allowedASINs = allowedUrls;
  }

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {

    String urlString = url.getURL().toLowerCase();
    if (!urlString.contains("/dp/")){
      System.out.println("Returning false" + url.getURL().toLowerCase());
      return false;
    }
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

    String filePath = page.getWebURL().getPath();
    String[] urlSplits = filePath.split("/");
    String asin = urlSplits[urlSplits.length - 1];
    File folder = new File(crawlDataFolder+"/"+asin);
    if (!folder.exists()) {
      folder.mkdirs();
    }
    FileOutputStream fos = null;
    try {
      System.out.println("Visited page now"+ page.getWebURL());
      File file = new File(folder.getPath()+"\\"+asin+".html");
      fos = new FileOutputStream(file);
      fos.write(page.getContentData());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    finally{
      try{
        if (fos != null){
          fos.close();
        }
      }catch (IOException ioe) {
        System.out.println("Error while closing stream: " + ioe);
      }
    }
    //super.visit(page);
  }
}
