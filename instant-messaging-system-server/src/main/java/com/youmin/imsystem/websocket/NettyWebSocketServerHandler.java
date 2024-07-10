package com.youmin.imsystem.websocket;

import cn.hutool.json.JSONUtil;
import com.youmin.imsystem.websocket.domain.enums.WSReqTypeEnum;
import com.youmin.imsystem.websocket.domain.enums.WSRespTypeEnum;
import com.youmin.imsystem.websocket.domain.vo.req.WSReqBase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            System.out.println("Handshake Completed");
        }
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent =  (IdleStateEvent)evt;
            if(idleStateEvent.state() == IdleState.READER_IDLE){
                System.out.println("user offline");
                //todo user offline logic
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        WSReqBase wsReqBase = JSONUtil.toBean(text, WSReqBase.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsReqBase.getType());
        switch (wsReqTypeEnum){
            case LOGIN:
                System.out.println("websocket login request");
                ctx.writeAndFlush(new TextWebSocketFrame("hello"));
                break;
            case AUTHORIZATION:
                break;
            case HEARTBEAT:
                break;

        }
    }
}
