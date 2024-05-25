package main;
import java.util.*;

import ngrams.NGramMap;
import ngrams.TimeSeries;

public class TSHelper {
    TreeSet<String> commonOutput;
    NGramMap ngm;
    public TSHelper(TreeSet<String> commonOutput, NGramMap ngm) {
        this.ngm = ngm;
        this.commonOutput = commonOutput;
    }
    public TreeSet<String> findPopular(Integer k, Integer startYear, Integer endYear) {
        if (k == 0) {
            return commonOutput; // Includes the word itself
        }
        PriorityQueue<Double> pqDouble = new PriorityQueue<>();
        HashMap<Double, List<String>> doubleString = new HashMap<>();
        for (String word : commonOutput) {
            ArrayList<String> tempList = new ArrayList<>();
            tempList.add(word);
            TimeSeries wordHistory = ngm.countHistory(word, startYear, endYear);
            Double totalCountWord = totalCount(wordHistory);
            if (doubleString.containsKey(totalCountWord)) {
                tempList.addAll(doubleString.get(totalCountWord));
                doubleString.put(totalCountWord, tempList);
            }
            doubleString.put(totalCountWord, tempList);
            if (totalCountWord <= 0) {
                continue;
            } else if (pqDouble.size() < k) {
                pqDouble.add(totalCountWord);
            } else if (pqDouble.peek() < totalCountWord) {
                pqDouble.poll();
                pqDouble.add(totalCountWord);

            }

        }
        TreeSet<String> finalOutput = new TreeSet<>();
        for (Double sizeWord: pqDouble) {
            List<String> temp = doubleString.get(sizeWord);
            for (String word : temp) {
                finalOutput.add(word);
            }
        }

        return finalOutput;
    }
    public double totalCount(TimeSeries ts) {
        double total = 0.0;
        for (Double perCount: ts.data()) {
            total += perCount;
        }
        return total;
    }
}
