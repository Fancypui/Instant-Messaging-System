package com.youmin.imsystem.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.logging.SocketHandler;

@Slf4j
@Configuration
public class NettyWebSocketServer {

    private int NETTY_WEBSOCKET_SERVER_PORT = 8090;

    //bossGroup is to accept the incoming request from client-side, and pass it to worker group
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    //workerGroup is a group of threads that handle channel read/write
    private EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());
    private NettyWebSocketServerHandler NETTY_WEBSOCKET_SERVER_HANDLER = new NettyWebSocketServerHandler();

    @PostConstruct
    public void start() throws InterruptedException {
        run();
    }

    @PreDestroy
    public void destroy(){
        Future<?> future = bossGroup.shutdownGracefully();
        Future<?> future1 = workerGroup.shutdownGracefully();
        future.syncUninterruptibly();
        future1.syncUninterruptibly();
        log.info("Close websocket server successfully");
    }

    private void run() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)//tell channel how to receive new calls from client-side
                .option(ChannelOption.SO_BACKLOG,128)// the maximum queue length for incoming connection indications
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new LoggingHandler(LogLevel.INFO))//add logging handler for boss group
                .childHandler(new ChannelInitializer<SocketChannel>() {


                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        /*
                            Because client-side will use HTTP protocols to upgrade to websocket communication protocol,
                            HttpServerCodec is required

                         */
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new IdleStateHandler(10,0,0));
                        pipeline.addLast(new ChunkedWriteHandler());
                        /*
                            1. When using http protocol to transmit data, it might transmit multiple segment,
                                HttpObjectAggregator is used to aggregate all the segmented data
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        /**
                         * 1. The core function of WebSocketServerProtocolHandler is to upgrade http protocol to websocket protocol
                         * 2. Browser can send request using ws://localhost(or up):8090/ to upgrade protocol
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                        pipeline.addLast(NETTY_WEBSOCKET_SERVER_HANDLER);
                    }
                });
        serverBootstrap.bind(NETTY_WEBSOCKET_SERVER_PORT).sync(); //start the netty websocket server
    }

}
