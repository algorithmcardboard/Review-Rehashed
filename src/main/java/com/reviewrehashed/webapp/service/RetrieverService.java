package com.reviewrehashed.webapp.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import com.reviewrehashed.indexer.HTMLParser;

@Service
public class RetrieverService {

	private String indexDirPath = "D:/JAVA Workspace/Review-Rehashed/build/libs/luceneIndex";

	public List<Document> search(String featureQuery, String productQuery) throws Exception {

		File indexDir = new File(indexDirPath);
		List<Document> resultDocs = new ArrayList<>();

		if (!indexDir.exists() || !indexDir.isDirectory()) {
			throw new Exception(indexDir + " does not exist or is not a directory.");
		}

		Directory fsDir = FSDirectory.open(indexDir.toPath());
		IndexSearcher is = new IndexSearcher(DirectoryReader.open(fsDir));

		Query productQuery1 = new TermQuery(new Term(HTMLParser.REVIEW_TITLE, productQuery));
		Query productQuery2 = new TermQuery(new Term(HTMLParser.PRODUCT_TITLE, productQuery));
		Query productQuery3 = new TermQuery(new Term(HTMLParser.REVIEW_CONTENT, productQuery));

		Builder productQueryBuilder = new BooleanQuery.Builder();
		BooleanQuery booleanProductQuery = productQueryBuilder.add(productQuery1, BooleanClause.Occur.SHOULD)
				.add(productQuery2, BooleanClause.Occur.SHOULD).add(productQuery3, BooleanClause.Occur.SHOULD).build();

		Query featureQuery1 = new TermQuery(new Term(HTMLParser.REVIEW_CONTENT, featureQuery));
		Query featureQuery2 = new TermQuery(new Term(HTMLParser.REVIEW_TITLE, featureQuery));

		Builder featureQueryBuilder = new BooleanQuery.Builder();
		BooleanQuery booleanFeatureQuery = featureQueryBuilder.add(featureQuery1, BooleanClause.Occur.MUST)
				.add(featureQuery2, BooleanClause.Occur.SHOULD).build();

		Builder queryBuilder = new BooleanQuery.Builder();
		BooleanQuery booleanQuery = queryBuilder.add(booleanProductQuery, BooleanClause.Occur.SHOULD)
				.add(booleanFeatureQuery, BooleanClause.Occur.SHOULD).build();

		long start = new Date().getTime();
		TopDocs hits = is.search(booleanQuery, 1);
		long end = new Date().getTime();
		System.err.println("Found " + hits.totalHits + " document(s) (in " + (end - start)
				+ " milliseconds) that matched query '" + productQuery + ": " + featureQuery + "':");

		for (int i = 0; i < hits.scoreDocs.length; i++) {
			ScoreDoc scoreDoc = hits.scoreDocs[i];
			resultDocs.add(is.doc(scoreDoc.doc));
		}
		return resultDocs;
	}
}
