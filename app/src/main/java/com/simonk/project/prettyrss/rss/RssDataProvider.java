package com.simonk.project.prettyrss.rss;

import com.simonk.project.prettyrss.utils.StringUtils;

import java.util.List;

public class RssDataProvider {

    public static String getImageUrlForItem(RssChannel.Item item) {
        if (item.getEnclosure() != null) {
            return item.getEnclosure().getUrl();
        }
        if (item.getMediaThumbnail() != null) {
            return item.getMediaThumbnail().getUrl();
        }
        if (item.getMediaContents() != null && item.getMediaContents().size() != 0) {
            return item.getMediaContents().get(0).getUrl();
        }
        if (item.getDescription().contains("img src")) {
            String description = item.getDescription();
            int start = description.indexOf("src=") + 5;
            int end = description.indexOf('"', start);
            return description.substring(start, end);
        }
        return null;
    }

    public static String getValidLink(List<RssChannel.Link> links) {
        for (RssChannel.Link link : links) {
            if (link.link != null) {
                return link.link;
            }
        }

        return null;
    }

    public static void removeHtmlTagsFromDescription(RssChannel rssChannel) {
        for (RssChannel.Item item : rssChannel.getChannel().getItems()) {
            item.setNormalizedDescription(StringUtils.removeHtmlFromRssDescription(item.getDescription()));
        }
    }
}
