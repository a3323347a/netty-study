# netty-study

学习netty的基本知识点

一、asyn包
    只是一个简单的异步的请求应答例子，一个线程发起请求，另外一个线程处理请求
二 、demo01包
    netty入门学习例子，不过是短连接，每次请求都要新建连接，性能比较低
三、demo02包
    netty入门学习例子，长连接例子
    问题一：改成长连接后，ClientHandler不能每次都在main方法中构建，promise对象无法通过主线程传递给ClientHandler，此时主线程如何获取NioEventLoop线程的数据？
    解决：通过多线程数据交互来解决，对Netty客户端创建的连接进行静态化处理，然后给服务端发送请求后运用RequestFuture.get()同步获取响应结果，最后用RequestFuture.received()替代
         ClientHandler的Promise异步通知
    问题二：主线程每次请求获取的响应结果对应的是哪次请求？
    解决：每次请求带上自增唯一的id，客户端需要把每次请求先缓存起来，同时服务端在接收到请求后，会把请求id放入响应结果中一起返回客户端
    

