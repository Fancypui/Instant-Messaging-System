package com.youmin.imsystem.common.common.utils.discover;

import com.sun.istack.internal.NotNull;
import com.youmin.imsystem.common.common.utils.discover.domain.UrlInfo;
import io.micrometer.core.lang.Nullable;
import org.jsoup.nodes.Document;

import java.util.Map;


public interface UrlTitleDiscover {


    Map<String, UrlInfo> getContentMap(String content);

    UrlInfo getContent(String url);

    @Nullable
    String getTitle(Document document);

    @Nullable
    String getImage(String url,Document document);

    @Nullable
    String getDescription(Document document);

    public static void main(String[] args) {

        String longStr = "danknlknnlknaa ";
//        String longStr = "一个带有端口号的URL http://www.jd.com:80,";
//        String longStr = "一个带有路径的URL http://mallchat.cn";
        PriortizedUrlDiscover discover = new PriortizedUrlDiscover();
        final Map<String, UrlInfo> map = discover.getContentMap(longStr);
        System.out.println(map);

    }
}
