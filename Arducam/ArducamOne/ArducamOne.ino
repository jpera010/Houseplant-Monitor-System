#include <NTPClient.h> 
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <WiFiUdp.h> 

#define FIREBASE_HOST "houseplant-monitor.firebaseio.com"
#define FIREBASE_AUTH "1N7ntYmFD3HCJngEVKeKtAbVCJEBEK7TOVpoNLnb"
#define WIFI_SSID "Justin's P10"
#define WIFI_PASSWORD "thatsucc"


int light; 
int light_checker; 
int water; 
int water_checker; 
int sched_day, sched_hour, sched_min, curr_hour, curr_min, curr_day, sched_setting; 
bool watered; /////bool for water_scheduler() function for checking whether system has watered yet

WiFiUDP ntpUDP; 
NTPClient timeClient(ntpUDP, "3.north-america.pool.ntp.org", -25200); 

void water_scheduler() {
  timeClient.update(); 
  curr_day = timeClient.getDay();               ///////////// Sunday = 0, Monday = 1, etc...
  curr_hour = timeClient.getHours();            ////////////Army hours: 14:00 = 2:00 pm 
  curr_min = timeClient.getMinutes();
  Serial.println(timeClient.getDay()); 
  Serial.println(timeClient.getFormattedTime()); 
  
  sched_day = Firebase.getInt("schedule/day"); 
  sched_hour = Firebase.getInt("schedule/hour"); 
  sched_min = Firebase.getInt("schedule/minute"); 
  sched_setting = Firebase.getInt("schedule/setting"); 
  if (Firebase.failed()) {
    Serial.println("Failed to retrieve user settings"); 
    Serial.println(Firebase.error()); 
    return;
  }
  Serial.print("Sched_day: "); 
  Serial.print(sched_day); 
  Serial.print(", Sched_hour: "); 
  Serial.print(sched_hour); 
  Serial.print(", Sched_minute: "); 
  Serial.print(sched_min); 
  Serial.print(", Sched_setting: "); 
  Serial.println(sched_setting); 

  if (sched_setting == 1) {                   /////Sched_setting = 1 => daily 
    
  }
  else if (sched_setting = 2) {               /////Sched_setting = 2 => weekly
    if (curr_day == sched_day && curr_hour == sched_hour && curr_min == sched_min && !watered) {
      Serial.println("Scheduled watering beginning...");
      digitalWrite(14, LOW); 
      delay(2000); 
      digitalWrite(14, HIGH); 
      watered = true;  
      Serial.println("Scheduled watering finished..."); 
    }
    else {
      watered = false; 
    }
  }
}

void lighting() {
  int light_checker = Firebase.getInt("tools/light"); 
  if (Firebase.failed()) {
    Serial.println("Failed to retrieve light value"); 
    Serial.println(Firebase.error()); 
    return; 
  }
    ////// Controls the lighting of the system...checks to see if there is a change before applying change
  if (light != light_checker) {                
    light = light_checker; 
    if (light == 1) {
      digitalWrite(16, HIGH); 
      Serial.println("Setting PIN16 to HIGH"); 
    }
    else {
      digitalWrite(16, LOW); 
      Serial.println("Setting PIN16 to LOW");
    }
  }
}

void manual_pump() {
    ////// Controls the water pump of the system...change values according to tests!!!!! CRITICAL !!!!! BE CAREFUL NOT TO LEAVE PUMP ON TOO LONG
  int water_checker = Firebase.getInt("tools/water_pump"); 
  if (Firebase.failed()) {
    Serial.println("Failed to retrieve light value"); 
    Serial.println(Firebase.error()); 
    return; 
  }
  if (water_checker == 1) {                    
    
    digitalWrite(14, LOW); 
    Serial.println("Setting PIN14 to LOW"); 
    Serial.println("Watering plant..."); 
    
    delay(1500);        ///// Modify this in milliseconds for how long pump lasts
    
    digitalWrite(14, HIGH);
    Serial.println("Setting PIN14 to HIGH");
    Serial.println("Done watering...");

    Firebase.setInt("tools/water_pump", 0);       ////// Setting database value back to 0
    Serial.println("Database value set back to 0"); 
  }
}

void setup() {
  light = 0; 
  water = 0; 
  watered = false; 
  Serial.println("Setting up..."); 
  pinMode(16, OUTPUT); 
  Serial.begin(9600);
  

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
  timeClient.begin(); 
}

void loop() {
  
  water_scheduler(); 

  lighting(); 

  manual_pump(); 
  
  delay(3000); 
}
