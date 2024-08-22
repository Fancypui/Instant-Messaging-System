package com.youmin.imsystem.common.common.utils.discover;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.data.util.Pair;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.youmin.imsystem.common.common.utils.FutureUtils;
import com.youmin.imsystem.common.common.utils.discover.domain.UrlInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
@Slf4j
public abstract class AbstractUrlDiscover implements UrlTitleDiscover{

    //Link recognize pattern
    private static final Pattern urlPattern = Pattern.compile("((http|https)://)?(www.)?([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");

    @Override
    public Map<String, UrlInfo> getContentMap(String content) {
        if(StrUtil.isBlank(content)){
            return null;
        }
        //extract url from text
        List<String> urls = ReUtil.findAll(urlPattern, content, 0);
        //parallel processing each url
        List<CompletableFuture<Pair<String, UrlInfo>>> futures = urls.stream().map(url -> CompletableFuture.supplyAsync(() -> {
            UrlInfo urlInfo = getContent(url);
            return Objects.nonNull(urlInfo) ? Pair.of(url, urlInfo) : null;
        })).collect(Collectors.toList());
        CompletableFuture<List<Pair<String, UrlInfo>>> future = FutureUtils. sequenceNonNull(futures);
        //encapsulate result
        return future.join().stream().collect(Collectors.toMap(Pair::getFirst,Pair::getSecond,(a,b)->a));
    }

    @Override
    public UrlInfo getContent(String url) {
        Document urlDocument = getUrlDocument(assemble(url));
        if(Objects.isNull(urlDocument)){
            return null;
        }
        String title = getTitle(urlDocument);
        String description = getDescription(urlDocument);
        String image = getImage(url,urlDocument);
        return UrlInfo.builder().image(image).title(title).description(description).build();
    }

    private String assemble(String url) {
        if(!StrUtil.startWith(url,"http")){
            return "http://"+url;
        }
        return url;
    }


    public Document getUrlDocument(String url){
        try {
            Connection connect = Jsoup.connect(url);
            connect.timeout(10000);
            Document document = connect.get();
            return document;
        }catch (IOException e){
            log.error("find error:url:{}", url, e);
        }
        return null;
    }

    /**
     * <link rel="shortcut icon" href="https://www.baidu.com/favicon.ico" type="image/x-icon">
     * verify if link is valid
     * @param href
     * @return
     */
    public boolean isConnect(String href){
        URL url;
        int state;
        String fileType;
        try{
            url = new URL(href);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            state = httpURLConnection.getResponseCode();
            fileType = httpURLConnection.getHeaderField("Content-Disposition");
            if((state==200||state==302||state==304)&&fileType==null){
                return true;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
