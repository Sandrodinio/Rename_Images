package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class MyLogger {
    private final String logFile;

    public MyLogger(String logFile) {
        this.logFile = logFile;
    }

    public void log(String message, Throwable throwable) {
        try {
            Files.writeString(new File(logFile).toPath(), message + ": " + throwable.getMessage() + "\n", java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}