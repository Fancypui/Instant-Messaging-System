package com.youmin.imsystem.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.jsoup.nodes.Document;

public class CommonUrlDiscover extends AbstractUrlDiscover{
    @Override
    public String getTitle(Document document) {
        return document.title();
    }

    @Override
    public String getImage(String url,Document document) {
        String image = document.select("link[type=image/x-icon]").attr("href");
        String href = StrUtil.isEmpty(image) ? document.select("link[rel$=icon]").attr("href") : image;
        if (StrUtil.containsAny(url, "favicon")) {
            return url;
        }
        if (isConnect(!StrUtil.startWith(href, "http") ? "http:" + href : href)) {
            return href;
        }
        return StrUtil.format("{}/{}", url, StrUtil.removePrefix(href, "/"));


    }

    @Override
    public String getDescription(Document document) {
        String description = document.head().select("meta[name=description]").attr("content");
        String keyword = document.head().select("meta[name=keywords]").attr("content");
        String content = StrUtil.isBlank(description)?keyword:description;
        return StrUtil.isNotBlank(content) ? content.substring(0, content.indexOf("。")) : content;
    }
}
