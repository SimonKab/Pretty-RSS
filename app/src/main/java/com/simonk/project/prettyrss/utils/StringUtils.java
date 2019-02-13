package com.simonk.project.prettyrss.utils;

import org.jsoup.Jsoup;

public class StringUtils {

    public static String removeHtmlFromRssDescription(String rssDescriptionRaw) {
        return Jsoup.parse(rssDescriptionRaw).text();
    }

}
