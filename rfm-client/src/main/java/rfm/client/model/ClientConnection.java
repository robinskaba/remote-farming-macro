package rfm.client.model;

import java.io.*;
import java.net.Socket;

public class ClientConnection {
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    private volatile byte[] latestScreenshot;

    public ClientConnection(String ip, int port) {
        connect(ip, port);
        receiveScreen();
    }

    public void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Error connecting to server", e);
        }
    }

    public void sendCommand(String command) {
        try {
            out.writeUTF(command);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error sending command", e);
        }
    }

    private void receiveScreen() {
        Thread thread = new Thread(() -> {
//            int i = 0;
//            long lastTime = System.currentTimeMillis();
            try {
                while (!socket.isClosed()) {
                    int length = in.readInt();
                    if (length == 0) break;
                    byte[] imageBytes = new byte[length];
                    in.readFully(imageBytes);
                    latestScreenshot = imageBytes; // Replace the reference atomically
//                    i++;
//                    if (System.currentTimeMillis() - lastTime > 1000) {
//                        System.out.println("FPS: " + i);
//                        i = 0;
//                        lastTime = System.currentTimeMillis();
//                    }
                }
            } catch (IOException _) {}
        });
        thread.setDaemon(true);
        thread.start();
    }

    public ByteArrayInputStream getScreenShareStream() {
        return latestScreenshot == null ? null : new ByteArrayInputStream(latestScreenshot);
    }
}
