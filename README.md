# Anonymous WebSocket Chat

This example demonstrates a minimal anonymous chat application. The backend is a Java WebSocket server running on top of TCP. The frontend is a simple HTML page that uses WebSocket for real-time communication and supports sending text, images, audio, video and links.

## Building and Running

1. Ensure Java and Maven are installed on your system.
2. Run `mvn package` to build a runnable jar with dependencies.
3. Start the server:

```bash
java -jar target/server-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The HTTP server will listen on port **8080** and the WebSocket server on port **8081**. Open `http://localhost:8080` in your browser to start chatting.
