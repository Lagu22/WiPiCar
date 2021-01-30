package com.lagu.WiPiCar;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FFmpegFXImageDecoder implements Runnable {
    /*
    https://codereview.stackexchange.com/questions/163042/streaming-h264-video-from-picamera-to-a-javafx-imageview
     */
    
    private final ImageView view;
    private final int port;
    private final int serverBacklog;
    private final String format;
    private final double framerate;
    private final int bitrate;
    private final String preset;
    private final int numBuffers;
    private final TextArea windowTerminal;
    
    public FFmpegFXImageDecoder(TextArea windowTerminal, ImageView imageView, int port, int serverBacklog,
                                String format, double framerate, int bitrate,
                                String preset, int numBuffers) {
        this.windowTerminal = windowTerminal;
        this.view = imageView;
        this.port = port;
        this.serverBacklog = serverBacklog;
        this.format = format;
        this.framerate = framerate;
        this.bitrate = bitrate;
        this.preset = preset;
        this.numBuffers = numBuffers;
    }

    @Override
    public void run() {
        Platform.runLater(() -> this.windowTerminal.appendText("\n[camera] Starting camera server"));
        try (final ServerSocket server = new ServerSocket(this.port, this.serverBacklog);
             final Socket clientSocket = server.accept();
             final FrameGrabber grabber = new FFmpegFrameGrabber(clientSocket.getInputStream(), 0)) {
            Platform.runLater(() -> this.windowTerminal.appendText("\n[camera] Connection from " + clientSocket.getInetAddress() +
                    " on port " + clientSocket.getPort()));
            System.out.println("Connection from " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort());
            final Java2DFrameConverter converter = new Java2DFrameConverter();
            grabber.setFrameRate(this.framerate);
            grabber.setFormat(this.format);
            grabber.setVideoBitrate(this.bitrate);
            grabber.setVideoOption("preset", this.preset);
            grabber.setNumBuffers(this.numBuffers);
            grabber.start();
            while (!Thread.interrupted()) {
                final Frame frame = grabber.grab();
                if (frame == null) {
                    Platform.runLater(() -> {
                        this.view.setImage(null);
                        this.windowTerminal.appendText("\n[camera] ending camera connection");
                    });
                    grabber.stop();
                    Thread.currentThread().interrupt();
                }
                final BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage != null) {
                        Platform.runLater(() -> this.view.setImage(SwingFXUtils.toFXImage(bufferedImage, null)));
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }


}