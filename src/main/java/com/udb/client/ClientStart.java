package com.udb.client;

import com.udb.protocol.ChatAction;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

public class ClientStart {
  private static String host = "192.168.1.16";
  private static int port = 8084;
  ChannelFutureListener listener = null;
  Bootstrap boot = null;
  ChannelFuture cf = null;
  /*
   * private static String userId="afa005de990a0c3e1a92144fe05c1e00"; private static String
   * password="e10adc3949ba59abbe56e057f20f883e";
   */
  private static String userId = "00b994afef59da1655e438b3cb30777a";
  private static String password = "19e7d665ff5530bf4baf739cfc0d4447";

  /**
   * 客户端开始类.
   * @return dfas
   * @throws InterruptedException asfasd
   */
  public ClientStart init() throws InterruptedException {

    EventLoopGroup group = new NioEventLoopGroup();
    boot = new Bootstrap();
    boot.group(group);
    boot.channel(NioSocketChannel.class);
    boot.handler(new ChannelInitializer<SocketChannel>() {

      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        // TODO Auto-generated method stub
        // ch.pipeline().addLast(new IdleStateHandler(360,360,360));
        ch.pipeline().addLast(new HttpRequestEncoder());
        ch.pipeline().addLast(new HttpObjectAggregator(65536));
        ch.pipeline().addLast(new HttpResponseDecoder());
        ch.pipeline().addLast(new HttpClinetHandler());
      }
    });
    boot.option(ChannelOption.SO_KEEPALIVE, true);
    listener = new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture cf) throws Exception {
        // TODO Auto-generated method stub
        if (cf.isSuccess()) {
          System.out.println("连接服务器成功");
          ChatAction.getInitialize().setChannel(cf.channel());
          // 登录
          ChatAction.getInitialize().login(userId, password);

        } else {
          System.out.println("服务器准备重连");
          doConnect();
        }
      }

    };

    return this;
  }

  /*
   * 连接方法
   */
  public void doConnect() throws Exception {
    cf = boot.connect(host, port).sync();
    cf.addListener(listener);
    cf.channel().closeFuture().sync();

  }

  /**
   * 设置用户
   * @param userId 用户id.
   * @param password 密码.
   * @return 返回当前对象.
   */
  public ClientStart setUser(String userId, String password) {
    this.userId = userId;
    this.password = password;
    return this;
  }

  //main方法
  public static void main(String[] args) {
    try {
      new Thread(() -> {
        try {
          new ClientStart().init().doConnect();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }).start();
      try{
      boolean result=true;
      while(result)
      {
        System.out.println("123");
      }
      }catch(Exception e)
      {
        
      }
      Thread.sleep(2000);
      ChatAction chat = ChatAction.getInitialize();
      ByteBuf buf = chat.send(userId, "afa005de990a0c3e1a92144fe05c1e00");
      HttpRequest request =
          new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "", buf);
      request.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
      request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
      request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
      chat.channel.writeAndFlush(request);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



}
