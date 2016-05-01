package com.reviewrehashed.webapp.dto;

import java.util.Date;

public class SearchResult {
	private String productTitle;
	private String reviewContent;
	private int reviewLength;
	private String asin;
	private String reviewId;
	private Date reviewDate;
	private int numFoundHelpful;
	private float numStars;
	private int numComments;
	private String productURL;
	private String reviewTitle;
	private int numImages;
	private boolean verifiedPurchase;

	private SearchResult(String productTitle, String reviewContent, int reviewLength, String asin, String reviewId,
			Date reviewDate, int numFoundHelpful, float numStars, int numComments, String productURL, String reviewTitle,
			int numImages, boolean verifiedPurchase) {
		this.productTitle = productTitle;
		this.reviewContent = reviewContent;
		this.reviewLength = reviewLength;
		this.asin = asin;
		this.reviewId = reviewId;
		this.reviewDate = reviewDate;
		this.numFoundHelpful = numFoundHelpful;
		this.numStars = numStars;
		this.numComments = numComments;
		this.productURL = productURL;
		this.reviewTitle = reviewTitle;
		this.numImages = numImages;
		this.verifiedPurchase = verifiedPurchase;
	}

	/**
	 * @return the productTitle
	 */
	public String getProductTitle() {
		return productTitle;
	}

	/**
	 * @return the reviewLength
	 */
	public int getReviewLength() {
		return reviewLength;
	}

	/**
	 * @return the productURL
	 */
	public String getProductURL() {
		return productURL;
	}

	/**
	 * @return the reviewTitle
	 */
	public String getReviewTitle() {
		return reviewTitle;
	}

	/**
	 * @return the numImages
	 */
	public int getNumImages() {
		return numImages;
	}

	/**
	 * @return the verifiedPurchase
	 */
	public boolean isVerifiedPurchase() {
		return verifiedPurchase;
	}

	/**
	 * @return the asin
	 */
	public String getAsin() {
		return asin;
	}

	/**
	 * @return the reviewId
	 */
	public String getReviewId() {
		return reviewId;
	}

	/**
	 * @return the reviewContent
	 */
	public String getReviewContent() {
		return reviewContent;
	}

	/**
	 * @return the reviewDate
	 */
	public Date getReviewDate() {
		return reviewDate;
	}

	/**
	 * @return the numFoundHelpful
	 */
	public int getNumFoundHelpful() {
		return numFoundHelpful;
	}

	/**
	 * @return the numStars
	 */
	public float getNumStars() {
		return numStars;
	}

	/**
	 * @return the numComments
	 */
	public int getNumComments() {
		return numComments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asin == null) ? 0 : asin.hashCode());
		result = prime * result + numComments;
		result = prime * result + numFoundHelpful;
		result = prime * result + numImages;
		result = (int) (prime * result + numStars);
		result = prime * result + ((productTitle == null) ? 0 : productTitle.hashCode());
		result = prime * result + ((productURL == null) ? 0 : productURL.hashCode());
		result = prime * result + ((reviewContent == null) ? 0 : reviewContent.hashCode());
		result = prime * result + ((reviewDate == null) ? 0 : reviewDate.hashCode());
		result = prime * result + ((reviewId == null) ? 0 : reviewId.hashCode());
		result = prime * result + reviewLength;
		result = prime * result + ((reviewTitle == null) ? 0 : reviewTitle.hashCode());
		result = prime * result + (verifiedPurchase ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchResult other = (SearchResult) obj;
		if (asin == null) {
			if (other.asin != null)
				return false;
		} else if (!asin.equals(other.asin))
			return false;
		if (numComments != other.numComments)
			return false;
		if (numFoundHelpful != other.numFoundHelpful)
			return false;
		if (numImages != other.numImages)
			return false;
		if (numStars != other.numStars)
			return false;
		if (productTitle == null) {
			if (other.productTitle != null)
				return false;
		} else if (!productTitle.equals(other.productTitle))
			return false;
		if (productURL == null) {
			if (other.productURL != null)
				return false;
		} else if (!productURL.equals(other.productURL))
			return false;
		if (reviewContent == null) {
			if (other.reviewContent != null)
				return false;
		} else if (!reviewContent.equals(other.reviewContent))
			return false;
		if (reviewDate == null) {
			if (other.reviewDate != null)
				return false;
		} else if (!reviewDate.equals(other.reviewDate))
			return false;
		if (reviewId == null) {
			if (other.reviewId != null)
				return false;
		} else if (!reviewId.equals(other.reviewId))
			return false;
		if (reviewLength != other.reviewLength)
			return false;
		if (reviewTitle == null) {
			if (other.reviewTitle != null)
				return false;
		} else if (!reviewTitle.equals(other.reviewTitle))
			return false;
		if (verifiedPurchase != other.verifiedPurchase)
			return false;
		return true;
	}

	public static class Builder {
		private String productTitle;
		private String reviewContent;
		private int reviewLength;
		private String asin;
		private String reviewId;
		private Date reviewDate;
		private int numFoundHelpful;
		private float numStars;
		private int numComments;
		private String productURL;
		private String reviewTitle;
		private int numImages;
		private boolean verifiedPurchase;

		public Builder addProductTitle(String productTitle) {
			this.productTitle = productTitle;
			return this;
		}

		public Builder addReviewContent(String reviewContent) {
			this.reviewContent = reviewContent;
			return this;
		};

		public Builder addReviewLength(int reviewLength) {
			this.reviewLength = reviewLength;
			return this;
		}

		public Builder addAsin(String asin) {
			this.asin = asin;
			return this;
		}

		public Builder addReviewID(String reviewId) {
			this.reviewId = reviewId;
			return this;
		}

		public Builder addReviewDate(Date reviewDate) {
			this.reviewDate = reviewDate;
			return this;
		}

		public Builder addNumFoundHelpful(int numFoundHelpful) {
			this.numFoundHelpful = numFoundHelpful;
			return this;
		}

		public Builder addNumStars(float numStars) {
			this.numStars = numStars;
			return this;
		}

		public Builder addNumComments(int numComments) {
			this.numComments = numComments;
			return this;
		}

		public Builder addProductURL(String productURL) {
			this.productURL = productURL;
			return this;
		}

		public Builder addReviewTitle(String reviewTitle) {
			this.reviewTitle = reviewTitle;
			return this;
		}

		public Builder addNumImages(int numImages) {
			this.numImages = numImages;
			return this;
		}

		public Builder addVerifiedPurchase(String verifiedPurchase) {
		  this.verifiedPurchase = false;
		  if (verifiedPurchase == "true"){
		    this.verifiedPurchase = true;
		  }
			return this;
		}

		public SearchResult build() {
			return new SearchResult(this.productTitle, this.reviewContent, this.reviewLength, this.asin, this.reviewId,
					this.reviewDate, this.numFoundHelpful, this.numStars, this.numComments, this.productURL,
					this.reviewTitle, this.numImages, this.verifiedPurchase);
		}
	}

}
