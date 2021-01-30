package com.lagu.WiPiCar;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class CarControl implements Runnable {

    public AtomicInteger controlState;
    int port;
    int backlog;
    TextArea windowTerminal;

    public CarControl(int port, int backlog, TextArea windowTerminal) {
        // https://www.geeksforgeeks.org/atomic-variables-in-java-with-examples/
        controlState = new AtomicInteger(0);
        this.port = port;
        this.backlog = backlog;
        this.windowTerminal = windowTerminal;
    }

    public void run() {
        Platform.runLater(() -> this.windowTerminal.appendText("\nStarting server..."));
        try (ServerSocket server = new ServerSocket(this.port, this.backlog);
             Socket clientSocket = server.accept();
             DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream())) {
            // while this thread is running check if a control update occurred
            // and if so write the update data to the socket stream
            Platform.runLater(() -> this.windowTerminal.appendText("\nConnection from " + clientSocket.getInetAddress()
                    + " on port " + clientSocket.getPort()));
            int state;
            while (!Thread.interrupted()) {
                state = controlState.get();
                if (state != 0) {
                    dataOut.write(state);
                    dataOut.flush();
                    System.out.println("data sent: " + state);
                    controlState.set(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> this.windowTerminal.appendText("\nConnection Error"));
        }
    }
}
