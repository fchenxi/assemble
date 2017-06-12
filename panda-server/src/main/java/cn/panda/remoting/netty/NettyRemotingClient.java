package cn.panda.remoting.netty;

import cn.panda.constants.RemotingClientConfig;
import cn.panda.remoting.AbstractRemotingClient;
import cn.panda.remoting.ChannelEventListener;
import cn.panda.remoting.RemotingEvent;
import cn.panda.remoting.RemotingEventType;
import cn.panda.remoting.exception.RemotingException;
import cn.panda.remoting.protocol.RemotingCommand;
import cn.panda.util.NamedThreadFactory;
import cn.panda.util.remoting.RemotingHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @author Robert HG (254963746@qq.com) on 11/3/15.
 */
public class NettyRemotingClient extends AbstractRemotingClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyRemotingClient.class);

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroup;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
//    private AppContext appContext;

    public NettyRemotingClient(final RemotingClientConfig remotingClientConfig) {
        this(remotingClientConfig, null);
//        this.appContext = appContext;
    }

    public NettyRemotingClient(final RemotingClientConfig remotingClientConfig,
                               final ChannelEventListener channelEventListener) {
        super(remotingClientConfig, channelEventListener);

        this.eventLoopGroup = new NioEventLoopGroup(remotingClientConfig.getClientSelectorThreads(), new NamedThreadFactory("NettyClientSelectorThread_", true));
    }

    @Override
    protected void clientStart() throws RemotingException {

        NettyLogger.setNettyLoggerFactory();

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                remotingClientConfig.getClientWorkerThreads(),
                new NamedThreadFactory("NettyClientWorkerThread_")
        );

//        final NettyCodecFactory nettyCodecFactory = new NettyCodecFactory(appContext, getCodec());
        final NettyCodecFactory nettyCodecFactory = new NettyCodecFactory(getCodec());

        this.bootstrap.group(this.eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        defaultEventExecutorGroup,
                        nettyCodecFactory.getEncoder(),
                        nettyCodecFactory.getDecoder(),
                        new IdleStateHandler(remotingClientConfig.getReaderIdleTimeSeconds(), remotingClientConfig.getWriterIdleTimeSeconds(), remotingClientConfig.getClientChannelMaxIdleTimeSeconds()),//
                        new NettyConnectManageHandler(),
                        new NettyClientHandler());
            }
        });

    }

    @Override
    protected void clientShutdown() {

        this.eventLoopGroup.shutdownGracefully();

        if (this.defaultEventExecutorGroup != null) {
            this.defaultEventExecutorGroup.shutdownGracefully();
        }
    }

    @Override
    protected cn.panda.remoting.ChannelFuture connect(SocketAddress socketAddress) {
        ChannelFuture channelFuture = this.bootstrap.connect(socketAddress);
        return new cn.panda.remoting.netty.NettyChannelFuture(channelFuture);
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<RemotingCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
            processMessageReceived(new NettyChannel(ctx), msg);
        }
    }

    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
                            SocketAddress localAddress, ChannelPromise promise) throws Exception {
            final String local = localAddress == null ? "UNKNOW" : localAddress.toString();
            final String remote = remoteAddress == null ? "UNKNOW" : remoteAddress.toString();
            LOGGER.info("CLIENT : CONNECT  {} => {}", local, remote);
            super.connect(ctx, remoteAddress, localAddress, promise);

            if (channelEventListener != null) {
                assert remoteAddress != null;
                putRemotingEvent(new RemotingEvent(RemotingEventType.CONNECT, remoteAddress
                        .toString(), new NettyChannel(ctx)));
            }
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {

            cn.panda.remoting.Channel channel = new NettyChannel(ctx);

            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
            LOGGER.info("CLIENT : DISCONNECT {}", remoteAddress);
            closeChannel(channel);
            super.disconnect(ctx, promise);

            if (channelEventListener != null) {
                putRemotingEvent(new RemotingEvent(RemotingEventType.CLOSE, remoteAddress, channel));
            }
        }


        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            cn.panda.remoting.Channel channel = new NettyChannel(ctx);

            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
            LOGGER.info("CLIENT : CLOSE {}", remoteAddress);
            closeChannel(channel);
            super.close(ctx, promise);

            if (channelEventListener != null) {
                putRemotingEvent(new RemotingEvent(RemotingEventType.CLOSE, remoteAddress, channel));
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

            cn.panda.remoting.Channel channel = new NettyChannel(ctx);

            final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
            LOGGER.warn("CLIENT : exceptionCaught {}", remoteAddress);
            LOGGER.warn("CLIENT : exceptionCaught exception.", cause);
            closeChannel(channel);
            if (channelEventListener != null) {
                putRemotingEvent(new RemotingEvent(RemotingEventType.EXCEPTION, remoteAddress, channel));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;

                cn.panda.remoting.Channel channel = new NettyChannel(ctx);

                final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);

                if (event.state().equals(io.netty.handler.timeout.IdleState.ALL_IDLE)) {
                    LOGGER.warn("CLIENT : IDLE [{}]", remoteAddress);
                    closeChannel(channel);
                }

                if (channelEventListener != null) {
                    RemotingEventType remotingEventType = RemotingEventType.valueOf(event.state().name());
                    putRemotingEvent(new RemotingEvent(remotingEventType,
                            remoteAddress, channel));
                }
            }

            ctx.fireUserEventTriggered(evt);
        }
    }


}
