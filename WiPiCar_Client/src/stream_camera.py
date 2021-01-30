#!/usr/bin/env python3

from picamera import Camera
from signal import pause
import socket


class Stream_Camera:
    def __init__(self, resolution, framerate, rotation):
        self.camera = Camera()
        self.camera.resolution = resolution
        self.camera.framerate = framerate
        self.camera.rotation = rotation
        self.name = "[" + str(__file__) + "]"

    def stream(self, ipv4, port, timeout):
        """
        Connect socket, open file stream, and start streaming camera footage over socket
        """
        self.camera.start_preview()
        print(self.name, "Connecting to server...")
        with socket.create_connection((ipv4, port), timeout) as soc:  # Create socket connection
            print(self.name, "Connected!")
            with soc.makefile("wb") as file:  # Create file stream
                try:
                    print(self.name, "Camera streaming")
                    self.camera.start_recording(file, format="h264", intra_period=0, quality=0, bitrate=25000000)
                    pause()  # Wait/record forever
                finally:
                    self.camera.stop_recording()
                    print(self.name, "Stopping camera")
        print(self.name, "Connection ended")
