package cellhealth.sender.graphite.channel;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.Timer;


/**
 * Created by Alberto Pascual on 22/06/15.
 */
public class Pipeline implements ChannelPipelineFactory {

    private ClientBootstrap clientBootstrap;
    private Timer timer;
    private Handler handler;
    private int reconnectTimeout;
    private ChannelPipeline channelPipeline;

    public Pipeline(ClientBootstrap clientBootstrap, Timer timer){
        this.clientBootstrap = clientBootstrap;
        this.timer = timer;
        this.reconnectTimeout = 5;
    }

    public Pipeline(ClientBootstrap clientBootstrap, Timer timer, int reconnectTimeout){
        this.clientBootstrap = clientBootstrap;
        this.timer = timer;
        this.reconnectTimeout = reconnectTimeout;
    }

    public ChannelPipeline getPipeline() throws Exception {
        this.channelPipeline = pipeline();
        this.handler = new Handler(this.clientBootstrap, this.timer, this.reconnectTimeout);
        this.channelPipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        this.channelPipeline.addLast("decoder", new StringDecoder());
        this.channelPipeline.addLast("encoder", new StringEncoder());
        this.channelPipeline.addLast("handler", handler);
        return this.channelPipeline;
    }

    public void setReconnectTimeout(int timeout) {
        this.reconnectTimeout = timeout;
    }

    public ChannelPipeline getCurrentPipeline() throws Exception {
        return (this.channelPipeline != null)? channelPipeline : getPipeline();
    }
}
