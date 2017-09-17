package com.example.androidthings.myproject;

import android.util.Log;

import java.io.IOException;

import com.google.android.things.contrib.driver.mma8451q.Mma8451Q;

import edu.berkeley.idd.utils.SerialMidi;
import com.google.android.things.pio.Gpio;



/**
 * HW3 Template
 * Created by bjoern on 9/12/17.
 * Wiring:s
 * USB-Serial Cable:
 *   GND to GND on IDD Hat
 *   Orange (Tx) to UART6 RXD on IDD Hat
 *   Yellow (Rx) to UART6 TXD on IDD Hat
 * Accelerometer:
 *   Vin to 3V3 on IDD Hat
 *   GND to GND on IDD Hat
 *   SCL to SCL on IDD Hat
 *   SDA to SDA on IDD Hat
 * Analog sensors:
 *   Middle of voltage divider to Analog A0..A3 on IDD Hat
 */


public class Hw3TemplateApp extends SimplePicoPro {

    float volume, frequency, timbre,a3; //store analog readings from ADS1015 ADC here [units: V]

    SerialMidi serialMidi;
    int channel = 0;
    int velocity = 127; //0..127
    int timbre_value = 0;
    final int timbre_controller = 0x47;
    boolean enabled = false;
    double background;

    public void setup() {

        // Initialize the serial port for communicating to a PC
        uartInit(UART6, 115200);
        serialMidi = new SerialMidi(UART6);

        // Initialize the Analog-to-Digital converter on the HAT
        analogInit(); //need to call this first before calling analogRead()

        pinMode(GPIO_128, Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_128, Gpio.EDGE_RISING);

        background = analogRead(A0);

    }

    public void loop() {
        // read all analog channels and print to UART
        volume = analogRead(A0);
        frequency = analogRead(A1);
        timbre = analogRead(A2);
        a3 = analogRead(A3);

        serialMidi.midi_controller_change(channel,timbre_controller, map(timbre, 0, 3.3, 0, 127));

        serialMidi.midi_note_on(channel, map(frequency*frequency, background*background, 3.3*3.3, 0, 84), map(volume, 0, 3.3, 0, 127));
        delay(20);
        serialMidi.midi_note_off(channel, map(frequency*frequency, background*background, 3.3*3.3, 0, 84), map(volume, 0, 3.3, 0, 127));
        delay(20);
    }

    public int map(double x, double in_min, double in_max, double out_min, double out_max) {
        Double d = ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
        return d.intValue();
    }

    @Override
    void digitalEdgeEvent(Gpio pin, boolean value) {
        enabled = !enabled;

        delay(100);
    }
}
