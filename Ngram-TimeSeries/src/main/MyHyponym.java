package main;
import browser.NgordnetQueryType;
import edu.princeton.cs.algs4.In;
import ngrams.NGramMap;
import java.util.*;

import static browser.NgordnetQueryType.ANCESTORS;


public class MyHyponym {
    HashMap<String, HashSet<String>> synsetIntWord;
    HashMap<String, HashSet<String>> synsetWordInt;
    NGramMap ngm;
    MyGraph theGraph;
    MyGraph hyperGraph;
    public MyHyponym(String wordsFile, String countsFile, String synset, String hyponyms) {
        ngm = new NGramMap(wordsFile, countsFile);
        theGraph = new MyGraph();
        hyperGraph = new MyGraph();
        In hyponymIn = new In(hyponyms);
        In synsetIn = new In(synset);
        synsetIntWord = new HashMap<>();
        synsetWordInt = new HashMap<>();
        while (hyponymIn.hasNextLine()) {
            String inds = hyponymIn.readLine();
            String[] indices = inds.split(",");
            String id = indices[0];

            for (int i = 1; i < indices.length; i++) {
                String hyperId = indices[i];
                if (!hyperGraph.graph.containsKey(hyperId)) {
                    hyperGraph.addVertex(hyperId);
                }
                if (!hyperGraph.graph.containsKey(id)) {
                    hyperGraph.addVertex(id);
                }
                hyperGraph.addEdge(hyperId, id);
            }


            if (!theGraph.graph.containsKey(id)) {
                theGraph.addVertex(id);
            }
            for (String otherId : indices) {
                if (!theGraph.graph.containsKey(otherId)) {
                    theGraph.addVertex(otherId);
                }
                theGraph.addEdge(id, otherId);
            }
        }
        while (synsetIn.hasNextLine()) {
            String words = synsetIn.readLine();
            String[] wordArray = words.split(",");
            String index = wordArray[0];
            String synonyms = wordArray[1];
            String[] synonymsList = synonyms.split(" ");
            if (!synsetIntWord.containsKey(index)) {
                synsetIntWord.put(index, new HashSet<>());
            }
            for (String syn : synonymsList) {
                synsetIntWord.get(index).add(syn);
                if (!synsetWordInt.containsKey(syn)) {
                    synsetWordInt.put(syn, new HashSet<>());
                }
                synsetWordInt.get(syn).add(index);
            }
        }
    }

    public TreeSet<String> intGraphToWords(TreeSet<String> graphInts) {
        TreeSet<String> wordOutput = new TreeSet<>();
        for (String index : graphInts) {
            HashSet<String> words = synsetIntWord.get(index);
            if (words != null) {
                wordOutput.addAll(words);
            }
        }
        return wordOutput;
    }

    public TreeSet<String> viewHyponyms(String word) {
        TreeSet<String> wordCollection = new TreeSet<>();
        if (synsetWordInt.containsKey(word)) {
            for (String synsetId : synsetWordInt.get(word)) {
                if (theGraph.graph.containsKey(synsetId)) {
                    wordCollection.addAll(theGraph.graph.get(synsetId));
                }
            }
        }
        return (intGraphToWords(theGraph.dfs(wordCollection)));
    }
    public TreeSet<String> viewHypernyms(String word) {
        TreeSet<String> wordCollection = new TreeSet<>();
        if (synsetWordInt.containsKey(word)) {
            for (String synsetId : synsetWordInt.get(word)) {
                if (hyperGraph.graph.containsKey(synsetId)) {
                    wordCollection.addAll(hyperGraph.graph.get(synsetId));
                }
            }
        }
        return (intGraphToWords(hyperGraph.dfs(wordCollection)));
    }

    public TreeSet<String> helperViewHypernyms(List<String> words, Integer k, Integer startYear, Integer endYear) {
        int inputLength = words.size();
        TreeSet<String> same = new TreeSet<>();
        HashMap<String, Integer> counterMap = new HashMap<>();
        for (String word : words) {
            TreeSet<String> hypos = viewHypernyms(word);
            for (String hypo : hypos) {
                if (!counterMap.containsKey(hypo)) {
                    counterMap.put(hypo, 1);
                } else {
                    counterMap.put(hypo, counterMap.get(hypo) + 1);
                }
            }
        }
        for (String key : counterMap.keySet()) {
            if (counterMap.get(key) == inputLength) {
                same.add(key);
            }
        }
        TSHelper tsHelp = new TSHelper(same, ngm);
        TreeSet<String> finalOut = tsHelp.findPopular(k, startYear, endYear);
        return finalOut;
    }

    public TreeSet<String> helperViewHyponyms(List<String> words, Integer k, Integer startYear, Integer endYear) {
        int inputLength = words.size();
        TreeSet<String> same = new TreeSet<>();
        HashMap<String, Integer> counterMap = new HashMap<>();
        for (String word : words) {
            TreeSet<String> hypos = viewHyponyms(word);
            for (String hypo : hypos) {
                if (!counterMap.containsKey(hypo)) {
                    counterMap.put(hypo, 1);
                } else {
                    counterMap.put(hypo, counterMap.get(hypo) + 1);
                }
            }
        }
        for (String key : counterMap.keySet()) {
            if (counterMap.get(key) == inputLength) {
                same.add(key);
            }
        }
        TSHelper tsHelp = new TSHelper(same, ngm);
        TreeSet<String> finalOut = tsHelp.findPopular(k, startYear, endYear);
        return finalOut;
    }


    public TreeSet<String> finalDirection(
            List<String> words, Integer k, Integer startYear, Integer endYear, NgordnetQueryType direction) {
        if (direction == ANCESTORS) {
            return helperViewHypernyms(words, k, startYear, endYear);
        }
        return helperViewHyponyms(words, k, startYear, endYear);
    }
}
