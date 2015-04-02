#include <SoftwareSerial.h>

const char DELIMETER = '\n';

// TX-O pin of bluetooth SMiRF, D0
const int PIN_BLUETOOTH_TX = 2;
// RX-I pin of bluetooth SMiRF, D1
const int PIN_BLUETOOTH_RX = 4;

// Joystick vertical analog input pin
const int PIN_JOYSTICK_VERT = 0;
// Joystick horizontal analog input pin
const int PIN_JOYSTICK_HORZ = 1;
// Joystick selection input pin
const int PIN_JOYSTICK_SEL = 12;

String bluetoothReceiveBuffer;

String previousJoystickState = "0,0,1";

int sendCounter = 0;

/*
 * Pin constants for the LED's. Use PWM pins for these.
 */
const int PIN_LED_LEFT = 3;
const int PIN_LED_TOP = 5;
const int PIN_LED_RIGHT = 6;
const int PIN_LED_BOTTOM = 9;

/* 
 * Constants for determining whether or not the joystick is pushed
 * in a given direction. It may be necessary to modify these for your joystick.
 */
const int JOYSTICK_LEFT_THRESHOLD = 900;
const int JOYSTICK_UP_THRESHOLD = 900;
const int JOYSTICK_RIGHT_THRESHOLD = 100;
const int JOYSTICK_DOWN_THRESHOLD = 100;

SoftwareSerial Bluetooth(PIN_BLUETOOTH_TX, PIN_BLUETOOTH_RX);

void setup() {
  bluetoothReceiveBuffer = "";
  
  // Begin serial monitor at 9600bps
  Serial.begin(9600);
  
  // Bluetooth SMiRF defaults to 115200bps
  Bluetooth.begin(115200);
    
  // Print three times individually
  // Last print enters command mode
  Bluetooth.print("$");
  Bluetooth.print("$");
  Bluetooth.print("$");
  
  // Short delay, wait for the SMiRF to send back CMD
  delay(100);
  // Temporarily change the baudrate to 9600, no parity
  Bluetooth.println("U,9600,N"); 
  // Start bluetooth serial at 9600
  Bluetooth.begin(9600);
  
  pinMode(PIN_JOYSTICK_SEL, INPUT_PULLUP);
  pinMode(PIN_LED_LEFT, OUTPUT);
  pinMode(PIN_LED_RIGHT, OUTPUT);
}

void loop() {
  processJoystick();
  processBluetoothInput();
}

void processJoystick() {
  int vertical = analogRead(PIN_JOYSTICK_VERT);
  int horizontal = analogRead(PIN_JOYSTICK_HORZ);
  int select = digitalRead(PIN_JOYSTICK_SEL);

  int verticalMessage = 0;
  int horizontalMessage = 0;

  if (vertical > JOYSTICK_UP_THRESHOLD) {
    verticalMessage = 1;
  }
  else if (vertical < JOYSTICK_DOWN_THRESHOLD) {
    verticalMessage = -1;
  }
  
  if (horizontal > JOYSTICK_LEFT_THRESHOLD) {
    horizontalMessage = 1;
  }
  else if (horizontal < JOYSTICK_RIGHT_THRESHOLD) {
    horizontalMessage = -1;
  }
  
  String joystickState = String(verticalMessage, DEC) + "," + String(horizontalMessage, DEC) + "," + String(select, DEC);
  if (!joystickState.equals(previousJoystickState)) {
    previousJoystickState = joystickState;
    Bluetooth.println(joystickState);
    parseMessage(joystickState);
  }
}

void processBluetoothInput() {
  if (Bluetooth.available()) {
    //Serial.print((char) Bluetooth.read());
    bluetoothReceiveBuffer += (char) Bluetooth.read();
    int index = bluetoothReceiveBuffer.indexOf(DELIMETER);
    while (index != -1) {
      String message = bluetoothReceiveBuffer.substring(0, index);
      bluetoothReceiveBuffer = bluetoothReceiveBuffer.substring(index + 1);
      parseMessage(message);
      index = bluetoothReceiveBuffer.indexOf(DELIMETER);
    }
  }
}

void parseMessage(String message) {
  Serial.println(message);
  
  
  int firstCommaIndex = message.indexOf(',');
  int secondCommaIndex = message.indexOf(',', firstCommaIndex + 1);
  int vertical = message.substring(0, firstCommaIndex).toInt();
  int horizontal = message.substring(firstCommaIndex + 1, secondCommaIndex).toInt();
  
  int topValue = 0;
  int bottomValue = 0;
  int leftValue = 0;
  int rightValue = 0;
  
  if (vertical == 1) {
    bottomValue = 255;
  }
  else if (vertical == -1) {
    topValue = 255;
  }
  
  if (horizontal == 1) {
    rightValue = 255;
  }
  else if (horizontal == -1) {
    leftValue = 255;
  }
  
  analogWrite(PIN_LED_TOP, topValue);
  analogWrite(PIN_LED_RIGHT, rightValue);
  analogWrite(PIN_LED_BOTTOM, bottomValue);
  analogWrite(PIN_LED_LEFT, leftValue);
}
