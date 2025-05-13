package rfm.server;

import org.json.JSONObject;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.Random;

public class Macro_ {
    private static JSONObject config;
    private static Robot robot;
    private static boolean running = false;
    private static boolean paused = false;  // Flag to track pause state
    private static int currentLoop = 0;  // Keeps track of which iteration we're on

    // Keybinds
    private static final int WARP_KEY = KeyEvent.VK_G;
    private static final int BACK_KEYBIND = KeyEvent.VK_S;
    private static final int FORWARD_KEYBIND = KeyEvent.VK_W;
    private static final int LEFT_KEYBIND = KeyEvent.VK_A;
    private static final int RIGHT_KEYBIND = KeyEvent.VK_D;

    private static final double WART_DURATION;
    private static final double MELON_DURATION;
    private static final double SUGAR_DURATION;
    private static final double CACTI_DURATION;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        try {
            config = loadConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        WART_DURATION = config.getDouble("WART_DURATION");
        MELON_DURATION = config.getDouble("MELON_DURATION");
        SUGAR_DURATION = config.getDouble("SUGAR_DURATION");
        CACTI_DURATION = config.getDouble("CACTI_DURATION");
    }

    private static JSONObject loadConfig() throws Exception {
        File file = new File("macro_config.json");
        String content = Files.readString(file.toPath());
        return new JSONObject(content);
    }

    public static void startWartMacro() {
        double duration = WART_DURATION;
        long baseTime = (long) (duration * 1000);

        warp();
        mouseDown();
        for (int i = currentLoop; i < 5; i++) {  // Continue from currentLoop if paused
            if (paused) {
                break;  // Exit the loop if paused
            }

            System.out.printf("Farming wart loop %d%n", i + 1);

            try {
                walk(KeyEvent.VK_A, baseTime); // Simulating walk using A
                walk(KeyEvent.VK_D, baseTime); // Simulating walk using D
                walk(KeyEvent.VK_A, baseTime); // Simulating walk using A
                walk(KeyEvent.VK_D, baseTime); // Simulating walk using D
            } catch (Exception e) {
                throw new RuntimeException("Error during macro execution", e);
            }

            randomDelay();
        }

        if (!paused) {
            stopMacro();  // Automatically stop after this macro if not paused
        }
    }

    public static void startMelonMacro() {
        double duration = MELON_DURATION;
        long baseTime = (long) (duration * 1000);

        warp();
        mouseDown();
        for (int i = currentLoop; i < 5; i++) {  // Continue from currentLoop if paused
            if (paused) {
                break;  // Exit the loop if paused
            }

            System.out.printf("Farming melon loop %d%n", i + 1);

            try {
                walk(KeyEvent.VK_D, baseTime); // Simulating walk using D
                walk(KeyEvent.VK_A, baseTime); // Simulating walk using A
                walk(KeyEvent.VK_D, baseTime); // Simulating walk using D
                walk(KeyEvent.VK_A, baseTime); // Simulating walk using A
                randomDelay();
            } catch (Exception e) {
                throw new RuntimeException("Error during macro execution", e);
            }
        }

        if (!paused) {
            stopMacro();  // Automatically stop after this macro if not paused
        }
    }

    public static void stopMacro() {
        running = false;
        System.out.println("Macro stopped.");
    }

    public static void pause() {
        paused = true;  // Set pause state to true
        System.out.println("Macro paused.");
        // Ensure all keys are lifted when paused
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_D);
    }

    public static void unpause() {
        paused = false;  // Set pause state to false
        System.out.println("Macro unpaused.");
        // Continue from where we left off
        startWartMacro();  // You can trigger the specific macro again, resuming from the current loop index
    }

    private static void warp() {
        robot.keyPress(WARP_KEY);
        robot.keyRelease(WARP_KEY);
    }

    private static void walk(int key, long duration) throws InterruptedException {
        randomStartOrEnd(key, true);
        Thread.sleep(duration);
        randomStartOrEnd(key, false);
    }

    private static void randomStartOrEnd(int key, boolean start) {
        if (start) {
            robot.keyPress(key);
        } else {
            robot.keyRelease(key);
        }
        randomDelay();
    }

    private static void randomDelay() {
        Random rand = new Random();
        long delay = rand.nextInt(500) + 100; // Random delay between 100ms to 600ms
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mouseDown() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }

    private static void mouseUp() {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void startSugarMacro() {
        double duration = SUGAR_DURATION;
        long baseTime = (long) (duration * 1000);

        warp();
        mouseDown();
        int i = 1;

        while (!paused) {
            System.out.printf("Farming sugar loop %d%n", i);

            // Sugar farm loop
            for (int x = 0; x < 14; x++) {
                try {
                    randomDelay();
                    walk(BACK_KEYBIND, baseTime);
                    randomDelay();
                    walk(RIGHT_KEYBIND, baseTime);
                } catch (Exception e) {
                    throw new RuntimeException("Error during macro execution", e);
                }

            }

            try {
                randomDelay();
                walk(BACK_KEYBIND, baseTime);
            } catch (Exception e) {
                throw new RuntimeException("Error during macro execution", e);
            }

            i++;
            warp();  // Repeat the warp action after each loop
        }

        if (!paused) {
            stopMacro();  // Automatically stop after this macro if not paused
        }
    }

    public static void startCactiMacro() {
        double duration = CACTI_DURATION;
        long baseTime = (long) (duration * 1000);

        warp();
        mouseDown();
        int i = 1;

        while (!paused) {
            System.out.printf("Farming cactus loop %d%n", i);

            // Cactus farm loop
            for (int x = 0; x < 13; x++) {
                randomDelay();
                robot.keyPress(LEFT_KEYBIND);
                randomDelay();

                randomDelay();
                robot.keyPress(FORWARD_KEYBIND);
                randomDelay();

                randomDelay();
                robot.keyPress(RIGHT_KEYBIND);
                randomDelay();

                if (x != 12) {
                    randomDelay();
                    robot.keyPress(FORWARD_KEYBIND);
                    randomDelay();
                }

                randomDelay();
                robot.keyRelease(LEFT_KEYBIND);
                robot.keyRelease(FORWARD_KEYBIND);
                robot.keyRelease(RIGHT_KEYBIND);
            }

            i++;
            warp();  // Repeat the warp action after each loop
        }

        if (!paused) {
            stopMacro();  // Automatically stop after this macro if not paused
        }
    }
}
