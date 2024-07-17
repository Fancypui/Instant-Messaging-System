package com.youmin.imsystem.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Optional;

public class HttpHeaderHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            //get token
            String token = Optional.ofNullable(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString)
                    .orElse("");
            //set token to channel, so that the next and future handler and event can used the token
            NettyUtils.setAttribute(ctx.channel(),NettyUtils.TOKEN,token);
            request.setUri(urlBuilder.getPath().toString());


        }

        ctx.fireChannelRead(msg);
    }
}
