package com.jdk2010.framework.rpc.provider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import com.jdk2010.framework.rpc.common.protocol.RpcDecoder;
import com.jdk2010.framework.rpc.common.protocol.RpcEncoder;
import com.jdk2010.framework.rpc.provider.annotation.Contract;
import com.jdk2010.framework.rpc.provider.handler.ContractHandler;
import com.jdk2010.framework.zookeeper.client.ZookeeperClient;

public class ContractServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractServer.class);

    private ZookeeperClient zookeeperClient;

    public ZookeeperClient getZookeeperClient() {
        return zookeeperClient;
    }

 

    public void setZookeeperClient(ZookeeperClient zookeeperClient) {
        this.zookeeperClient = zookeeperClient;
    }

    private Map<String, Object> handlerMap = new HashMap<String, Object>();

    @Override
    public void afterPropertiesSet() throws Exception {
        
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new RpcDecoder()).addLast(new RpcEncoder())
                                    .addLast(new ContractHandler(handlerMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            if (StringUtils.isEmpty(zookeeperClient.getRegisterHost())) {
                throw new RuntimeException("please makesure the registerHost is not null!");
            }

            String[] array = zookeeperClient.getRegisterHost().split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            System.out.println("host:"+host);
            System.out.println("port:"+port);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            LOGGER.debug("server started on port {}", port);
            zookeeperClient.register(zookeeperClient.getRegisterHost());
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(Contract.class);
        if (beansMap != null) {
            for (String key : beansMap.keySet()) {
                handlerMap.put(key, beansMap.get(key));
            }
        }
    }

}
