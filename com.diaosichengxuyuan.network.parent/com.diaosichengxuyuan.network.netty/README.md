1.拆包粘包：LineBasedFrameDecoder StringDecoder DelimiterBasedFrameDecoder LengthFieldBasedFrameDecoder等
2.长连接
3.心跳检测：IdleStateHandler
4.TCP参数：TCP_NODELAY(是否立刻发送收到的报文段) SO_SNDBUF(TCP发送滑动窗口大小) SO_KEEPALIVE(是否使用TCP心跳机制) 
5.http支持：HttpResponseEncoder HttpRequestDecoder
6.https支持：SslHandler
7.websocket支持：WebSocketServerHandler
8.epoll线程模型：EpollServerSocketChannel