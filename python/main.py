#!/usr/bin/python
# -*- coding: utf-8 -*-
# autor: Jefferson Rivera
# Abril de 2018
# email: riverajefer@gmail.com

import sys
from time import sleep
import signal
from gpiozero import LED, Button
from threading import Thread
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

LED = LED(17)
BUTTON = Button(27)

PAHT_CRED = '/home/pi/iot/cred.json'
URL_DB = 'https://rpi-demo-e24dd.firebaseio.com/'
REF_HOME = 'home'
REF_LUCES = 'luces'
REF_BOTONES = 'botones'
REF_LUZ_SALA = 'luz_sala'
REF_PULSADOR_A = 'pulsador_a'

class IOT():

    def __init__(self):
        cred = credentials.Certificate(PAHT_CRED)
        firebase_admin.initialize_app(cred, {
            'databaseURL': URL_DB
        })

        self.refHome = db.reference(REF_HOME)
        
        #self.estructuraInicialDB() # solo ejecutar la primera vez

        self.refLuces = self.refHome.child(REF_LUCES)
        self.refLuzSala = self.refLuces.child(REF_LUZ_SALA)

        self.refBotones = self.refHome.child(REF_BOTONES)
        self.refPulsadorA = self.refBotones.child(REF_PULSADOR_A)

    def estructuraInicialDB(self):
        self.refHome.set({
            'luces': {
                'luz_sala':True,
                'luz_cocina':True
            },
            'botones':{
                'pulsador_a':True,
                'pulsador_b':True
            }
        })
    
    def ledControlGPIO(self, estado):
        if estado:
            LED.on()
            print('LED ON')
        else:
            LED.off()
            print('LED OFF')

    def lucesStart(self):

        E, i = [], 0

        estado_anterior = self.refLuzSala.get()
        self.ledControlGPIO(estado_anterior)

        E.append(estado_anterior)

        while True:
          estado_actual = self.refLuzSala.get()
          E.append(estado_actual)

          if E[i] != E[-1]:
              self.ledControlGPIO(estado_actual)

          del E[0]
          i = i + i
          sleep(0.4)

    def pulsador_on(self):
        print('Pulsador On')
        self.refPulsadorA.set(True)

    def pulsador_off(self):
        print('Pulsador Off')
        self.refPulsadorA.set(False)

    def botonesStart(self):
        print('Start btn !')
        BUTTON.when_pressed = self.pulsador_on
        BUTTON.when_released = self.pulsador_off


print ('START !')
iot = IOT()

subproceso_led = Thread(target=iot.lucesStart)
subproceso_led.daemon = True
subproceso_led.start()

subproceso_btn = Thread(target=iot.botonesStart)
subproceso_btn.daemon = True
subproceso_btn.start()
signal.pause()
