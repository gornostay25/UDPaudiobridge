#!/usr/bin/python
from socket import socket,AF_INET,SOCK_DGRAM
from time import sleep


sock = socket(AF_INET,SOCK_DGRAM)
with open(0,'rb') as f:
    b = f.read(14144)
    while b:
        sock.sendto(b, ('192.168.0.102', 8079))
        sleep(0.07)
        b = f.read(14144)
