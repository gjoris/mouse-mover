package eu.droidit.mousemover;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;


public class MouseMover extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        setupSystemTray();

        Thread th = new Thread(setupTask());
        th.setDaemon(true);
        th.start();
    }

    private Task<Integer> setupTask() {
        int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
        int screenHeight = (int) Screen.getPrimary().getBounds().getHeight();
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                for (int iterations = 0; iterations < 60 * 60 * 24 * 365 * 10; iterations++) {
                    if (isCancelled()) {
                        break;
                    }
                    moveCursor(new Double(screenWidth * Math.random()).intValue(), new Double(screenHeight * Math.random()).intValue());
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return 0;
            }
        };
    }

    private void setupSystemTray() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = null;
            try {
                image = ImageIO.read(getClass().getResource("/eu/droidit/mousemover/images/mouse1.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            PopupMenu popup = new PopupMenu();
            MenuItem item = new MenuItem("Exit");

            popup.add(item);

            TrayIcon trayIcon = new TrayIcon(image, "MouseMover", popup);

            item.addActionListener(event -> Platform.runLater(() -> System.exit(0)));

            try {
                tray.add(trayIcon);
            } catch (Exception e) {
                System.err.println("Can't add to tray");
            }
        } else {
            System.err.println("Tray unavailable");
        }
    }

    private void moveCursor(int screenX, int screenY) {
        Platform.runLater(() -> {
            try {
                Robot robot = new Robot();
                robot.mouseMove(screenX, screenY);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        });
    }
}
