#ifndef LED_LIGHT_STRIP_H
#define LED_LIGHT_STRIP_H

#define ledStripPin 2
#include "scheduling.h"
bool ledEventValue;
bool ledSchedOnDone;
bool ledSchedOffDone;
String typeOfSchedChange = "";
int ledschedHourOn, ledschedMinOn = 0;
int ledschedHourOff, ledschedMinOff = 0;

void ledManualOnOff() {
  if (ledEventValue == true) {
    digitalWrite(ledStripPin, LOW);
  }
  else {
    digitalWrite(ledStripPin, HIGH);
  }
}

void setLEDScheduleOffTime() {
  if (typeOfSchedChange == "/care/lightScheduleOff/hour") {
    ledschedHourOff = Firebase.getInt("/care/lightScheduleOff/hour");
  }
  if (typeOfSchedChange == "/care/lightScheduleOff/minute") {
    ledschedMinOff = Firebase.getInt("/care/lightScheduleOff/minute");
  }
  ledSchedOffDone = false;
  if (Firebase.failed()) {
    //    Serial.println("Failed to retrieve schedule");
    //    Serial.println(Firebase.error());
    return;
  }
}

void setLEDScheduleOnTime() {
  if (typeOfSchedChange == "/care/lightScheduleOn/hour") {
    ledschedHourOn = Firebase.getInt("/care/lightScheduleOn/hour");
  }
  if (typeOfSchedChange == "/care/lightScheduleOn/minute") {
    ledschedMinOn = Firebase.getInt("/care/lightScheduleOn/minute");
  }
  ledSchedOnDone = false;
  if (Firebase.failed()) {
    //    Serial.println("Failed to retrieve schedule");
    //    Serial.println(Firebase.error());
    return;
  }
}

void checkLEDschedule() {
  if (ledSchedOnDone == false) {
    if ((currHour == ledschedHourOn) && (currMin == ledschedMinOn)) {
      Firebase.setBool("/care/lightSet", true);
      if (Firebase.failed()) {
        //    Serial.println("Failed to retrieve schedule");
        //    Serial.println(Firebase.error());
        return;
      }
      ledSchedOnDone = true;
      digitalWrite(ledStripPin, LOW);
    }
  }
  if (ledSchedOffDone == false) {
    if ((currHour == ledschedHourOff) && (currMin == ledschedMinOff)) {
      Firebase.setBool("/care/lightSet", false);
      if (Firebase.failed()) {
        //    Serial.println("Failed to retrieve schedule");
        //    Serial.println(Firebase.error());
        return;
      }
      ledSchedOffDone = true;
      digitalWrite(ledStripPin, HIGH);
    }
  }
  //delay(1000);
}

#endif
