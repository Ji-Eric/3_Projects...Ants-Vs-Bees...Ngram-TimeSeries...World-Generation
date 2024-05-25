package ngrams;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * An object for mapping a year number (e.g. 1996) to numerical data. Provides
 * utility methods useful for data analysis.
 *
 * @author Josh Hug
 */
public class TimeSeries extends TreeMap<Integer, Double> {

    /** If it helps speed up your code, you can assume year arguments to your NGramMap
     * are between 1400 and 2100. We've stored these values as the constants
     * MIN_YEAR and MAX_YEAR here. */
    public static final int MIN_YEAR = 1400;
    public static final int MAX_YEAR = 2100;


    /**
     * Constructs a new empty TimeSeries.
     */
    public TimeSeries() {
        super();
    }

    /**
     * Creates a copy of TS, but only between STARTYEAR and ENDYEAR,
     * inclusive of both end points.
     */
    public TimeSeries(TimeSeries ts, int startYear, int endYear) {
        super();
        for (int i = startYear; i <= endYear; i++) {
            if (ts != null && ts.get(i) != null) {
                this.put(i, ts.get(i));
            }
        }
    }

    /**
     * Returns all years for this TimeSeries (in any order).
     */
    public List<Integer> years() {
        List<Integer> holder = new ArrayList<>();
        for (Integer i: this.keySet()) {
            holder.add(i);
        }
        return holder;
    }

    /**
     * Returns all data for this TimeSeries (in any order).
     * Must be in the same order as years().
     */
    public List<Double> data() {
        List<Double> holder = new ArrayList<>();
        for (Double i: this.values()) {
            holder.add(i);
        }
        return holder;
    }

    /**
     * Returns the year-wise sum of this TimeSeries with the given TS. In other words, for
     * each year, sum the data from this TimeSeries with the data from TS. Should return a
     * new TimeSeries (does not modify this TimeSeries).
     *
     * If both TimeSeries don't contain any years, return an empty TimeSeries.
     * If one TimeSeries contains a year that the other one doesn't, the returned TimeSeries
     * should store the value from the TimeSeries that contains that year.
     */
    public TimeSeries plus(TimeSeries ts) {
        TimeSeries time = new TimeSeries();
        if (ts.years().isEmpty() && years().isEmpty()) {
            return time;
        }
        if (ts.years().isEmpty() && !years().isEmpty()) {
            return this;
        }
        if (!ts.years().isEmpty() && years().isEmpty()) {
            return ts;
        }
        for (int year = MIN_YEAR; year <= MAX_YEAR; year++) {
            if (this.containsKey(year) && ts.containsKey(year)) {
                time.put(year, this.get(year) + ts.get(year));
            } else if (this.containsKey(year) && this.get(year) != null) {
                time.put(year, this.get(year));
            } else if (ts.containsKey(year) && ts.get(year) != null) {
                time.put(year, ts.get(year));
            }
        }
        return time;
    }

    /**
     * Returns the quotient of the value for each year this TimeSeries divided by the
     * value for the same year in TS. Should return a new TimeSeries (does not modify this
     * TimeSeries).
     *
     * If TS is missing a year that exists in this TimeSeries, throw an
     * IllegalArgumentException.
     * If TS has a year that is not in this TimeSeries, ignore it.
     */
    public TimeSeries dividedBy(TimeSeries ts) {
        TimeSeries divideList = new TimeSeries();
        for (int year = MIN_YEAR; year <= MAX_YEAR; year++) {
            if (this.containsKey(year) && !ts.containsKey(year)) {
                throw new IllegalArgumentException("Year is gone");
            }
            if (ts.containsKey(year) && ts.get(year) != null && this.get(year) != null) {
                double value = get(year) / ts.get(year);
                divideList.put(year, value);
            }
        }

        return divideList;
    }
}
