package com.youmin.imsystem.websocket;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.extra.servlet.ServletUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;
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

            HttpHeaders headers = request.headers();
            String ip = headers.get("X-Real-IP");
            if(Objects.isNull(ip)){//get remote address if the request does not pass through nginx
                InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtils.setAttribute(ctx.channel(),NettyUtils.IP,ip);

        }

        ctx.fireChannelRead(msg);
    }
}
