package com.youmin.imsystem.common.common.utils.discover;

import cn.hutool.core.util.StrUtil;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class PriortizedUrlDiscover extends AbstractUrlDiscover{
    private final List<UrlTitleDiscover> urlDiscover = new ArrayList<>(2);

    public PriortizedUrlDiscover(){
        urlDiscover.add(new CommonUrlDiscover());
        urlDiscover.add(new WxUrlDiscover());
    }
    @Override
    public String getTitle(Document document) {
        for (int i = 0; i < urlDiscover.size(); i++) {
            String title = urlDiscover.get(i).getTitle(document);
            if(StrUtil.isNotBlank(title)){
                return title;
            }
        }
        return null;
    }

    @Override
    public String getImage(String url, Document document) {
        for (int i = 0; i < urlDiscover.size(); i++) {
            String image = urlDiscover.get(i).getImage(url,document);
            if(StrUtil.isNotBlank(image)){
                return image;
            }
        }
        return null;
    }

    @Override
    public String getDescription(Document document) {
        for (int i = 0; i < urlDiscover.size(); i++) {
            String description = urlDiscover.get(i).getDescription(document);
            if(StrUtil.isNotBlank(description)){
                return description;
            }
        }
        return null;
    }
}
