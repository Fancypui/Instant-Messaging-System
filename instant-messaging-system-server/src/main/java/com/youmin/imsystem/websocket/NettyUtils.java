package com.youmin.imsystem.websocket;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyUtils {

    public static final AttributeKey<String> TOKEN = AttributeKey.valueOf("TOKEN");
    public static final AttributeKey<String> IP = AttributeKey.valueOf("IP");

    public static <T> void setAttribute(Channel channel, AttributeKey<T> key, T value){
        Attribute<T> attrKey = channel.attr(key);
        attrKey.set(value);
    }

    public static <T> T getAttribute(Channel channel, AttributeKey<T> key){
        Attribute<T> attrKey = channel.attr(key);
        return attrKey.get();
    }
}
