package com.youmin.imsystem.common.common.utils.discover;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WxUrlDiscover extends AbstractUrlDiscover{
    @Override
    public String getTitle(Document document) {
        return document.getElementsByAttributeValue("property","og:title").attr("content");
    }


    @Override
    public String getImage(String url,Document document) {
        String href = document.getElementsByAttributeValue("property", "og:image").attr("content");
        return isConnect(href) ? href : null;
    }

    @Override
    public String getDescription(Document document) {
        return document.getElementsByAttributeValue("property","og:description").attr("content");
    }
}
