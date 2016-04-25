package com.reviewhashed.crawler;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {

  public String getProductInfo(File page) throws IOException{
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
