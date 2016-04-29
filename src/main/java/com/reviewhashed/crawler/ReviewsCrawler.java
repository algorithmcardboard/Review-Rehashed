package com.reviewhashed.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import uk.org.lidalia.slf4jext.LoggerFactory;

public class ReviewsCrawler extends WebCrawler {
  private static final Logger logger = LoggerFactory.getLogger(ReviewsCrawler.class);
  private static File storageFolder;
  private static File crawlDataFolder;
  private static Set<String> allowedASINs;

  @Override
  public void init(int id, CrawlController crawlController) {
    super.init(id, crawlController);
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

  public static void setAllowedUrls(Set<String> allowedUrls) {
    allowedASINs = allowedUrls;
  }

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {

    String urlString = url.getURL().toLowerCase();
    String asin = urlString.substring(urlString.lastIndexOf("/") + 1,
        urlString.indexOf('?') == -1 ? urlString.length() : urlString.indexOf('?')).toLowerCase();

    if (allowedASINs.contains(asin)) {
      logger.info("Returning True " +url.getURL().toLowerCase() + " " + asin);
      return true;
    }
    logger.info("Returning false " + url.getURL().toLowerCase() + " " + asin);
    return false;
  }

  @Override
  public void visit(Page page) {

    //String review = getReviewInfo(page);

    
    String urlString = page.getWebURL().getURL();
    int pageNumber = 0;
    logger.info("Visiting : " + urlString);
    try {
      List<NameValuePair> parse = URLEncodedUtils.parse(new URI(urlString), "UTF-8");
      for (NameValuePair nvPair : parse) {
        if("pagenumber".equalsIgnoreCase(nvPair.getName())){;
          pageNumber = Integer.parseInt(nvPair.getValue());
        }
      }
    } catch (URISyntaxException e1) {
      e1.printStackTrace();
    }
    String asin = urlString.substring(urlString.lastIndexOf("/") + 1,
        urlString.indexOf('?') == -1 ? urlString.length() : urlString.indexOf('?')).toLowerCase();
    
    File folder = new File(crawlDataFolder+"/"+ asin + "/reviews" );
    if (!folder.exists()) {
      folder.mkdirs();
    }
    FileOutputStream fos = null;
    try {
      logger.info("Visited page now"+ page.getWebURL());
      File file = new File(folder.getPath()+"/"+pageNumber+".html");
      fos = new FileOutputStream(file);
      fos.write(page.getContentData());
    } catch (IOException e) {
      e.printStackTrace();
    }
    finally{
      try{
        if (fos != null){
          fos.close();
        }
      }catch (IOException ioe) {
        logger.error("Error while closing stream: " + ioe);
      }
    }
  }

  private String getReviewInfo(File page) throws IOException{
    Document doc = Jsoup.parse(page,"UTF-8","");
    Elements features = doc.select("div#feature-bullets").select("li:not(.aok-hidden");
    StringBuilder sb = new StringBuilder();
    for(Element feature : features){
      sb.append(feature.text());
      sb.append("\n");
    }
    return sb.toString();
  }
}
