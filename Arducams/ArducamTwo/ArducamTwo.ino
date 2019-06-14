// ArducamTwo - LED strip, water pump, lock solenoid, soil moisture sensor, and camera

#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include "water_pump.h"
#include "lock.h"
#include "LED_light_strip.h"
#include <Wire.h>
#define FIREBASE_HOST "houseplant-monitor.firebaseio.com"
#define FIREBASE_AUTH "1N7ntYmFD3HCJngEVKeKtAbVCJEBEK7TOVpoNLnb"
#define WIFI_SSID "JOSESLAPTOP"
#define WIFI_PASSWORD "hello1234"
#define array_length 10
enum soil_states {soil_off, soil_read, soil_output} soil_state;
int array_index;
bool signalDetected;
bool readingData;
bool sensorEventValue;
bool soilIsDry;
bool breakI;
int currPlant;
int Soil[array_length]; // array of values to average
int soil_power = 15; // Pin for sensor VCC
int soil_pin = A0; // Pin for sensor signal
int soil_total; // running total
int soil_moisture; // actual value to return
int soil_reading; //soil moisture sensor reading

void setup() {
  // pins for camera
  //  pinMode(4,OUTPUT);
  //  pinMode(5,OUTPUT); Am not sure of INPUT/OUTPUT of camera
  //  pinMode(12,OUTPUT);
  //  pinMode(13,OUTPUT);
  //  pinMode(14,OUTPUT);
  //  pinMode(16,OUTPUT);

  //pins for soil moisture sensor
  //pinMode(A0, INPUT);
  //pinMode(3, OUTPUT);


  //pins for 12V components
  pinMode(0, OUTPUT); //water pump
  pinMode(16, OUTPUT); //lock
  pinMode(2, OUTPUT); //LED strip

  Serial.begin(9600);

  pinMode(soil_power, OUTPUT);
  digitalWrite(soil_power, LOW);
  initializeVariables();
  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wifi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.stream("");
  timeClient.begin();
  ledEventValue = Firebase.getBool("/care/lightSet");
  ledManualOnOff();
  currPlant = Firebase.getInt("/plantData/currentPlant");
  digitalWrite(waterPumpPin, HIGH);
  signalDetected = Firebase.getBool("/signals/updateSensors");
  lockEventValue = Firebase.getBool("/security/lockSet");
  manualLockUnlock();
}



void loop() {
  if (Firebase.failed()) {
    Serial.println("Failed to stream inside loop");
    Serial.println(Firebase.error());
    return;
  }
  if (Firebase.available()) {
    FirebaseObject event = Firebase.readEvent();
    String eventPath = event.getString("path");
    Serial.print("Event path is:");
    Serial.println(eventPath);

    if (eventPath == "/security/lockSet") {
      lockEventValue = event.getBool("data");
      manualLockUnlock(); // call function if lock value changed manually
      Serial.print("Lock function called");
      //OR keypadValue correct OR fingerprint recognized
      return;
    }
    // -- breakIN ALERT --//
    if (eventPath == "/alerts/breakIn")
    {
      breakI = event.getBool("data");
      if (breakI)
      {
        for (int i = 0; i < 10; ++i) {
          ledEventValue = true;
          ledManualOnOff();
          delay(100);
          ledEventValue = false;
          ledManualOnOff();
          delay(100);
        }
      }
      return;
    }
    // -- soil moisture -- //
    if (eventPath == "/plantData/currentPlant")
    {
      currPlant = event.getInt("data");
      return;
    }

    // -- these if statements control light behavior -- //

    if (eventPath == "/care/lightSet") {
      ledEventValue = event.getBool("data");
      ledManualOnOff();
      return;
    }
    if (eventPath == "/care/lightScheduleOn/hour" || eventPath == "/care/lightScheduleOn/minute") {
      typeOfSchedChange = eventPath;
      setLEDScheduleOnTime();
      return;
    }
    if (eventPath == "/care/lightScheduleOff/hour" || eventPath == "/care/lightScheduleOff/minute" ) {
      typeOfSchedChange = eventPath;
      setLEDScheduleOffTime();
      return;
    }
    // -- these if statments control water pump behavior -- //

    if (eventPath == "/care/waterPumpSet") {
      waterPumpEventStatus = event.getBool("data");
      manualWater();
      return;
    }
    if (eventPath == "/signals/updateSensors")
    {
      sensorEventValue = event.getBool("data");
      signalDetected = sensorEventValue;
      return;
    }
    if (eventPath == "/care/waterSchedule/day" ||
        eventPath == "/care/waterSchedule/hour" ||
        eventPath == "/care/waterSchedule/minute" ||
        eventPath == "/care/waterSchedule/setting" ||
        eventPath == "/care/waterSchedule/waterSetting") {
      typeOfPumpSchedChange = eventPath;
      Serial.println("Setting schedule");
      setPumpSchedule();
      return;
    }

    if (eventPath == "/alerts/plantDry") {
      Serial.print("Run auto-watering");
      waterPumpEventStatus = true;
      manualWater();
      return;
    }
  }

  if (signalDetected) {
    Serial.println("Signal detected, reading sensor..");
    readingData = true;
    soil_state = soil_off;
    Firebase.setBool("signals/updateSensors", 0);
    while (readingData) {
      run_sensorsSM();
      delay(1000); // read every 1 second
    }
    //Firebase.setBool("signals/updateSensors", 0);
    //signalDetected = false;
    //
    //always do this behavior
    getCurrentTime();
    checkLEDschedule();
    checkPumpSchedule();
    delay(1000);
  }

}


