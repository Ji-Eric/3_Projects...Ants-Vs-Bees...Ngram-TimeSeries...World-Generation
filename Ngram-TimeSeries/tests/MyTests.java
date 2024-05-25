import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.NgordnetQueryType;
import main.AutograderBuddy;
import main.HyponymsHandler;
import main.MyHyponym;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static com.google.common.truth.Truth.assertThat;

public class MyTests {
    public static final String sm = "data/wordnet/hyponyms11.txt";
    public static final String sms = "data/wordnet/synsets11.txt";
    public static final String VERY_SHORT_WORDS_FILE = "data/ngrams/very_short.csv";
    public static final String TOTAL_COUNTS_FILE = "data/ngrams/total_counts.csv";
    public static final String SMALL_SYNSET_FILE = "data/wordnet/synsets16.txt";
    public static final String SMALL_HYPONYM_FILE = "data/wordnet/hyponyms16.txt";
    public static final String LARGE_SYNSET_FILE = "data/wordnet/synsets.txt";
    public static final String LARGE_HYPONYM_FILE = "data/wordnet/hyponyms.txt";

    private static final String SMALL_WORDS_FILE = "data/ngrams/top_14377_words.csv";
    private static final String WORDS_FILE = "data/ngrams/top_49887_words.csv";
    private static final String HYPONYMS_FILE_SUBSET = "data/wordnet/hyponyms1000-subgraph.txt";
    private static final String SYNSETS_FILE_SUBSET = "data/wordnet/synsets1000-subgraph.txt";
//    MyHyponym testing = new MyHyponym(SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE, TOTAL_COUNTS_FILE, WORDS_FILE);

    @Test
    public void testOccurrenceAndChangeKo88() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                VERY_SHORT_WORDS_FILE, TOTAL_COUNTS_FILE, sms, sm);
        List<String> words = List.of("descent");

        NgordnetQuery nq = new NgordnetQuery(words, 0, 0, 0, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[descent, jump, parachuting]";
        assertThat(actual).isEqualTo(expected);
    }

    // TODO: Add more unit tests (including edge case tests) here.

    @Test
    public void HardestTest() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                SMALL_WORDS_FILE, TOTAL_COUNTS_FILE, LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        List<String> words = List.of("vulgariser", "individual");
        NgordnetQuery nq = new NgordnetQuery(words, 0, 0, 0, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[populariser, popularizer, vulgariser, vulgarizer]";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void asdf() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                SMALL_WORDS_FILE, TOTAL_COUNTS_FILE, LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        List<String> words = List.of("food", "cake");

        NgordnetQuery nq = new NgordnetQuery(words, 1950, 1990, 5, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[cake, cookie, kiss, snap, wafer]";
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    public void asdfe() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                SMALL_WORDS_FILE, TOTAL_COUNTS_FILE, LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        List<String> words = List.of("dealings", "dealings");

        NgordnetQuery nq = new NgordnetQuery(words, 1470, 2019, 9, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[building, business, construction, distribution, field, industry, issue, production, trade]";
        assertThat(actual).isEqualTo(expected);
    }

    // TODO: Add more unit tests (including edge case tests) here.
    @Test
    public void asdfea() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                SMALL_WORDS_FILE, TOTAL_COUNTS_FILE, LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        List<String> words = List.of("dealings");

        NgordnetQuery nq = new NgordnetQuery(words, 1470, 2019, 1, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[business]";
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    public void hgf() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                SMALL_WORDS_FILE, TOTAL_COUNTS_FILE, LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        List<String> words = List.of("disease");

        NgordnetQuery nq = new NgordnetQuery(words, 1470, 2019, 6, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[cancer, cold, consumption, disease, dose, tuberculosis]";
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    public void asd() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                SMALL_WORDS_FILE, TOTAL_COUNTS_FILE, LARGE_SYNSET_FILE, LARGE_HYPONYM_FILE);
        List<String> words = List.of("circumstances");

        NgordnetQuery nq = new NgordnetQuery(words, 2000, 2020, 9, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[circumstances, condition, entity, fate, lot, portion, possession, relation, state]";
        assertThat(actual).isEqualTo(expected);
    }
}

// TODO: Add more unit tests (including edge case tests) here.
//Incorrect text string returned when handling query with words: [circumstances] and k = 9
//student handle() returned [destiny, entity, fate, fortune, lot, luck, portion, relation, state]
//expected handle() returned [circumstances, condition, entity, fate, lot, portion, possession, relation, state]
//
//expected: [circumstances, condition, entity, fate, lot, portion, possession, relation, state]
//but was : [destiny, entity, fate, fortune, lot, luck, portion, relation, state]
//at HyponymsHandlerTest.runRandomTests:105 (HyponymsHandlerTest.java)
//at HyponymsHandlerTest.test18:482 (HyponymsHandlerTest.java)
//Output:
//This test uses startYear = 2000, endYear = 2020
//Files: top_14377_words.csv, total_counts.csv, synsets.txt, hyponyms.txt