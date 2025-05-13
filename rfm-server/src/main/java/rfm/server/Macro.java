package rfm.server;

import org.json.JSONObject;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.Random;

public class Macro {
    private static JSONObject config;
    private static final int WARP_KEY = KeyEvent.VK_G;
    private static final int BACK_KEYBIND = KeyEvent.VK_S;
    private static final int FORWARD_KEYBIND = KeyEvent.VK_W;
    private static final int LEFT_KEYBIND = KeyEvent.VK_A;
    private static final int RIGHT_KEYBIND = KeyEvent.VK_D;

    private static long WART_DURATION;
    private static long MELON_DURATION;
    private static long SUGAR_DURATION;
    private static long CACTI_DURATION;

    private static int KEY_DELAY_MIN = 0;
    private static int KEY_DELAY_MAX = 180;
    private static int ROW_END_DELAY_MIN = 0;
    private static int ROW_END_DELAY_MAX = 300;

    static {
        loadDurations();
    }

    private static void loadDurations() {
        try {
            File file = new File("macro_config.json");
            String content = Files.readString(file.toPath());
            config = new JSONObject(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        WART_DURATION = config.getLong("WART_DURATION");
        MELON_DURATION = config.getLong("MELON_DURATION");
        SUGAR_DURATION = config.getLong("SUGAR_DURATION");
        CACTI_DURATION = config.getLong("CACTI_DURATION");
    }

    private static void randomDelay(int min, int max) {
        Random rand = new Random();
        long delay = rand.nextInt(min, max);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final Robot robot;
    long loopStart;
    int currentLoop;

    public Macro() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private void macroStart() {
        currentLoop = 0;
    }

    private void warp() {
        robot.keyPress(WARP_KEY);
        robot.keyRelease(WARP_KEY);
    }

    private void mouseDown() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void mouseUp() {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void randomizedKeyAction(int key, boolean press) {
        if (press) robot.keyPress(key);
        else robot.keyRelease(key);
        randomDelay(KEY_DELAY_MIN, KEY_DELAY_MAX);
    }

    private volatile boolean paused = false;
    private volatile boolean running = false;
    private Thread macroThread;

    public void startWartMacro() {
        if (running) return;

        macroThread = new Thread(() -> {
            running = true;
            macroStart();

            warp();
            randomDelay(0, 150);
            mouseDown();

            int currentLoop = 0;
            while (running) {
                loopStart = System.currentTimeMillis();
                int key = currentLoop % 2 == 0 ? LEFT_KEYBIND : RIGHT_KEYBIND;

                walk(key, WART_DURATION);
                if (!running) break;

                randomDelay(ROW_END_DELAY_MIN, ROW_END_DELAY_MAX);

                currentLoop++;
                
                if (currentLoop == 5) {
                    currentLoop = 0;
                    mouseUp();
                    warp();
                    mouseDown();
                }
            }

            mouseUp();
            running = false;
        });

        macroThread.start();
    }

    public void pause() {
        System.out.println("Pausing");
        paused = true;
    }

    public void resume() {
        System.out.println("Resuming");
        paused = false;
        synchronized (this) {
            notifyAll();
        }
    }

    public void stop() {
        running = false;
        paused = false;
        if (macroThread != null) {
            macroThread.interrupt();
        }
        synchronized (this) {
            notifyAll(); // In case it's paused and waiting
        }
    }

    private void walk(int key, long duration) {
        randomizedKeyAction(key, true);

        long start = System.currentTimeMillis();
        long elapsed = 0;

        try {
            while (elapsed < duration * 1000) {
                if (!running) break;

                if (paused) {
                    randomizedKeyAction(key, false); // release key when paused
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    if (!running) return;

                    randomizedKeyAction(key, true); // re-press after resume
                    start = System.currentTimeMillis() - elapsed;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }

                elapsed = System.currentTimeMillis() - start;
            }
        } finally {
            randomizedKeyAction(key, false); // always release key
        }
    }


    public void startMelonMacro() {
    }

    public void startCactiMacro() {
    }

    public void startSugarMacro() {
    }
}
