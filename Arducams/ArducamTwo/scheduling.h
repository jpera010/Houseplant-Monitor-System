#ifndef SCHEDULING_H
#define SCHEDULING_H

#include <NTPClient.h>
#include <WiFiUdp.h>
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "3.north-america.pool.ntp.org", -25200);

int currDay, currHour, currMin = 0;

void getCurrentTime() {
  timeClient.update();
  currHour = timeClient.getHours();  // 24h time: 14:00 = 2:00 pm
  currMin = timeClient.getMinutes();
  //  Serial.println(timeClient.getDay());
  //  Serial.println(timeClient.getFormattedTime());
}

//void getCurrentTimeForPump() {
//  timeClient.update();
//  currDay = timeClient.getDay(); //Sunday = 0, Monday = 1, etc...
//  currHour = timeClient.getHours();  // 24h time: 14:00 = 2:00 pm
//  currMin = timeClient.getMinutes();
//  //  Serial.println(timeClient.getDay());
//  //  Serial.println(timeClient.getFormattedTime());
//}


#endif
