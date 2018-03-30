package com.udb.client;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import com.udb.protocol.ChatAction;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HttpClinetHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("断开连接");
		ctx.channel().eventLoop().schedule(
				new Runnable() {
					public void run()
					{
						try {
							
							new ClientStart().init().doConnect();
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				,2, TimeUnit.SECONDS);
		ctx.close();
		
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// TODO Auto-generated method stub
		 if (evt instanceof IdleStateEvent) {
             IdleStateEvent e = (IdleStateEvent) evt;
             if (e.state() == IdleState.READER_IDLE) {
            	 System.out.println("READER_IDLE");
             } else if (e.state() == IdleState.WRITER_IDLE) {
                 System.out.println("WRITER_IDLE");
            	 //ctx.writeAndFlush(new PingMessage());
             }
         }
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	   // TODO Auto-generated method stub
	//	ByteBuf content = Unpooled.wrappedBuffer(JsonUtils.beanToJson(requestParam).getBytes("UTF-8"));
		
		//header data
/*		int  cmd=1;
		int version=0;
		String sessionId="";
		//byte cmdbyte=new Integer(cmd).byteValue();
		//byte versionbyte=new Integer(version).byteValue();
		byte[] scsessionIdbyte=sessionId.getBytes(Charset.forName("UTF-8"));
		//body data
		String userCode="afa005de990a0c3e1a92144fe05c1e00";
		String password="e10adc3949ba59abbe56e057f20f883e";
		String deviceId="";
		int deviceType=1;
		int pushCount=0;
		int loginType =0;
		byte[] userCodebyte=userCode.getBytes(Charset.forName("UTF-8"));
		byte[] passwordbyte=password.getBytes(Charset.forName("UTF-8"));
		byte[] deviceIdbyte=deviceId.getBytes(Charset.forName("UTF-8"));
		//byte deviceTypebyte=new Integer(deviceType).byteValue();
		//byte pushCountbyte=new Integer(pushCount).byteValue();
		//byte loginTypebyte=new Integer(loginTypebyte).byteValue();

		ByteBuf buf =  Unpooled.buffer(1024);
		buf.writeInt(cmd);
		buf.writeInt(version);
		buf.writeInt(scsessionIdbyte.length);
		buf.writeBytes(scsessionIdbyte);
		buf.writeInt(userCodebyte.length);
		buf.writeBytes(userCodebyte);
		buf.writeInt(passwordbyte.length);
		buf.writeBytes(passwordbyte);
		buf.writeInt(deviceIdbyte.length);
		buf.writeBytes(deviceIdbyte);
		buf.writeInt(deviceType);
		buf.writeInt(pushCount);
		buf.writeInt(loginType);
	     //URI uri = new URI("");
		HttpRequest request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,"",buf);
		request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
		request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,buf.readableBytes());
		request.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
		ctx.channel().writeAndFlush(request);*/
		
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		if(msg instanceof HttpResponse)
		{
			HttpResponse httpResponse = (HttpResponse)msg;
			System.out.println(httpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE));
		}
		
		if(msg instanceof HttpContent){
			HttpContent httpContent = (HttpContent)msg;
			ByteBuf buf = httpContent.content();
			//System.out.println(buf.readableBytes());
		    int cmd=buf.readInt();
		    
		    ByteBuf byteBuf=   ChatAction.getInitialize().execute(cmd, buf);
		    if(byteBuf!=null)
		    {
		    	HttpRequest request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,"",byteBuf);
				request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
				request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());
				request.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
		    	ctx.writeAndFlush(request);
		    }
		    
	
			
			//buf.release();
			//ctx.channel().close();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		cause.printStackTrace();
		ctx.channel().close();
	}



}
