package samples;

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.HttpServer;
import com.dalcomlab.sattang.server.ServerListener;
import com.dalcomlab.sattang.server.ServerOptions;

import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class HttpServerMultipleInstance {

    public static void main(String[] args) throws Exception {
        ExecutorService service = createExecutorService(2);
        CountDownLatch waitingLatch = new CountDownLatch(2);
        HttpServer httpServer8080 = new HttpServer("0.0.0.0", 8080);
        HttpServer httpServer8090 = new HttpServer("0.0.0.0", 8090);

        // start first instance
        service.submit(() -> {
            try {
                httpServer8080.handle("/", (request, response) -> {
                    StringBuilder html = new StringBuilder();
                    html.append("<!DOCTYPE html>\n");
                    html.append("<html>\n");
                    html.append("<head>\n");
                    html.append("    <title>Hello World! - 8080</title>\n");
                    html.append("</head>\n");
                    html.append("<body>\n");
                    html.append("<h1>Hello World! - 8080</h1>\n");
                    html.append("</body>\n");
                    html.append("</html>\n");
                    sendHtml(request, response, html.toString());
                }).handle("/stop", (request, response) -> {
                    httpServer8080.stop();
                }).listen(new ServerListener() {
                    @Override
                    public void onStop() {
                        waitingLatch.countDown();
                    }
                }).start(new ServerOptions());
            } catch (Exception e) {
                e.printStackTrace();
                waitingLatch.countDown();
            }
        });

        // start second instance
        service.submit(() -> {
            try {
                httpServer8090.handle("/", (request, response) -> {
                    StringBuilder html = new StringBuilder();
                    html.append("<!DOCTYPE html>\n");
                    html.append("<html>\n");
                    html.append("<head>\n");
                    html.append("    <title>Hello World! - 8090</title>\n");
                    html.append("</head>\n");
                    html.append("<body>\n");
                    html.append("<h1>Hello World! - 8090</h1>\n");
                    html.append("</body>\n");
                    html.append("</html>\n");
                    sendHtml(request, response, html.toString());
                }).handle("/stop", (request, response) -> {
                    httpServer8090.stop();
                }).listen(new ServerListener() {
                    @Override
                    public void onStop() {
                        waitingLatch.countDown();
                    }
                }).start(new ServerOptions());
            } catch (Exception e) {
                e.printStackTrace();
                waitingLatch.countDown();
            }
        });

        waitingLatch.await();
        service.shutdown();
    }


    public static void sendHtml(HttpRequest request, HttpResponse response, String html) {
        try {
            response.addHeader(HttpHeader.CONTENT_TYPE, "text/html");
            response.addHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(html.getBytes().length));
            OutputStream output = response.getOutputStream();
            if (output != null) {
                output.write(html.getBytes());
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ExecutorService createExecutorService(int count) {
        final ThreadFactory factory = new ThreadFactory() {
            private int counter;

            @Override
            public Thread newThread(Runnable r) {
                final String name = "server thread <" + counter++ + ">";
                Thread t = new Thread(r, name);
                t.setDaemon(true);
                return t;
            }
        };

        return Executors.newFixedThreadPool(count, factory);
    }
}
