package com.udb.protocol;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

public class ChatAction {
	
	    private volatile static ChatAction chat;  
	    private ChatAction (){}  
	    public static ChatAction getInitialize() {  
	    if (chat == null) {  
	        synchronized (ChatAction.class) {  
	        if (chat == null) {  
	        	chat = new ChatAction();  
	        }  
	        }  
	    }  
	    return chat;  
	    }  
	
	private final static int  VERSION=1;
	private final static int  RESP_LOGIN=90001;
	private final static int  REQ_SYNC=5;
	private final static int  RESP_SYNC=90004;
	private final static int  REQ_SEND=3;//
	private final static int  RESQ_SEND=90003;//
	private final static int  RESP_MSG=90005;
	
	public void setChannel(Channel channel)
	{
		this.channel=channel;
	}
	/*public void setSession(String sessionId)
	{
		this.sessionId=sessionId;
	}*/
	
	
	
	public Channel channel;
	public static String sessionId;

	
	public ByteBuf execute(int cmd,ByteBuf buf)
	{
		ByteBuf byteBuf=null;
		
		switch(cmd)
		{
		 case RESP_LOGIN: loginResponse(buf);break;
		 case RESP_SYNC:  byteBuf=sync(buf); break;
		 case RESP_MSG:  byteBuf=msg(buf); break;
		 case RESQ_SEND:	sendResponse(buf);break;
		
		}
		return byteBuf;
	}
	
	
	public void login(String userCode,String password)
	{
		
		int  cmd=1;
		int version=0;
		String sessionId="";
		byte[] scsessionIdbyte=sessionId.getBytes(Charset.forName("UTF-8"));
		//body data
		String deviceId="";
		int deviceType=1;
		int pushCount=0;
		int loginType =0;
		int clientType=1;
		byte[] userCodebyte=userCode.getBytes(Charset.forName("UTF-8"));
		byte[] passwordbyte=password.getBytes(Charset.forName("UTF-8"));
		byte[] deviceIdbyte=deviceId.getBytes(Charset.forName("UTF-8"));
		ByteBuf buf =  Unpooled.buffer(1024);
		buf.writeInt(cmd);
		buf.writeInt(version);
		buf.writeInt(scsessionIdbyte.length);
		buf.writeBytes(scsessionIdbyte);
		buf.writeInt(userCodebyte.length);
		buf.writeBytes(userCodebyte);
		buf.writeInt(passwordbyte.length);
		buf.writeBytes(passwordbyte);
		
		
		buf.writeInt(clientType);
		
		buf.writeInt(deviceIdbyte.length);
		buf.writeBytes(deviceIdbyte);
		
		//buf.writeInt(deviceType);
		buf.writeInt(loginType);
		buf.writeInt(pushCount);
		
	     //URI uri = new URI("");
		HttpRequest request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,"",buf);
		request.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
		request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,buf.readableBytes());
		request.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
		channel.writeAndFlush(request);
	}
	
	
	/***
	 * 登录处理
	 * @param buf
	 */
	private void loginResponse(ByteBuf buf)
	{
		
		int version=buf.readInt();
		int sessionIdLength=buf.readInt();
		if(sessionIdLength>0)
		{
			
			byte[] sessionIdByte=new byte[sessionIdLength];
			ByteBuf sessionidBuf=buf.readBytes(sessionIdByte);
			sessionId=new String(sessionIdByte);
		}
		int result=buf.readInt();
		int userType=buf.readInt();
		System.out.printf("result:%d    ,sessiondId:%s",result,sessionId);
	}
	
	
	private ByteBuf  sync(ByteBuf buf)
	{
	    int version=	buf.readInt();
	    int type=	buf.readInt();
	    System.out.printf("sync massagess's type %d%n",type);
	    buf.release();
	    ByteBuf byteBuf =  Unpooled.buffer(1024);
	    byteBuf.writeInt(REQ_SYNC);
	    byteBuf.writeInt(VERSION);
		byte[] sessionIdbyte=sessionId.getBytes(Charset.forName("UTF-8"));
	    byteBuf.writeInt(sessionIdbyte.length);
	    byteBuf.writeBytes(sessionIdbyte);
	    byteBuf.writeLong(0);
	    byteBuf.writeInt(1);
	   
	    return byteBuf;
	}
	
	//同步消息
	private ByteBuf msg(ByteBuf buf)
	{
		 //ByteBuf byteBuf =  Unpooled.buffer(1024);
	    int version=	buf.readInt();
	    byte isMore=	buf.readByte();
	    int count=	buf.readInt();
	    int type=	buf.readInt();
	    System.out.println("msg type:"+type);
	    for(int i=0;i<count;i++)
	    {
	    	
	    	String sendDate=this.readString(buf);
	    	String UserId=this.readString(buf);
	    	String UserName=this.readString(buf);
	    	String UserImage=this.readString(buf);
	    	String ReceiveId=this.readString(buf);
	    	String ReceiveName=this.readString(buf);
	    	String ReceiveImage=this.readString(buf);
	    	long MsgId=buf.readLong();
	    	int msgType=buf.readInt();
	    	int GroupMsgFlag=buf.readInt();
	    	
	    	if(msgType==1||msgType==5)
	    	{
	    		String WordMsg=this.readString(buf);
	    		System.out.println(WordMsg);
	    		
	    	}else if(msgType==2)
	    	{
	    		String imageUrl=this.readString(buf);
	    		int width=buf.readInt();
	    		int height=buf.readInt();
	    		System.out.println(imageUrl);
	    	}
	    	
	    	
	    	
	    }
	    
	    
		int senderNumL=buf.readInt();
		if(senderNumL>0)
		{
			
			byte[] senderNumByte=new byte[senderNumL];
			ByteBuf sessionidBuf=buf.readBytes(senderNumByte);
		}
		 String	sendName="";
		int senderNameL=buf.readInt();
		if(senderNameL>0)
		{
			
			byte[] sendNameByte=new byte[senderNameL];
			ByteBuf sendNameBuf=buf.readBytes(sendNameByte);
           	sendName=new String(sendNameByte);
		}
		 
		buf.readInt();
	    buf.readInt();
		
	    int titleL=buf.readInt();
		if(titleL>0)
		{
			
			byte[] titleByte=new byte[titleL];
			ByteBuf titleBuf=buf.readBytes(titleByte);
		}
		int contentL=buf.readInt();
		if(contentL>0)
		{
			
			byte[] contentByte=new byte[contentL];
			ByteBuf cntentBuf=buf.readBytes(contentByte);
            String	content=new String(contentByte);
            System.out.println(sendName+" say:"+content);
		}
	    
		buf.release();
		 
		return null;
	}
	
	
	public ByteBuf send(String sendUserId,String recvUserId)
	{
		
	    ByteBuf byteBuf =  Unpooled.buffer(1024);
	    byteBuf.writeInt(REQ_SEND);
	    byteBuf.writeInt(VERSION);
	    System.out.println("sessionId:"+sessionId);  
		byte[] sessionIdbyte=sessionId.getBytes(Charset.forName("UTF-8"));
	    byteBuf.writeInt(sessionIdbyte.length);
	    byteBuf.writeBytes(sessionIdbyte);
	    byteBuf.writeLong(0);
	    byteBuf.writeInt(1);
		
	    byte[] senderbyte=sendUserId.getBytes(Charset.forName("UTF-8"));
	    byte[] receiverbyte=recvUserId.getBytes(Charset.forName("UTF-8"));
	    byteBuf.writeInt(senderbyte.length);
	    byteBuf.writeBytes(senderbyte);
	    byteBuf.writeInt(receiverbyte.length);
	    byteBuf.writeBytes(receiverbyte);
	    byteBuf.writeInt(1);
	    String msg="hello boy,what are you doing?";
	    byte[] msgbyte=msg.getBytes(Charset.forName("UTF-8"));
	    byteBuf.writeInt(msgbyte.length);
	    byteBuf.writeBytes(msgbyte);
	    System.out.printf("i say:%s%n",msg);
		return byteBuf;
	}
	
	public ByteBuf sendResponse(ByteBuf buf)
	{
	    int version=	buf.readInt();
	    	buf.readLong();buf.readLong();
	    int result=buf.readInt(); 	
		System.out.printf("send msg result:%d",result);
		
		return null;
	}

	
	private String readString(ByteBuf buf)
	{
		int length=buf.readInt();
		if(length>0)
		{
			byte[] bytes=new byte[length];
			ByteBuf cntentBuf=buf.readBytes(bytes);
            String	content=new String(bytes);
            return content;
		}
		return "";
	}
	
	
	
}
