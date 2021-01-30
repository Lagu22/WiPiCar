#!/usr/bin/python

from adafruit_motorkit import MotorKit


class Motors:
    def __init__(self):
        self.kit = MotorKit()
        self.tiers = (0, 0.50, 0.75, 1.0)  # 0: stop | 1: half speed | 2: turn speed | 3: full speed
        self.pwr = self.tiers[0]  # Can range from 0.50 to 1.0

    def forward(self):
        # Incremental drive increase
        if self.pwr != self.tiers[1]:
            self.pwr = self.tiers[1]
        elif self.pwr == self.tiers[1]:
            self.pwr = self.tiers[3]
        self.kit.motor1.throttle = self.pwr
        self.kit.motor3.throttle = self.pwr
        self.kit.motor2.throttle = -(self.pwr)
        self.kit.motor4.throttle = -(self.pwr)

    def backward(self):
        # Stop otherwise back up
        self.neutral()
        if self.pwr != self.tiers[0]:
            self.pwr = self.tiers[0]
        elif self.pwr == self.tiers[0]:
            self.pwr = self.tiers[1]
        else:
            self.pwr = self.tiers[0]
        self.kit.motor1.throttle = -(self.pwr)
        self.kit.motor3.throttle = -(self.pwr)
        self.kit.motor2.throttle = self.pwr
        self.kit.motor4.throttle = self.pwr

    def leftward(self):
        self.neutral()
        self.pwr = self.tiers[2]
        self.kit.motor1.throttle = -(self.pwr)
        self.kit.motor3.throttle = self.pwr
        self.kit.motor2.throttle = self.pwr
        self.kit.motor4.throttle = -(self.pwr)

    def rightward(self):
        self.neutral()
        self.pwr = self.tiers[2]
        self.kit.motor1.throttle = self.pwr
        self.kit.motor3.throttle = -(self.pwr)
        self.kit.motor2.throttle = -(self.pwr)
        self.kit.motor4.throttle = self.pwr

    def neutral(self):
        # Coast letting motors free spin
        self.kit.motor1.throttle = None
        self.kit.motor3.throttle = None
        self.kit.motor2.throttle = None
        self.kit.motor4.throttle = None
