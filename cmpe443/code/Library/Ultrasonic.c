#include "Car.h"
#include "Parameters.h"
#include "SystemStructures.h"
#include "Timer.h"
#include "Ultrasonic.h"

void ULTRASONIC_Init() {
	// Changes function of TRIGGER pin to T2_MAT3 (FUNC (2:0) -> 011)
	uint32_t value = IOCON_ULTRASONIC_TRIGGER;
	value &= ~7;
	value |= 3;
	IOCON_ULTRASONIC_TRIGGER = value;
	
	// Changes function of ECHO pin to T2_CAP0 (FUNC (2:0) -> 011)
	value = IOCON_ULTRASONIC_ECHO;
	value &= ~7;
	value |= 3;
	IOCON_ULTRASONIC_ECHO = value;
	
	TIMER2_Init(); // Timer2 is disabled upon initialization
	TIMER2->PR = PERIPHERAL_CLOCK_FREQUENCY/1000000 - 1; // TC increments every 1 microseconds
	TIMER2->MCR |= 1<<9; // Interrupts when TC matches MR3
	TIMER2->CCR = (1 << 2) | (1 << 1) | (1 << 0); // Captures on CAP0 rising & falling edges and interrupts
}

void ULTRASONIC_Start() {
	uint32_t value = TIMER2->EMR;
	value |= 1<<3;  // Sets MAT3 to HIGH
	// Sets MAT3 pin to LOW on TC match (EMC3 (11:10) -> 01)
	value &= ~(1<<11);
	value |= 1<<10;
	TIMER2->EMR = value;
	
	TIMER2->MR3 = 10; // microseconds
	
	TIMER2_Start();
}

void ULTRASONIC_Stop() {
	TIMER2_Stop();
}

uint8_t isUltrasonicTriggerPeriodOver = 0;

uint8_t ultrasonicEdgeCount = 0;
uint32_t ultrasonicRisingTime = 0;
uint32_t ultrasonicFallingTime = 0;

void TIMER2_IRQHandler() {
	if (TIMER2->IR & 1<<3) { // Interrupt due to MAT3
		if (isUltrasonicTriggerPeriodOver) {
			TIMER2->EMR |= 1<<3; // Sets MAT3 to HIGH
			TIMER2->MR3 = 10; // microseconds
			isUltrasonicTriggerPeriodOver = 0;
			ultrasonicEdgeCount = 0;
		} else {
			TIMER2->MR3 = 60000; // milliseconds; changes MR3 Register Value for Suggested Waiting
			isUltrasonicTriggerPeriodOver = 1;
		}
		
		TIMER2->TC = 0;
		TIMER2->IR = 1 << 3; // Clears IR flag for MAT3
	
	} else if (TIMER2->IR & 1<<4) { // Interrupt due to CAP0
		if (ultrasonicEdgeCount == 0) { // Rising edge
			ultrasonicRisingTime = TIMER2->CR0;
			ultrasonicEdgeCount = 1;
		} else if (ultrasonicEdgeCount == 1) { // Falling edge
			ultrasonicFallingTime = TIMER2->CR0;
			
			uint32_t ultrasonicDistance = (ultrasonicFallingTime-ultrasonicRisingTime) / 58; // cm
			if (FORWARD_FLAG == 1 && ultrasonicDistance <= OBSTACLE_DISTANCE) {
				SHOULD_ESCAPE_OBSTACLE = 1;
				ESCAPED_OBSTACLE = 0;
			} else if (BACKWARD_FLAG == 1 && SHOULD_ESCAPE_OBSTACLE == 1 && ultrasonicDistance >= OBSTACLE_ESCAPE_DISTANCE) {
				SHOULD_ESCAPE_OBSTACLE = 0;
				ESCAPED_OBSTACLE = 1;
			}
			
			ultrasonicEdgeCount = 2;
		}
		
		TIMER2->IR = 1<<4; // Clears IR flag for CAP0
	}
	
	NVIC_ClearPendingIRQ(TIMER2_IRQn);
}
