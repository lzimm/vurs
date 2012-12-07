package net.vurs.service.definition.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.vurs.common.ConstructingHashMap;
import net.vurs.common.constructor.AtomicLongConstructor;
import net.vurs.common.constructor.FloatConstructor;
import net.vurs.service.definition.nlp.regex.RegexTokenizer;
import net.vurs.service.definition.nlp.token.Tokenization;
import net.vurs.util.ErrorControl;
import net.vurs.util.RelativePath;

public class TweetModel {
	public static String ENGLISH_TWEET_DUMP = "tweets.txt";
	public static Float DEFUALT_STOP_FREQUENCY = 0.01f;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private RegexTokenizer tokenizer = null;
	private List<Tokenization> tokenization = null;
	private Map<String, Float> tokenFrequencies = null;
	
	public TweetModel() {
		this.tokenizer = new RegexTokenizer();
		this.readFile();
	}
	
	public Tokenization tokenize(String line) {
		return this.tokenizer.tokenize(line);
	}

	private void readFile() {
		this.tokenization = new ArrayList<Tokenization>();
		
		File dataFile = new File(RelativePath.root().getAbsolutePath()
				.concat(File.separator)
				.concat("nlp")
				.concat(File.separator)
				.concat("data")
				.concat(File.separator)
				.concat(ENGLISH_TWEET_DUMP));

		BufferedReader in = null;
		
		try {
			this.logger.info(String.format("Reading file: %s", dataFile));
			in = new BufferedReader(new FileReader(dataFile));
			
			String line = null;
			while ((line = in.readLine()) != null) {
				Tokenization tokens = this.tokenizer.tokenize(line);
				this.tokenization.add(tokens);
			}
			
			in.close();
		} catch (Exception e) {
			ErrorControl.logException(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				ErrorControl.logException(e);
			}
		}
		
		this.tokenFrequencies = new ConstructingHashMap<String, Float>(new FloatConstructor<String>());

		Map<String, AtomicLong> tokenCount = new ConstructingHashMap<String, AtomicLong>(new AtomicLongConstructor<String>());
		
		for (Tokenization tokens: this.tokenization) {
			for (String token: tokens.tokens()) {
				tokenCount.get(token).getAndIncrement();
			}
		}
		
		this.logger.info(String.format("Counted %s unique ngrams", tokenCount.size()));
		
		float total = 0;
		float max = 0;
		float min = Float.MAX_VALUE;
		
		float setSize =  new Float(this.tokenization.size());
		for (Entry<String, AtomicLong> entry: tokenCount.entrySet()) {
			String token = entry.getKey();
			Long value = entry.getValue().get();
			Float frequency = new Float(value) / setSize;
			
			tokenFrequencies.put(token, frequency);
			
			if (frequency > max) max = frequency;
			if (frequency < min) min = frequency;
			total += frequency;
			
			if (frequency > DEFUALT_STOP_FREQUENCY) {
				this.logger.info(String.format("- %s: %s", token, frequency));
			}
		}
		
		this.logger.info(String.format("NGram Summary: Average: %s, Max: %s, Min: %s", total / tokenCount.size(), max, min));
	}

	public Float frequency(String word) {
		return this.tokenFrequencies.get(word);
	}
	
	public boolean stop(String word) {
		return this.stop(word, DEFUALT_STOP_FREQUENCY);
	}
	
	public boolean stop(String word, Float frequency) {
		return this.tokenFrequencies.get(word) > frequency;
	}
	
}
