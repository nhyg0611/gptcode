import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ChatServer extends WebSocketServer {
    private final Set<WebSocket> connections = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.add(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        for (WebSocket socket : connections) {
            socket.send(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started on port " + getPort());
    }

    public static void main(String[] args) throws IOException {
        int httpPort = 8080;
        int wsPort = 8081;
        ChatServer server = new ChatServer(wsPort);
        server.start();
        startHttp(httpPort);
        System.out.println("HTTP server started on port " + httpPort);
    }

    private static void startHttp(int port) throws IOException {
        HttpServer http = HttpServer.create(new InetSocketAddress(port), 0);
        http.createContext("/", ChatServer::handleStatic);
        http.setExecutor(null);
        http.start();
    }

    private static void handleStatic(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        if (uri.equals("/")) {
            uri = "/index.html";
        }
        Path file = Paths.get("public", uri.substring(1));
        if (!Files.exists(file)) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }
        String type = Files.probeContentType(file);
        byte[] bytes = Files.readAllBytes(file);
        if (type != null) {
            exchange.getResponseHeaders().add("Content-Type", type);
        }
        exchange.sendResponseHeaders(200, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
