package ngrams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import edu.princeton.cs.algs4.In;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {
    TimeSeries wordCount;
    TreeMap<String, TimeSeries> timeCapsule;
    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        timeCapsule = new TreeMap<>();
        wordCount = new TimeSeries();
        In wordsReader = new In(wordsFilename);
        In countsReader = new In(countsFilename);
        while (wordsReader.hasNextLine()) {
            String readWords = wordsReader.readLine();
            String[] arrayWords = readWords.split("\t");
            if (timeCapsule.containsKey(arrayWords[0])) {
                timeCapsule.get(arrayWords[0]).put(Integer.parseInt(arrayWords[1]), Double.parseDouble(arrayWords[2]));
            } else {
                TimeSeries wordSeries = new TimeSeries();
                wordSeries.put(Integer.parseInt(arrayWords[1]), Double.parseDouble(arrayWords[2]));
                timeCapsule.put(arrayWords[0], wordSeries);
            }
        }
        while (countsReader.hasNextLine()) {
            String file = countsReader.readLine();
            String[] fileArray = file.split(",");
            wordCount.put(Integer.parseInt(fileArray[0]), Double.parseDouble(fileArray[1]));
        }
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        TimeSeries empty = new TimeSeries();
        TimeSeries time = this.timeCapsule.get(word);
        if (this.timeCapsule.get(word) == null) {
            return empty;
        }
        return new TimeSeries(time, startYear, endYear);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        TimeSeries empty = new TimeSeries();
        TimeSeries time = this.timeCapsule.get(word);
        if (this.timeCapsule.get(word) == null) {
            return empty;
        }
        return time;
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        return wordCount;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        TimeSeries empty = new TimeSeries();
        TimeSeries time = this.timeCapsule.get(word);
        TimeSeries temp = new TimeSeries(time, startYear, endYear);
        if (time == null) {
            return empty;
        }
        return temp.dividedBy(wordCount);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        TimeSeries empty = new TimeSeries();
        TimeSeries time = this.timeCapsule.get(word);
        if (this.timeCapsule.get(word) == null) {
            return empty;
        }
        return time.dividedBy(wordCount);
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        ArrayList<String> collect = new ArrayList<>(words);
        TimeSeries empty = new TimeSeries();
        int i = 0;
        while (i < collect.size()) {
            String temp = collect.get(i);
            if (this.timeCapsule.get(temp) != null) {
                empty = empty.plus(weightHistory(temp, startYear, endYear));
            }
            i++;
        }
        return empty;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        ArrayList<String> collect = new ArrayList<>(words);
        TimeSeries empty = new TimeSeries();
        int i = 0;
        while (i < collect.size()) {
            String temp = collect.get(i);
            if (this.timeCapsule.get(temp) != null) {
                empty = empty.plus(weightHistory(temp));
            }
            i++;
        }
        return empty;
    }
}
