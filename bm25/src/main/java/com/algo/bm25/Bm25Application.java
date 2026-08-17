package com.algo.bm25;
import java.util.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BM25 {

	//public static void main(String[] args) {
	//	SpringApplication.run(Bm25Application.class, args);
	//}

	private List<String> documents;
	private double avgDocLength;
	private final double k1 = 1.5;
	private final double b = 0.75;

	public BM25(List<String> documents) {
		this.documents = documents;
		calculateAverageDocumentLength();
	}

	private void calculateAverageDocumentLength() {
		int totalLength = 0;

		for (String doc : documents) {
			totalLength += doc.split("\\s+").length;
		}

		avgDocLength = (double) totalLength / documents.size();
	}

	// Term Frequency in document
	private int termFrequency(String doc, String term) {
		int count = 0;

		for (String word : doc.toLowerCase().split("\\s+")) {
			if (word.equals(term.toLowerCase())) {
				count++;
			}
		}

		return count;
	}

	// Document Frequency
	private int documentFrequency(String term) {
		int df = 0;

		for (String doc : documents) {
			Set<String> words =
					new HashSet<>(Arrays.asList(doc.toLowerCase().split("\\s+")));

			if (words.contains(term.toLowerCase())) {
				df++;
			}
		}

		return df;
	}

	// IDF Calculation
	private double idf(String term) {
		int N = documents.size();
		int df = documentFrequency(term);

		return Math.log(
				((N - df + 0.5) / (df + 0.5)) + 1
		);
	}

	// BM25 Score for one term
	public double score(String doc, String term) {

		int tf = termFrequency(doc, term);
		int docLength = doc.split("\\s+").length;

		double numerator = tf * (k1 + 1);

		double denominator = tf +
				k1 * (1 - b + b * ((double) docLength / avgDocLength));

		return idf(term) * (numerator / denominator);
	}

	// BM25 score for a multi-term query
	public double scoreQuery(String doc, String query) {

		double score = 0.0;

		for (String term : query.toLowerCase().split("\\s+")) {
			score += score(doc, term);
		}

		return score;
	}

	public static void main(String[] args) {

		List<String> docs = Arrays.asList(
				"java is a programming language",
				"java is widely used in enterprise applications",
				"python is popular for machine learning",
				"java programming is important for backend development",
				"python is good for AIML",
				"python"
		);

		BM25 bm25 = new BM25(docs);

		String query = "python";

		for (String doc : docs) {
			double score = bm25.scoreQuery(doc, query);

			System.out.println("Document: " + doc);
			System.out.println("BM25 Score = " + score);
			System.out.println();
		}
	}
}

}
