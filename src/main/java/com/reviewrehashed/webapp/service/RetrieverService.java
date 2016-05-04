package com.reviewrehashed.webapp.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import com.reviewrehashed.indexer.HTMLParser;
import com.reviewrehashed.lucene.similarity.CustomSimilarity;
import com.reviewrehashed.lucene.similarity.MyCustomScoreQuery;

@Service
public class RetrieverService {

	private String indexDirPath = "C:/Users/Developer/workspace/ReviewsRehashed/build/libs/luceneIndex";

	public List<Document> search(String featureQuery, String productQuery) throws Exception {

		File indexDir = new File(indexDirPath);
		List<Document> resultDocs = new ArrayList<>();

		if (!indexDir.exists() || !indexDir.isDirectory()) {
			throw new Exception(indexDir + " does not exist or is not a directory.");
		}

		Directory fsDir = FSDirectory.open(indexDir.toPath());
		IndexSearcher is = new IndexSearcher(DirectoryReader.open(fsDir));

		is.setSimilarity(new CustomSimilarity());
		Query booleanQuery = getQuery(featureQuery, productQuery);

		long start = new Date().getTime();
		CustomScoreQuery customQuery = new MyCustomScoreQuery(booleanQuery);

		TopDocs hits = is.search(customQuery, 20);

		long end = new Date().getTime();
		System.out.println("Found " + hits.totalHits + " document(s) (in " + (end - start)
				+ " milliseconds) that matched query '" + productQuery + ": " + featureQuery + "':");

		for (int i = 0; i < hits.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = hits.scoreDocs[i];
			resultDocs.add(is.doc(scoreDoc.doc));
			System.out.println(scoreDoc.score);
		}

		return resultDocs;
	}

	private Query getQuery(String feature, String product) throws ParseException {

		MultiFieldQueryParser featureParser = new MultiFieldQueryParser(
				new String[] { HTMLParser.REVIEW_CONTENT, HTMLParser.REVIEW_TITLE }, new ShingleAnalyzerWrapper(),
				new HashMap<String, Float>() {
					{
						put(HTMLParser.REVIEW_CONTENT, 0.25f);
						put(HTMLParser.REVIEW_TITLE, 0.75f);
					}
				});
		featureParser.setDefaultOperator(Operator.OR);
		Query featureQuery = featureParser.parse(feature);
		
		Builder b = (new BooleanQuery.Builder()).add(featureQuery, Occur.MUST);
		
		if(product != null && !"".equals(product.trim())){
			MultiFieldQueryParser productParser = new MultiFieldQueryParser(
					new String[] { HTMLParser.ASIN, HTMLParser.PRODUCT_TITLE }, new StandardAnalyzer());
			productParser.setDefaultOperator(Operator.AND);
			
			Query productQuery = productParser.parse(product);
			
			b.add(productQuery, Occur.MUST);
		}

		return b.build();
	}

	public static void main(String[] args) {
		RetrieverService service = new RetrieverService();
		List<Document> docs = new ArrayList<>();
		try {
			docs = service.search("resolution", "LG");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Size of docs is " + docs.size());
	}
}
