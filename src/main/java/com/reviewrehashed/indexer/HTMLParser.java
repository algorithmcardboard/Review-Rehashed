package com.reviewrehashed.indexer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import com.reviewhashed.crawler.ReviewsController;

import uk.org.lidalia.slf4jext.LoggerFactory;

public class HTMLParser {

  private static final Logger logger = LoggerFactory.getLogger(ReviewsController.class);

  public String getReviewContent(Element feature) {
    String text = feature.select("span.review-text").text();
    return text;
  }

  public String getReviewTitle(Element feature) {
    String title = feature.select("a.review-title").text();
    return title;
  }

  public float getRatings(Element feature) {
    String ratingsText = feature.select("span.a-icon-alt").text();
    String ratings = (ratingsText.substring(0, ratingsText.indexOf("out"))).trim();
    return Float.parseFloat(ratings);
  }

  public int getNumberOfComments(Element feature) {
    return Integer.parseInt(feature.select("span.review-comment-total.aok-hidden").text());
  }

  public String isVerifiedPurchase(Element feature) {
    if (feature.text().contains("Verified Purchase")) {
      return "true";
    }
    return "false";
  }

  public int getNumberFeltHelpful(Element feature) {
    int numberFeltHelpful = 0;
    String helpful = feature.select("span.cr-vote").select("span.a-color-secondary").text();
    if (helpful.contains("found")) {
      try {
        numberFeltHelpful = Integer.parseInt(helpful.substring(0, 1));
      } catch (IllegalArgumentException e) {
        logger.error("Exception caught for NumberFeltHelpful -  ", e);
      }
    }
    return numberFeltHelpful;
  }

  public DateTime getReviewDate(Element feature) {
    String dateString = feature.select("span.review-date").text();
    String date = (dateString.substring(2, dateString.length())).trim();
    DateTimeFormatter dtf = DateTimeFormat.forPattern("MMM dd, YYYY");
    DateTime jodatime = dtf.parseDateTime(date);
    jodatime.toDateTime(DateTimeZone.UTC);
    return jodatime;
  }

  public Document getDocument(InputStream fis, File f) throws IOException {
    org.jsoup.nodes.Document doc = Jsoup.parse(fis, "UTF-8", "");

    org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();

    Elements features = doc.select("div.a-section.review");
    for (Element feature : features) {

      String reviewId = feature.attr("id");
      float numberOfStars = getRatings(feature);
      String reviewTitle = getReviewTitle(feature);
      DateTime reviewDate = getReviewDate(feature);
      String reviewContent = getReviewContent(feature);
      int numberOfComments = getNumberOfComments(feature);
      int numberFeltHelpful = getNumberFeltHelpful(feature);
      String verifiedPurchase = isVerifiedPurchase(feature);

      if ((reviewId != null) && (!reviewId.equals(""))) {
        document.add(new TextField("reviewId", reviewId, Field.Store.YES));
      }

      if ((reviewTitle != null) && (!reviewTitle.equals(""))) {
        document.add(new TextField("reviewTitle", reviewTitle, Field.Store.YES));
      }

      if ((reviewDate != null) && (!reviewDate.equals(""))) {
        document.add(
            new TextField("reviewDate", DateTools.dateToString(reviewDate.toDate(),
                Resolution.DAY), Field.Store.YES));
      }

      if ((reviewContent != null) && (!reviewContent.equals(""))) {
        document.add(new TextField("reviewContent", reviewContent, Field.Store.YES));
      }

      if ((verifiedPurchase != null) && (!verifiedPurchase.equals(""))) {
        document.add(new TextField("verifiedPurchase", verifiedPurchase, Field.Store.YES));
      }

      document.add(new FloatField("numberOfStars", numberOfStars, Field.Store.YES));

      document.add(new IntField("numberFeltHelpful", numberFeltHelpful, Field.Store.YES));

      document.add(new IntField("numberComments", numberOfComments, Field.Store.YES));

      document.add(new StringField("filename", f.getCanonicalPath(), Field.Store.YES));
    }
    return document;

  }

}
