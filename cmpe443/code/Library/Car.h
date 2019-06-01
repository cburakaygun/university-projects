#ifndef CAR_H
#define CAR_H

#include "LPC407x_8x_177x_8x.h"

void CAR_Init(void);
void CAR_SetSpeed(uint32_t leftMotorSpeed, uint32_t rightMotorSpeed);	// Sets the speed of left and right motors
void CAR_Forward(void);					// Moves car forward and turns on front LEDs 
void CAR_Backward(void);				// Moves car backward and turns off back LEDs
void CAR_RotateLeft(void);				// Rotates car counter clockwise and blinks left LEDs during rotation
void CAR_RotateRight(void);			// Rotates car clockwise and blinks left LEDs during rotation
void CAR_Stop(void);					// Stops car and turns off all the LEDs

void CAR_ChangeModeTo(char newMode);	// Sets the mode of car to 'newMode'

#endif
