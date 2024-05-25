package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.NgordnetQueryType;

import java.util.List;

public class HyponymsHandler extends NgordnetQueryHandler {
    MyHyponym hypo;
    public HyponymsHandler(MyHyponym hypo) {
        this.hypo = hypo;
    }
    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();
        int k = q.k();
        NgordnetQueryType direction = q.ngordnetQueryType();
        return hypo.finalDirection(words, k, startYear, endYear, direction).toString();
    }
}
