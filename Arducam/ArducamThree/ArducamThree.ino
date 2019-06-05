// ArducamThree - temperature sensor, keypad, single LED
#include <ArduinoJson.h>
#include <Keypad.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#define Password_Length 5

#include <Wire.h> // Used to establied serial communication on the I2C bus
#include "SparkFunTMP102.h" // Used to send and recieve specific information from our sensor

#define array_length 10

const int ALERT_PIN = A0;
TMP102 sensor0(0x48);

enum temp_states {temp_off, temp_read, temp_output} temp_state;
float Temp[array_length]; // array of values to average
int Tarray_index;
float temp_total; // running total
float temperature; // actual value to return
float temp_reading;
bool signalDetected;
bool readingData;
float sensor_reading;
bool unsafeTemp;
bool lockEventValue;
float minTemp = 74;
float maxTemp = 76;

int signalPin = 15;  // activates output pin 15 for LED

char Data[Password_Length]; // array to store user keypad input
String MasterPasscode = "";
//char Master[Password_Length] = "1234";
byte data_count = 0, master_count = 0;
bool unlocked;
char customKey;

const byte ROWS = 4;
const byte COLS = 4;

// keypad mapping
char hexaKeys[ROWS][COLS] = {
  {'1', '2', '3', 'A'},
  {'4', '5', '6', 'B'},
  {'7', '8', '9', 'C'},
  {'*', '0', '#', 'D'}
};

// wiring for pins on arducam
byte rowPins[ROWS] = {16, 5, 4, 0};
byte colPins[COLS] = {2, 14, 12, 13};

Keypad customKeypad = Keypad(makeKeymap(hexaKeys), rowPins, colPins, ROWS, COLS);

#define FIREBASE_HOST "houseplant-monitor.firebaseio.com"
#define FIREBASE_AUTH "1N7ntYmFD3HCJngEVKeKtAbVCJEBEK7TOVpoNLnb"
#define WIFI_SSID "Akhil's iPhone"
#define WIFI_PASSWORD "FirePuff12"


void setup() {
  Serial.begin(115200);
  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.stream("");
  initializeVariables();
  initializeTempSensor();
  pinMode(signalPin, OUTPUT);
  signalDetected = Firebase.getBool("signals/updateSensors");
  //unlocked = !(Firebase.getInt("security/lockSet"));
  //if (!unlocked) {
  //  digitalWrite(signalPin, LOW);
 // }
 // else {
 //   digitalWrite(signalPin, HIGH);
 // }
}

void loop() {
  if (Firebase.available())
  {
    FirebaseObject event = Firebase.readEvent();
    String eventPath = event.getString("path");
    Serial.print("Event path is: ");
    Serial.println(eventPath);
    if (eventPath == "/security/lockSet")
    {
      Serial.println("lockSet");
      lockEventValue = event.getBool("data");
      if (lockEventValue == false) {
        digitalWrite(signalPin, LOW);
        //Serial.println("System is open");
      }
      else {
        digitalWrite(signalPin, HIGH);
        //Serial.println("System is closed");
      }
    }
    if (eventPath == "/security/keypadPasscode")
    {
      MasterPasscode = event.getString("data");
      //Serial.println(MasterPasscode);
    }
    if (eventPath == "/signals/updateSensors")
    {
      signalDetected = event.getBool("data");
      //Serial.println(signalDetected);
    }
  }

  //MasterPasscode = Firebase.getString("keypadPasscode");
  //signalDetected = Firebase.getInt("signal/updateSensors");
  //char const *Master = MasterPasscode.c_str();
  customKey = customKeypad.getKey();
  runKeypad(customKey);
  runSensor(signalDetected);
}

void runSensor(bool b) {
  if (b) {
    Serial.println("Signal detected, reading sensors...");
    readingData = true;
    temp_state = temp_off;
    while (readingData) {
      run_tempSM();
      delay(1000); // read every 1 second
    }
  }
  return;
}

