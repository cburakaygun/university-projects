#ifndef ULTRASONIC_H
#define ULTRASONIC_H

#include "LPC407x_8x_177x_8x.h"

#define IOCON_ULTRASONIC_TRIGGER	*((volatile uint32_t*) 0x4002C024)	// P0_9 (P11), T2_MAT3
#define IOCON_ULTRASONIC_ECHO		*((volatile uint32_t*) 0x4002C010)	// P0_4 (P34), T2_CAP0

void ULTRASONIC_Init(void);
void ULTRASONIC_Start(void);
void ULTRASONIC_Stop(void);

extern uint8_t isUltrasonicTriggerPeriodOver;

extern uint8_t ultrasonicEdgeCount;  // Flag used for calculating the distance to an object
extern uint32_t ultrasonicRisingTime;  // The time of the rising edge of CAP0
extern uint32_t ultrasonicFallingTime; // The time of the falling edge of CAP0

#endif
