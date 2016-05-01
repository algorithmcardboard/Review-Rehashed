package com.reviewrehashed.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.reviewrehashed.indexer.HTMLParser;
import com.reviewrehashed.webapp.dto.SearchResult;
import com.reviewrehashed.webapp.dto.SearchResult.Builder;
import com.reviewrehashed.webapp.service.RetrieverService;

import uk.org.lidalia.slf4jext.LoggerFactory;

@Controller
public class SearchController {

  private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
  // Tells the application context to inject an instance of UserService here
  @Autowired
  private RetrieverService retrieverService;

  private Gson gson = new Gson();

  @ResponseBody
  @RequestMapping("search.do")
  public ResponseEntity<String> search(@RequestParam("feature") String feature,
      @RequestParam("product") String product) {
    List<SearchResult> searchResults = new ArrayList<>();
    List<Document> results = new ArrayList<>();

    try {
      results = retrieverService.search(feature, product);
    } catch (Exception e) {
      e.printStackTrace();
      logger.info("Error ", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    System.out.println("Results size is " + results.size());
    for (Document doc : results) {
      Builder b = new Builder();
      b.addAsin(doc.get(HTMLParser.ASIN))
      .addProductTitle(doc.get(HTMLParser.PRODUCT_TITLE))
      .addReviewID(doc.get(HTMLParser.REVIEW_ID))
      .addNumComments(doc.getField(HTMLParser.NUMBER_COMMENTS).numericValue().intValue())
      .addNumFoundHelpful(doc.getField(HTMLParser.NUMBER_FELT_HELPFUL).numericValue().intValue())
      .addNumImages(doc.getField(HTMLParser.NUMBER_OF_IMAGES).numericValue().intValue())
      .addNumStars(doc.getField(HTMLParser.NUMBER_OF_STARS).numericValue().floatValue())
      .addProductURL(doc.get(HTMLParser.PRODUCT_URL))
      .addReviewContent(doc.get(HTMLParser.REVIEW_CONTENT))
      .addVerifiedPurchase(doc.get(HTMLParser.VERIFIED_PURCHASE))
      .addReviewTitle(doc.get(HTMLParser.REVIEW_TITLE))
      .addReviewLength(doc.getField(HTMLParser.REVIEW_LENGTH).numericValue().intValue())
      .build();
      searchResults.add(b.build());
    }

    String json = gson.toJson(searchResults);
    return new ResponseEntity<String>(json, HttpStatus.CREATED);
  }
}
