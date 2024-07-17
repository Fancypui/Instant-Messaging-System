package com.youmin.imsystem.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.youmin.imsystem.websocket.domain.enums.WSReqTypeEnum;
import com.youmin.imsystem.websocket.domain.enums.WSRespTypeEnum;
import com.youmin.imsystem.websocket.domain.vo.req.WSReqBase;
import com.youmin.imsystem.websocket.service.impl.WebsocketServiceImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {



    private WebsocketServiceImpl websocketService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active go first");
        this.websocketService = SpringUtil.getBean(WebsocketServiceImpl.class);
        websocketService.connect(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userevent go first");
        if(evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            String token = NettyUtils.getAttribute(ctx.channel(), NettyUtils.TOKEN);
            if(StrUtil.isNotBlank(token)) {
                this.websocketService.authorize(ctx.channel(), token);
            }
        }
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent =  (IdleStateEvent)evt;
            if(idleStateEvent.state() == IdleState.READER_IDLE){
                System.out.println("user offline");
                //todo user offline logic
//                userOffline(ctx);
            }
        }
    }

    /**
     * remove binding relationship between channel and user'uid
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("Trigger user inactive   Offline![{}]",ctx.channel().id());
        userOffline(ctx);
    }

    private void userOffline(ChannelHandlerContext ctx){
        this.websocketService.remove(ctx.channel());
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        WSReqBase wsReqBase = JSONUtil.toBean(text, WSReqBase.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsReqBase.getType());
        switch (wsReqTypeEnum){
            case LOGIN:
                /**
                 * request qr url from wechat and send back to client-side
                 */
                this.websocketService.handleLoginReq(ctx.channel());
                log.info("Request for QR code = "+msg.text());
                break;
            case AUTHORIZATION:

                break;
            case HEARTBEAT:
                break;

        }
    }
}
