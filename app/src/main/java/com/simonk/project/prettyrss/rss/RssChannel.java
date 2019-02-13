package com.simonk.project.prettyrss.rss;

import android.net.Uri;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(name = "rss")
public class RssChannel {

    @Attribute
    private String version;

    @Element(name = "channel")
    private Channel channel;

    public String getVersion() {
        return version;
    }

    public Channel getChannel() {
        return channel;
    }

    private Uri sourceUri;

    @Element(required = false)
    private String sourceUriString;

    public Uri getSourceUri() {
        if (sourceUri == null && sourceUriString != null) {
            sourceUri = Uri.parse(sourceUriString);
        }
        return sourceUri;
    }

    public void setSourceUri(Uri uri) {
        sourceUri = uri;
        sourceUriString = uri.toString();
    }

    @Root(name = "channel", strict = false)
    public static class Channel {

        @Element
        private String title;

        @ElementList(entry = "link", inline = true, required = false)
        private List<Link> links;

        @Element
        private Description description;

        @Element(required = false)
        private String language;

        @Element(required = false)
        private String pubDate;

        @Element(required = false)
        private String lastBuildDate;

        @Element(required = false)
        private int ttl;

        @Element(required = false)
        private String category;

        @Element(name = "image", required = false)
        private Image image;

        @ElementList(name = "item", inline = true)
        private List<Item> items;

        public String getTitle() {
            return title;
        }

        public List<Link> getLinks() {
            return links;
        }

        public Description getDescription() {
            return description;
        }

        public String getLanguage() {
            return language;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getLastBuildDate() {
            return lastBuildDate;
        }

        public int getTtl() {
            return ttl;
        }

        public String getCategory() {
            return category;
        }

        public Image getImage() {
            return image;
        }

        public List<Item> getItems() {
            return items;
        }
    }

    @Root(name = "description")
    public static class Description {

        @Text(required = false)
        private String text;

        public String getText() {
            return text;
        }

    }

    @Root(name = "image", strict = false)
    public static class Image {

        @Element
        private String url;

        @Element
        private String title;

        @Element
        private String link;

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

    }

    @Root(strict = false)
    public static class Link {

        @Attribute(required = false)
        public String href;

        @Attribute(required = false)
        public String rel;

        @Attribute(name = "type", required = false)
        public String contentType;

        @Text(required = false)
        public String link;

        public String getHref() {
            return href;
        }

        public String getRel() {
            return rel;
        }

        public String getContentType() {
            return contentType;
        }

        public String getLink() {
            return link;
        }
    }

    @Root(name = "item", strict = false)
    public static class Item {

        @Element(required = false)
        private String title;

        @Element(required = false)
        private String description;

        private String normalizedDescription;

        @Element(required = false)
        private String category;

        @Element(required = false)
        private String pubDate;

        @Element(required = false)
        private String guid;

        @ElementList(entry = "link", inline = true, required = false)
        private List<Link> links;

        @Element(required = false)
        private Enclosure enclosure;

        @Element(name = "thumbnail", required = false)
        @Namespace(prefix = "media")
        private MediaThumbnail mediaThumbnail;

        @ElementList(name = "content", required = false, inline = true)
        private List<MediaContent> mediaContents;

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public void setNormalizedDescription(String description) {
            this.normalizedDescription = description;
        }

        public String getNormalizedDescription() {
            return normalizedDescription == null ? description : normalizedDescription;
        }

        public String getCategory() {
            return category;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getGuid() {
            return guid;
        }

        public List<Link> getLinks() {
            return links;
        }

        public Enclosure getEnclosure() {
            return enclosure;
        }

        public MediaThumbnail getMediaThumbnail() {
            return mediaThumbnail;
        }

        public List<MediaContent> getMediaContents() {
            return mediaContents;
        }
    }

    @Root(strict = false)
    public static class MediaThumbnail {

        @Attribute(name = "url")
        private String url;

        public String getUrl() {
            return url;
        }
    }

    @Root(name = "content", strict = false)
    public static class MediaContent {

        @Attribute(name = "url")
        private String url;

        public String getUrl() {
            return url;
        }
    }

    @Root(name = "enclosure", strict = false)
    public static class Enclosure {

        @Attribute(name = "url")
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
