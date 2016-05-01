package com.reviewrehashed.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.reviewrehashed.webapp.dto.SearchResult;

import uk.org.lidalia.slf4jext.LoggerFactory;

@Controller
public class SearchController {

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@ResponseBody
	@RequestMapping("search.do")
	public List<SearchResult> search(@RequestParam("feature") String feature, @RequestParam("product") String product) {
		List<SearchResult> searchResults = new ArrayList<>();

		logger.info("Feature is ", feature, " product is ", product);
		return searchResults;
	}
}
