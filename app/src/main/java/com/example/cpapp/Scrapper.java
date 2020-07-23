package com.example.cpapp;


import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scrapper {
    public static final String TAG="SCRAPPER";
    public Set<String> codechefSolved(String parameter) throws IOException {
        Set<String> problems = new HashSet<>();
        String url = parameter;
        Document document = Jsoup.connect(url).get();
        Elements links = document.select("span > a"); // a with href
        for(Element link :links){
            problems.add(link.html());
        }
        System.out.println(problems.size()+"???????????????????????????????????");
        return problems;
    }

}
