package rfm.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerConnection {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;

    public ServerConnection() {
        while (true) {
            try {
                waitForClient(); // blocks until connected
                startCommunication(); // handles screen + commands
            } catch (Exception e) {
                System.out.println("Client disconnected. Waiting for next...");
                closeCurrentConnection();
            }
        }
    }

    private void waitForClient() throws IOException {
        System.out.println("Waiting for client...");
        ServerSocket serverSocket = new ServerSocket(5000);
        socket = serverSocket.accept();
        serverSocket.close(); // only allow one client at a time
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected.");
    }

    private void startCommunication() throws IOException {
        BlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(3);

        Thread captureThread = new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    long start = System.currentTimeMillis();

                    byte[] image = ScreenshotGen.getScreenshot();
                    if (!queue.offer(image)) {
                        queue.poll();
                        queue.offer(image);
                    }

                    long duration = System.currentTimeMillis() - start;
                    long targetFrameTime = 1000 / 30;
                    long sleepTime = Math.max(0, targetFrameTime - duration);
                    Thread.sleep(sleepTime);
                } catch (Exception ignored) {}
            }
        });

        Thread senderThread = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    byte[] image = queue.take();
                    out.writeInt(image.length);
                    out.write(image);
                    out.flush();
                }
            } catch (Exception ignored) {}
        });

        Thread commandThread = new Thread(() -> {
            Macro macro = new Macro();
            try {
                while (!socket.isClosed()) {
                    String message = in.readUTF();
                    Command command = CommandTranslator.translate(message);
                    System.out.println("Received command: " + command + command.getMode());

                    switch (command) {
                        case START -> {
                            switch (command.getMode()) {
                                case Mode.WART -> macro.startWartMacro();
                                case Mode.MELON -> macro.startMelonMacro();
                                case Mode.CACTI -> macro.startCactiMacro();
                                case Mode.SUGAR -> macro.startSugarMacro();
                            }
                        }
                        case PAUSE -> macro.pause();
                        case UNPAUSE -> macro.resume();
                        case STOP -> macro.stop();
                    }

                    if (command == Command.KILL) {
                        socket.close();
                        break;
                    }
                }
            } catch (IOException ignored) {}
        });

        captureThread.start();
        senderThread.start();
        commandThread.start();

        try {
            commandThread.join();
        } catch (InterruptedException ignored) {}
    }


    private void closeCurrentConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