void run_sensorsSM() {
  switch (soil_state) {
    case soil_off:
      Serial.println("Starting sensor readings");
      // soil moisture
      array_index = 0;
      soil_total = 0;
      soil_moisture = 0;
      for (int i = 0; i < array_length; i++) { // initialize array to zeros
        Soil[i] = 0;
      }
      soil_state = soil_read;
      break;

    case soil_read:
      //Serial.println("Reading soil moisture");
      Serial.print("Value ");
      Serial.println(array_index + 1);
      // reading soil moisture
      soil_reading = readSoil();
      soil_reading = constrain(soil_reading, 0, 880); // make sure value is within constraints
      soil_reading = map(soil_reading, 0, 880, 0, 100); // map value so it's between 0 and 100
      Soil[array_index] = soil_reading;  // add reading to array
      soil_total += Soil[array_index]; // add reading to total
      Serial.print("Soil moisture reading is ");
      Serial.println(soil_reading);
      //update SM state
      array_index += 1; // increment array index
      if (array_index >= array_length) {
        soil_state = soil_output;
      }
      else {
        soil_state = soil_read;
      }

      break;

    case soil_output:
      readingData = false;
      Serial.println("\nDone reading sensors");
      soil_moisture = soil_total / array_length;
      Serial.print("Average Soil moisture is ");
      Serial.println(soil_moisture);
      if (soil_moisture <= 15)
      {
        Firebase.setBool("alerts/plantDry", true);
        soilIsDry = true;
      }
      else
      {
        soilIsDry = false;
        //Firebase.setBool("alerts/plantDry", false);
      }
      soil_state = soil_off;
      updateMoisture(soil_moisture);
      break;
  }
}

void initializeVariables() {
  //signalDetected = false;
  readingData = false;
  soil_state = soil_off;
  //soil sensor variables
  soil_reading = 0;
  soil_moisture = 0;
  soil_total = 0;
}

int readSoil() {
  digitalWrite(soil_power, HIGH);//turn VCC "On"
  delay(10);//wait 10 milliseconds
  int val = analogRead(soil_pin);//Read the SIG value form sensor
  digitalWrite(soil_power, LOW);//turn VCC "Off"
  return val;// send current moisture value
}

void updateMoisture(int moisture) {
  if (currPlant == 1)
  {
    Firebase.setInt("plantData/1/moisture_level", moisture);
    Serial.print("Failed to get moisture level");
  }
  else
  {
    Firebase.setInt("plantData/2/moisture_level", moisture);
  }
  if (Firebase.failed()) {
    Serial.print("pushing soil moisture failed:");
    Serial.println(Firebase.error());
    return;
  }
}
