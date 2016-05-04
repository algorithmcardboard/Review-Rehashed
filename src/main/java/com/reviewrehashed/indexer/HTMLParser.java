package com.reviewrehashed.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import com.reviewrehashed.crawler.ReviewsController;

import uk.org.lidalia.slf4jext.LoggerFactory;

public class HTMLParser {

	public static final String REVIEW_TITLE = "reviewTitle";
	public static final String PRODUCT_TITLE = "productTitle";
	public static final String REVIEW_CONTENT = "reviewContent";
	public static final String FORMATTED_REVIEW_CONTENT = "formattedReviewContent";
	public static final String REVIEW_LENGTH = "reviewLength";
	public static final String ASIN = "ASIN";
	public static final String NUMBER_COMMENTS = "numberComments";
	public static final String NUMBER_FELT_HELPFUL = "numberFeltHelpful";
	public static final String NUMBER_OF_STARS = "numberOfStars";
	public static final String NUMBER_OF_IMAGES = "numberOfImages";
	public static final String PRODUCT_URL = "productUrl";
	public static final String VERIFIED_PURCHASE = "verifiedPurchase";
	public static final String REVIEW_DATE = "reviewDate";
	public static final String REVIEW_ID = "reviewId";
	public static final Logger logger = LoggerFactory.getLogger(ReviewsController.class);

	public String getReviewContent(Element feature) {
		String text = feature.select("span.review-text").text();
		return text;
	}

	public String getFormattedReviewContent(Element feature) {
		String text = feature.select("span.review-text").html();
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

	public int getWordCount(String content) throws IOException {
		String text = content.trim();
		String[] tokens = text.split(" ");
		return tokens.length;
	}

	private String getProductUrl(org.jsoup.nodes.Document document) {
		return document.select("div.product-info").select("div.product-title").select("a").attr("href");
	}

	public int getNumberFeltHelpful(Element feature) {
		int numberFeltHelpful = 0;
		String helpful = feature.select("span.cr-vote").select("span.a-color-secondary").text();
		if (helpful.contains("found")) {
			try {
				String str = helpful.substring(0, helpful.indexOf(' '));
				if ("one".equalsIgnoreCase(str)) {
					str = "1";
				}
				numberFeltHelpful = Integer.parseInt(str);
			} catch (IllegalArgumentException e) {
				logger.error("Exception caught for NumberFeltHelpful -  ", e);
			}
		}
		return numberFeltHelpful;
	}

	private String getProductTitle(org.jsoup.nodes.Document doc) {
		return doc.select("div.product-info").select("div.product-title").select("a[href]").text();
	}

	private int getNumberOfImages(Element feature) {
		Elements images = feature.select("div.review-image-title-section");
		return images.size();
	}

	public DateTime getReviewDate(Element feature) {
		String dateString = feature.select("span.review-date").text();
		String date = (dateString.substring(2, dateString.length())).trim();
		DateTimeFormatter dtf = DateTimeFormat.forPattern("MMM dd, YYYY");
		DateTime jodatime = dtf.parseDateTime(date);
		jodatime.toDateTime(DateTimeZone.UTC);
		return jodatime;
	}

	public ArrayList<Document> getDocument(File f) throws IOException {
		org.jsoup.nodes.Document doc = Jsoup.parse(new FileInputStream(f), "UTF-8", "");
		String productUrl = getProductUrl(doc);
		String productAsin = productUrl.substring(productUrl.lastIndexOf("/") + 1, productUrl.length());
		String productTitle = getProductTitle(doc);

		Elements features = doc.select("div.a-section.review");
		ArrayList<org.apache.lucene.document.Document> listOfDocs = new ArrayList<>();
		for (Element feature : features) {

			String reviewId = feature.attr("id");
			float numberOfStars = getRatings(feature);
			String reviewTitle = getReviewTitle(feature);
			DateTime reviewDate = getReviewDate(feature);
			String reviewContent = getReviewContent(feature);
			String formattedReviewContent = getFormattedReviewContent(feature);
			int reviewLength = getWordCount(reviewContent);
			int numberOfComments = getNumberOfComments(feature);
			int numberFeltHelpful = getNumberFeltHelpful(feature);
			int numberOfImages = getNumberOfImages(feature);
			String verifiedPurchase = isVerifiedPurchase(feature);

			org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();

			if ((reviewId != null) && (!reviewId.equals(""))) {
				document.add(new TextField(REVIEW_ID, reviewId, Field.Store.YES));
			}

			if ((reviewTitle != null) && (!reviewTitle.equals(""))) {
				document.add(new TextField(REVIEW_TITLE, reviewTitle, Field.Store.YES));
			}

			if ((productTitle != null) && (!productTitle.equals(""))) {
				document.add(new TextField(PRODUCT_TITLE, productTitle, Field.Store.YES));
			}

			if ((reviewDate != null) && (!reviewDate.equals(""))) {
				document.add(new TextField(REVIEW_DATE, DateTools.dateToString(reviewDate.toDate(), Resolution.DAY),
						Field.Store.YES));
			}

			if ((reviewContent != null) && (!reviewContent.equals(""))) {
				String[] sentences = reviewContent.split(" ");
				for(String str: sentences){
					document.add(new TextField(REVIEW_CONTENT, str, Field.Store.YES));
				}
			}

			if ((formattedReviewContent != null) && (!formattedReviewContent.equals(""))) {
				document.add(new StoredField(FORMATTED_REVIEW_CONTENT, formattedReviewContent));
			}

			if ((verifiedPurchase != null) && (!verifiedPurchase.equals(""))) {
				document.add(new TextField(VERIFIED_PURCHASE, verifiedPurchase, Field.Store.YES));
			}

			if ((productUrl != null) && (!productUrl.equals(""))) {
				document.add(new TextField(PRODUCT_URL, productUrl, Field.Store.YES));
			}

			document.add(new IntField(NUMBER_OF_IMAGES, numberOfImages, Field.Store.YES));
			document.add(new FloatField(NUMBER_OF_STARS, numberOfStars, Field.Store.YES));
			document.add(new IntField(NUMBER_FELT_HELPFUL, numberFeltHelpful, Field.Store.YES));
			document.add(new IntField(NUMBER_COMMENTS, numberOfComments, Field.Store.YES));
			document.add(new TextField(ASIN, productAsin, Field.Store.YES));
			document.add(new IntField(REVIEW_LENGTH, reviewLength, Field.Store.YES));

			listOfDocs.add(document);
			System.out.println(f.getCanonicalFile());
		}
		return listOfDocs;

	}

}
