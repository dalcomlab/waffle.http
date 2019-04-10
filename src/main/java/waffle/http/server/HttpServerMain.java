package waffle.http.server;

import java.net.InetSocketAddress;

public class HttpServerMain {
    public static void main(String args[]) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8080);
        HttpServer httpServer = new HttpServerNio(inetSocketAddress);
        Thread thread = new Thread(httpServer);
        System.out.println("Http is starting.....");
        thread.start();
    }
}
