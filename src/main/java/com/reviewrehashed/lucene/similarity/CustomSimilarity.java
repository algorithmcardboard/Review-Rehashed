package com.reviewrehashed.lucene.similarity;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class CustomSimilarity extends ClassicSimilarity {

  public float computeNorm(String field, FieldInvertState state) {
    return 1.0f;
  }
}
