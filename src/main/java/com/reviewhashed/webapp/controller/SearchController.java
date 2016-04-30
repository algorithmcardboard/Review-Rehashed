package com.reviewhashed.webapp.controller;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.org.lidalia.slf4jext.LoggerFactory;

@Controller
public class SearchController {
	
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@ResponseBody
	@RequestMapping("search.do")
	public String search(@RequestParam("feature")String feature, @RequestParam("product")String product){
		logger.info("Feature is ", feature, " product is ", product);
		return "Searched ";
	}
}
