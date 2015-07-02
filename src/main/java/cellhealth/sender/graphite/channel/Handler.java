package cellhealth.sender.graphite.channel;

import cellhealth.sender.graphite.sender.GraphiteSender;
import cellhealth.utils.constants.message.*;
import cellhealth.utils.constants.message.Error;
import cellhealth.utils.logs.L4j;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alberto Pascual on 22/06/15.
 */
public class Handler extends SimpleChannelUpstreamHandler {

    private ClientBootstrap clientBootstrap;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    private Timer timer;
    private int reconnectTimeout;
    private long startTime;

    public Handler(ClientBootstrap clientBootstrap, Timer timer){
        this.clientBootstrap = clientBootstrap;
        this.timer = timer;
        this.reconnectTimeout = 5;
        this.startTime = -1;
    }

    public Handler(ClientBootstrap clientBootstrap, Timer timer, int reconnectTimeout){
        this.clientBootstrap = clientBootstrap;
        this.timer = timer;
        this.reconnectTimeout = reconnectTimeout;
        this.startTime = -1;
    }

    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) this.clientBootstrap.getOption("remoteAddress");
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        this.warning(new StringBuilder(Warning.DISCONNECT_FROM).append(this.getRemoteAddress()).toString());
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        this.warning(new StringBuilder(Warning.SLEEPING_FOR).append(this.reconnectTimeout).append("s").toString());
        this.warning(new StringBuilder(Warning.CHANNEL_CLOSED).append(GraphiteSender.isShuttingDown()).toString());
        if (!GraphiteSender.isShuttingDown()) 	  {
            timer.newTimeout(new TimerTask() {
                public void run(Timeout timeout) throws Exception {
                    try {
                        warning(new StringBuilder(Warning.RECONNECT_TO).append(getRemoteAddress()).toString());
                        clientBootstrap.connect();
                    } catch (Exception e) {
                        L4j.getL4j().error(new StringBuilder(Error.ERROR_TRY_RECONNECT).append(e.getMessage()).toString(), e);
                    }
                }}, this.reconnectTimeout, TimeUnit.SECONDS);
        }
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            this.warning(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        this.warning(new StringBuilder(Warning.GRAPHITE_SEND_MSG).append(e.getMessage()).toString());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConnectException) {
            this.startTime = -1;
            L4j.getL4j().error(new StringBuilder(Error.ERROR_CONNECT).append(cause.getMessage()).toString(), cause);
        }
        if (cause instanceof ReadTimeoutException) {
            L4j.getL4j().error(new StringBuilder(Error.DISCONNECTING_TIMEOUT).append(cause.getMessage()).toString());
        } else {
            L4j.getL4j().error(new StringBuilder(Error.DISCONNECTING_TRAFFIC).append(cause.getMessage()).toString());
        }
        ctx.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (this.startTime < 0) {
            this.startTime = System.currentTimeMillis();
        }
        this.warning(new StringBuilder(Info.CONNECT_TO).append(this.getRemoteAddress()).toString());
    }

    private void warning(String msg) {
        String date=sdf.format(new Date());
        if (startTime < 0) {
            L4j.getL4j().warning(new StringBuilder(Warning.CONNECTION_IS_DOWN).append(" ").append(msg).toString());
        } else {
            L4j.getL4j().warning(new StringBuilder("[").append(Warning.UPTIME).append(": ").append(((System.currentTimeMillis() - startTime) / 1000)).append("] ").append(msg).toString());
        }
    }
}
