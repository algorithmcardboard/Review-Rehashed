package com.reviewrehashed.lucene.similarity;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.search.Query;
import org.slf4j.Logger;

import com.reviewrehashed.indexer.HTMLParser;

import uk.org.lidalia.slf4jext.LoggerFactory;

public class MyCustomScoreQuery extends CustomScoreQuery {

	private static final Logger logger = LoggerFactory.getLogger(MyCustomScoreQuery.class);

	protected static final double MUL_FACTOR = 0.01d;
	private Query query;

	public MyCustomScoreQuery(Query query) {
		super(query);
		this.query = query;
	}

	@Override
	public CustomScoreProvider getCustomScoreProvider(final LeafReaderContext context) {
		return new CustomScoreProvider(context) {
			@Override
			public float customScore(int doc, float subQueryScore, float valSrcScore) throws IOException {

				Document document = context.reader().document(doc);
				double luceneScore = super.customScore(doc, subQueryScore, valSrcScore);
				double luceneScoreNormalized = (1 / (1 + Math.exp(-luceneScore * MUL_FACTOR)));

				// Calcualting intrinisic quality of the docdument

				int numHelpful = document.getField(HTMLParser.NUMBER_FELT_HELPFUL).numericValue().intValue();
				int numImages = document.getField(HTMLParser.NUMBER_OF_IMAGES).numericValue().intValue();
				int numComments = document.getField(HTMLParser.NUMBER_COMMENTS).numericValue().intValue();
				// float numStars =
				// document.getField(HTMLParser.NUMBER_OF_STARS).numericValue().intValue();
				int isVerifiedPurchase = 0;
				if (document.getField(HTMLParser.VERIFIED_PURCHASE).stringValue().equals("true")) {
					isVerifiedPurchase = 1;
				}

				double docQuality = (0.6 * (1 / (1 + Math.exp(-numHelpful * 0.05))))
						+ (0.2 * (1 / (1 + Math.exp(-isVerifiedPurchase * 100))))
						+ (0.13 * (1 / (1 + Math.exp(-numComments * 0.1)))) 
								+ (0.07 * (1 / (1 + Math.exp(-numImages))));

				double docQualityNormalized = (1 / (1 + Math.exp(-docQuality)));

				// System.out.println(luceneScore);
				logger.info("Lucene score " + luceneScore + " " + luceneScoreNormalized);
				logger.info("Doc quality " + docQuality + " " + docQualityNormalized);

				// Calculating final score
				float finalDocScore = (float) (0.25 * docQualityNormalized + 0.75 * luceneScoreNormalized);

				return finalDocScore;
				// return (float) luceneScore;
			}
		};
	}

}
