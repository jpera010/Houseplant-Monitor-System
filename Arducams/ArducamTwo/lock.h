#ifndef LOCK_H
#define LOCK_H
#define lockPin 16

bool lockEventValue;
bool checkForMotion;
bool motion;
int counter = 0;

void manualLockUnlock() {
  if (lockEventValue == true) {
    while (counter != 15) { //before locking, check for motion for 15 seconds
      counter += 1;
      checkForMotion = Firebase.getBool("/alerts/motionDetected");
      if (checkForMotion == true) {
        counter = 0;
      }
      //delay(1000);
    }
    digitalWrite(lockPin, LOW);
    counter = 0;
  }
  else {
    digitalWrite(lockPin, HIGH);
  }
}

#endif
