#include "Parameters.h"
#include "SpeedSensor.h"
#include "Timer.h"

void SPEEDSENSOR_Init() {
	// Sets FUNC bits of IOCON_SPEEDSENSOR to 001 (T2_CAP0 functionality)
	uint32_t value = IOCON_SPEEDSENSOR;
	value &= ~(1<<2);
	value |= (1<<1 | 1<<0);
	IOCON_SPEEDSENSOR = value;
	
	TIMER2_Init(); // Timer2 is disabled on initialization
	TIMER2->MR0 = ROTATION_NUMBER_FOR_90_DEGREE * HOLE_NUMBER;
	TIMER2->MCR |= 7; // Resets and Stop TC on MR0 match and Interrupts
}

void SPEEDSENSOR_Start() {
	TIMER2_Start();
}

void SPEEDSENSOR_Stop() {
	TIMER2_Stop();
}
