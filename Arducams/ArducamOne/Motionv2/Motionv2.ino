#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#define FIREBASE_HOST "houseplant-monitor.firebaseio.com"
#define FIREBASE_AUTH "1N7ntYmFD3HCJngEVKeKtAbVCJEBEK7TOVpoNLnb"
#define WIFI_SSID "JOSESLAPTOP"
#define WIFI_PASSWORD "hello1234"
float tempVolt[70];
int Reads = 0;
double Total = 0;
double Average = 0;
int Count = 0;
int Mcount = 0;
int OutofRange = 0;
bool breakI;
unsigned long lastPrint = 0;
bool lockEventValue;
//////////////////////////
// Hardware Definitions //
//////////////////////////
#define PIR_AOUT A0  // PIR analog output on A0
#define PIR_DOUT 2   // PIR digital output on D2
#define LED_PIN  13  // LED to illuminate on motion

#define PRINT_TIME 100 // Rate of serial printouts
//unsigned long lastPrint = 0; // Keep track of last serial out
//bool closed = true;
void setup()
{

  Serial.begin(115200);  // Serial is used to view Analog out
  // Analog and digital pins should both be set as inputs:
  // Connect to WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print("*");
  }

  Serial.println("");
  Serial.println("WiFi connection Successful");
  Serial.print("The IP Address of ESP8266 Module is: ");
  Serial.println(WiFi.localIP());// Print the IP address
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  Serial.println("Firebase connection successful");
  Firebase.stream("security/lockSet");


  //closed = Firebase.getBool("tools/lock");
  //Serial.println(closed);
  delay(700);
  pinMode(PIR_AOUT, INPUT);
  pinMode(PIR_DOUT, INPUT);

  // Configure the motion indicator LED pin as an output
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW); // Turn the LED off
}

void loop()
{
  if (Firebase.available())
  {
    FirebaseObject event = Firebase.readEvent();
    lockEventValue = event.getBool("data");
    if (lockEventValue == false)
    {
      Serial.print("System Open");
    }
    else
    {
      Serial.print("System Closed");
    }
  }

  // Read OUT pin, and set onboard LED to mirror output
  //readDigitalValue();

  // Read A pin, print that value to serial port:
  //if(Mcount > 0 && Mcount%4 == 0)
  //{
  //closed = false;
  //}
  //bool closed;
  //closed = Firebase.getBool("security/lockSet");
  printAnalogValue();
  //if(closed)
  //{

  // }


  // else{
  // systemOpen();
  //}
}

void systemOpen()
{
  //Serial.println("System open");
  //delay(3000);
  //Serial.println("System closing in: 3");
  //delay(300);
  //Serial.println("System closing in: 2");
  //delay(300);
  //Serial.println("System closing in: 1");
  //delay(300);
  //closed = true;
  //Mcount++;

}

void readDigitalValue()
{
  // The OpenPIR's digital output is active high
  int motionStatus = digitalRead(PIR_DOUT);

  // If motion is detected, turn the onboard LED on:
  if (motionStatus == HIGH)
    digitalWrite(LED_PIN, HIGH);
  else // Otherwise turn the LED off:
    digitalWrite(LED_PIN, LOW);
}

void printAnalogValue()
{



  if ( (lastPrint + PRINT_TIME) < millis() )
  {
    lastPrint = millis();
    // Read in analog value:
    unsigned int analogPIR = analogRead(PIR_AOUT);
    // Convert 10-bit analog value to a voltage
    // (Assume high voltage is 5.0V.)
    float voltage = (float) analogPIR / 1024.0 * 5.0;
    // Print the reading from the digital pin.
    // Mutliply by 5 to maintain scale with AOUT.
    //Serial.print(5 * digitalRead(PIR_DOUT));
    //Serial.print(',');    // Print a comma
    //Serial.print(2.5);    // Print the upper limit
    //Serial.print(',');    // Print a comma
    //Serial.print(1.7);    // Print the lower limit
    //Serial.print(',');    // Print a comma
    //Serial.print(voltage); // Print voltage
    //Serial.println();
    tempVolt[Reads] = voltage;
    //Serial.println(Count);
    if (Reads >= 14)
    {
      for (int i = 0; i < Reads; i++)
      {
        Total = Total + tempVolt[i];
        if (tempVolt[i] < 3.0 || tempVolt[i] > 3.6)
        {
          OutofRange++;
        }
      }
      //if(voltage < 3.0 || voltage > 3.6)
      //{
      //  OutofRange++;
      //}

      Average = Total / 14;
      //if(Average >= 2.3 && Average <= 4.3)
      //{
      if (OutofRange >= 6)
      {
        //Serial.println("Motion detected");
        Firebase.setBool("alerts/motionDetected", true);
        if(lockEventValue == true)
        {
          //Serial.println("Justin help me");
          Firebase.setBool("alerts/breakIn", true);
        }
      }
      else {
        //Serial.println("Motion not detected");
        Firebase.setBool("alerts/motionDetected", false);
      }
      Reads = 0;
      Total = 0;
      Average = 0;
      Count = 0;
      Mcount++;
      OutofRange = 0;
    }
    else {
      Reads++;
      Count++;
    }
  }
}
