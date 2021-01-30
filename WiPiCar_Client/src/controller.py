#!/usr/bin/env python3

from multiprocessing import Process
import socket
import traceback

from motors import Motors
from stream_camera import Stream_Camera

HOST = "10.0.0.112"  # The server's hostname or IP address
PORT = 8001  # The port used by the server
BLANK = bytes("\x00", "utf-8")  # 0  used as a null command from server

motor_controller = Motors()
stream_cam = Stream_Camera((640, 480), 48, 180)

with socket.socket() as socket:  # Connect the the server's controlling socket and start reading data as commands
    socket.connect((HOST, PORT))
    camera_process = Process(target=stream_cam.stream, args=(HOST, (PORT - 1), 15,))
    try:
        while True:
            data = socket.recv(4)  # 4 bytes = int
            if data != BLANK:
                if data == bytes("\x01", "utf-8"):  # Forward
                    motor_controller.forward()
                elif data == bytes("\x02", "utf-8"):  # Right
                    motor_controller.rightward()
                elif data == bytes("\x03", "utf-8"):  # Backward
                    motor_controller.backward()
                elif data == bytes("\x04", "utf-8"):  # Left
                    motor_controller.leftward()
                elif data == bytes("\x05", "utf-8"):  # Camera On - starts new process for camera streaming to run in
                    if not camera_process.is_alive():
                        camera_process.start()
                    elif camera_process.is_alive():  # Camera Off -  close camera proc otherwise terminate
                        try:
                            camera_process.close()
                        except ValueError:
                            camera_process.terminate()
                elif data == bytes("\x06", "utf-8"):
                    motor_controller.neutral()
    except:
        traceback.print_exc()
    finally:
        if camera_process.is_alive():  # If it exists, close camera stream process
            try:
                camera_process.close()
            except ValueError:
                camera_process.terminate()
        motor_controller.neutral()  # Stop motor movement
