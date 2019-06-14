#ifndef WATER_PUMP_H
#define WATER_PUMP_H

#define waterPumpPin 0
#include "scheduling.h"

bool waterPumpEventStatus = false;
bool waterPumpDone = false;
String typeOfPumpSchedChange = "";
int pumpSchedDay = 1;
int pumpSchedHour = 13;
int pumpSchedMin = 55;
int pumpSchedSetting, pumpSchedWtrSetting = 1;

void waterOneFourthCup() {
  for (int i = 0; i < 4; i++) {
    digitalWrite(waterPumpPin, LOW);
    delay(875);
    digitalWrite(waterPumpPin, HIGH);
    delay(3000);
  }
  Firebase.setBool("/care/waterPumpSet", false);
  Serial.println("Plant watered with 1/4 cup");
}

void waterOneEigthCup() {
  for (int i = 0; i < 2; i++) {
    digitalWrite(waterPumpPin, LOW);
    delay(875);
    digitalWrite(waterPumpPin, HIGH);
    delay(3000);
  }
  Firebase.setBool("/care/waterPumpSet", false);
  Serial.println("Plant watered with 1/8 cup");
}

void setPumpSchedule() {
  if (typeOfPumpSchedChange == "/care/waterSchedule/day") {
    pumpSchedDay = Firebase.getInt("/care/waterSchedule/day");
  }
  if (typeOfPumpSchedChange == "/care/waterSchedule/hour") {
    pumpSchedHour = Firebase.getInt("/care/waterSchedule/hour");
  }
  if (typeOfPumpSchedChange == "/care/waterSchedule/minute") {
    pumpSchedMin = Firebase.getInt("/care/waterSchedule/minute");
  }
  if (typeOfPumpSchedChange == "/care/waterSchedule/setting") {
    pumpSchedSetting = Firebase.getInt("/care/waterSchedule/setting");
  }
  if (typeOfPumpSchedChange == "/care/waterSchedule/waterSetting") {
    pumpSchedWtrSetting = Firebase.getInt("/care/waterSchedule/waterSetting");
  }
  waterPumpDone = false;
  if (Firebase.failed()) {
    //    Serial.println("Failed to retrieve schedule");
    //    Serial.println(Firebase.error());
    return;
  }
}

void manualWater() { //for this setting, always water only 1/8 cup (1750ms)
  if (waterPumpEventStatus == true) {
    waterOneEigthCup();
    Firebase.setBool("/care/waterPumpSet", false);
    waterPumpEventStatus == false;
  }
}

void waterPlantSettings(int pmpSetting, int pmpWtrSetting) {
  if (pmpSetting == 1) {
    if (pmpWtrSetting == 1) {
      waterOneEigthCup();
    }
    else if (pmpWtrSetting == 2) {
      waterOneFourthCup();
    }
  }
  else if (pmpSetting == 2) {
    if (pmpWtrSetting == 1) {
      waterOneEigthCup();
    }
    else if (pmpWtrSetting == 2) {
      waterOneFourthCup();
    }
  }
}

void checkPumpSchedule() {
  Serial.println("Pump Schedule"); 
  if (waterPumpDone == false) {
    Serial.print("currHour: "); 
    Serial.println(currHour);
    Serial.print("currMin: ");  
    Serial.println(currMin);
    Serial.print("pumpSchedHour: "); 
    Serial.println(pumpSchedHour);
    Serial.print("pumpSchedMin: "); 
    Serial.println(pumpSchedMin);
    if ((currHour == pumpSchedHour) && (currMin == pumpSchedMin)) {
      
      Serial.println("Setting waterPumpSet to true"); 
      Firebase.setBool("/care/waterPumpSet", true);
      if (Firebase.failed()) {
        //    Serial.println("Failed to retrieve schedule");
        //    Serial.println(Firebase.error());
        return;
      }
      waterPumpDone = true;
      waterPlantSettings(pumpSchedSetting, pumpSchedWtrSetting);
    }
  }
  //delay(1000);
}


#endif
