package main;

import browser.NgordnetServer;
import org.slf4j.LoggerFactory;

public class Main {
    static {
        LoggerFactory.getLogger(Main.class).info("\033[1;38mChanging text color to white");
    }
    public static void main(String[] args) {
        NgordnetServer hns = new NgordnetServer();

        String wordFile = "data/ngrams/top_49887_words.csv";
        String countsFile = "data/ngrams/total_counts.csv";
        String synsetFile = "./data/wordnet/synsets.txt";
        String hyponymFile = "./data/wordnet/hyponyms.txt";
        //        (String wordsFile, String countsFile, String synset, String hyponyms)
        MyHyponym hypo = new MyHyponym(wordFile, countsFile, synsetFile, hyponymFile);

        hns.startUp();
        hns.register("history", new DummyHistoryHandler());
        hns.register("historytext", new DummyHistoryTextHandler());
        hns.register("hyponyms", new HyponymsHandler(hypo));

        System.out.println("Finished server startup! Visit http://localhost:4567/ngordnet.html");
    }
}