void runKeypad(char a) {
  if (a) {
    digitalWrite(signalPin, HIGH);
    Serial.println(a);
    Data[data_count] = a;
    data_count++;
    delay(50);
    digitalWrite(signalPin, LOW);
  }
  if (unlocked) {
    if (customKey == '*') {
      unlocked = 0;
      Firebase.setBool("security/lockSet", 1);
      digitalWrite(signalPin, LOW);
      while (data_count != 0) {
        Data[data_count--] = 0;
      }
    }
  }
  else {
    //if input count = 4
    if (data_count == Password_Length - 1) {
      if (!strcmp(Data, MasterPasscode.c_str())) { // strcmp returns 0 if equal
        unlocked = 1;
        Firebase.setBool("security/lockSet", 0);
        digitalWrite(signalPin, HIGH); //on for 5 seconds
      }
      else { //incorrect password, blink on/off
        digitalWrite(signalPin, HIGH);
        delay(250);
        digitalWrite(signalPin, LOW);
        delay(250);
        digitalWrite(signalPin, HIGH);
        delay(250);
        digitalWrite(signalPin, LOW);
        delay(250);
        digitalWrite(signalPin, HIGH);
        delay(250);
        digitalWrite(signalPin, LOW);
      }
      while (data_count != 0) {
        Data[data_count--] = 0;
      }
    }
  }
}

void updateTemp(int temp, bool tempAlert) {
  Firebase.setBool("signals/updateSensors", 0);
  if(tempAlert == true)
  {
    Firebase.setBool("alerts/unsafeTemp", tempAlert);
  }
  
  if (Firebase.failed()) {
    Serial.print("pushing unsafe temp alert failed:");
    Serial.println(Firebase.error());
    return;
  }
  //Firebase.setBool("signals/updateSensors", 0);
  if (Firebase.failed()) {
    Serial.print("pushing update sensor signal failed:");
    Serial.println(Firebase.error());
    return;
  }
  delay(1000);
  Firebase.setInt("plantData/temperature", temp);
  if (Firebase.failed()) {
    Serial.print("pushing temperature failed:");
    Serial.println(Firebase.error());
    return;
  }
}

void initializeTempSensor() {
  pinMode(ALERT_PIN, INPUT); // Declare alertPin as an input
  sensor0.begin();  // Join I2C bus
  sensor0.setFault(0);  // Trigger alarm immediately
  sensor0.setAlertPolarity(1); // Active HIGH
  sensor0.setAlertMode(0); // Comparator Mode.
  sensor0.setConversionRate(2);  // how quickly sensor gets new reading 0-3: 0:0.25Hz, 1:1Hz, 2:4Hz, 3:8Hz
  sensor0.setExtendedMode(0); //0:12-bit Temperature(-55C to +128C) 1:13-bit Temperature(-55C to +150C)
}

void initializeVariables() {
  temp_state = temp_off;
  //signalDetected = false;
  readingData = false;
  sensor_reading = 0;
  temperature = 0;
  temp_total = 0;
  //unsafeTemp = false;
}

void run_tempSM() {
  switch (temp_state) {
    case temp_off:
      Serial.println("Starting sensor readings");
      Tarray_index = 0;
      temp_total = 0;
      temperature = 0;
      //unsafeTemp = false;
      for (int i = 0; i < array_length; i++) { // initialize array to zeros
        Temp[i] = 0;
      }
      temp_state = temp_read;
      break;

    case temp_read:
      //Serial.print("Reading Temperature ");
      Serial.println(Tarray_index + 1);
      sensor_reading = readTemp();
      Temp[Tarray_index] = sensor_reading;  // add reading to array
      Serial.print("Sensor reading is ");
      Serial.println(sensor_reading);
      Serial.print("Sensor out of range? ");
      unsafeTemp = (sensor_reading < minTemp || sensor_reading > maxTemp);
      Serial.println(unsafeTemp);
      temp_total += Temp[Tarray_index]; // add reading to total
      Tarray_index += 1; // increment array index
      if (Tarray_index >= array_length) {
        temp_state = temp_output;
      }
      else {
        temp_state = temp_read;
      }
      break;

    case temp_output:
      Serial.println("Done reading Temperature");
      readingData = false;
      temperature = temp_total / array_length;
      int temp1 = int(temperature);
      temp_state = temp_off;
      Serial.print("Temperature is ");
      Serial.println(temperature);
      Serial.println("");
      unsafeTemp = (temperature < minTemp || temperature > maxTemp);
      updateTemp(temperature, unsafeTemp);
      break;
  }
}

float readTemp() {
  sensor0.wakeup();
  float temp = sensor0.readTempF();
  sensor0.sleep();
  return temp;
}
