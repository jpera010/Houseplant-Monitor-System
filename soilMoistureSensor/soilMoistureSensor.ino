// libraries for wifi and firebase
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#include <Wire.h> // Used to establied serial communication on the I2C bus
#include "SparkFunTMP102.h" // Used to send and recieve specific information from our sensor

#define array_length 10
enum soil_states {soil_off, soil_read, soil_output} soil_state;
int array_index; 
bool signalDetected;
bool readingData;
bool sensorEventValue;

int soil_pin = A0; // Pin for sensor signal
int soil_power = 16; // Pin for sensor VCC

// variables for soil moisture sensor
int Soil[array_length]; // array of values to average

int soil_total; // running total
int soil_moisture; // actual value to return
int soil_reading; //soil moisture sensor reading

#define FIREBASE_HOST "houseplant-monitor.firebaseio.com"
#define FIREBASE_AUTH "1N7ntYmFD3HCJngEVKeKtAbVCJEBEK7TOVpoNLnb"
//#define WIFI_SSID "Justin's P10"
//#define WIFI_PASSWORD "412412113"
//#define WIFI_SSID "V30_2056"
//#define WIFI_PASSWORD "thatsucc"
//#define WIFI_SSID "JOSESLAPTOP 0477"
//#define WIFI_PASSWORD "8F635h7|"
#define WIFI_SSID "FirePuff12"
#define WIFI_PASSWORD "VolcaMang0rona715"

void setup() {
  Serial.begin(115200);
  pinMode(soil_power, OUTPUT);
  digitalWrite(soil_power, LOW);
  initializeVariables();
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
  Firebase.stream("signals/updateSensors");
}

void loop(){
  
  if (Firebase.available())
  {
    FirebaseObject event = Firebase.readEvent();
    sensorEventValue = event.getBool("data");
    if (sensorEventValue == false)
    {
      //Serial.println("SignalNotDetected");
      signalDetected = sensorEventValue;
    }
    else
    {
      //Serial.println("Signal Detected");
      signalDetected = sensorEventValue;
    }
  }
  if (signalDetected) {
    Serial.println("Signal detected, reading sensor..");
    readingData = true;
    soil_state = soil_off;
    while (readingData) {
      run_sensorsSM();
      delay(1000); // read every 1 second
    }   
    //signalDetected = false;
  }
  else {
    //signalDetected = true;
    //Serial.println("No Signal Detected");
    delay(500); //run every .5 seconds
  }
  
}

void run_sensorsSM(){
    switch(soil_state) {
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
        if(soil_moisture <= 15)
        {
          Firebase.setBool("alerts/plantDry", true);
        }
        else
        {
          Firebase.setBool("alerts/plantDry", false);
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
  Firebase.setInt("plantData/1/moisture_level", moisture); 
  if (Firebase.failed()) {
      Serial.print("pushing soil moisture failed:");
      Serial.println(Firebase.error());  
      return;  
  }
}
