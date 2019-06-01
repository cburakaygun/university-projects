#include "Car.h"
#include "GPIO.h"
#include "LED.h"
#include "Parameters.h"
#include "SystemStructures.h"
#include "Timer.h"

uint8_t PREVIOUS_LED_STATE = 0;		// Initial value is not important
uint8_t IS_LEFT = 0;				// Initial value is not important

void TIMER2_Init() {
	PCONP |= 1<<22; // Enables Timer2
	
	TIMER2->CTCR &= ~3; // Changes the mode of Timer2 to timer mode
	
	TIMER2->TCR &= ~1; // Disables TC and PC of Timer2
	
	TIMER2->TCR |= 1<<1; // Resets TC and PC of Timer2
	
	TIMER2->TCR &= ~(1<<1); // Removes reset of Timer2
	
	NVIC_SetPriority(TIMER2_IRQn, 4);
}

void TIMER2_Start() {
	TIMER2->TCR |= 1<<1; // Resets TC and PC of Timer2
	
	// Enables Timer2 TC and PC for counting and removes the reset on counters
	uint32_t value = TIMER2->TCR;
	value &= ~(1<<1);
	value |= 1;
	TIMER2->TCR = value;
	
	NVIC_ClearPendingIRQ(TIMER2_IRQn);
	NVIC_EnableIRQ(TIMER2_IRQn);
}

void TIMER2_Stop() {
	NVIC_DisableIRQ(TIMER2_IRQn);
	TIMER2->TCR &= ~1; // Disables Timer2
}

void TIMER3_Init() {
	PCONP |= 1<<23; // Enables Timer3
	
	TIMER3->CTCR &= ~3; // Changes the mode of Timer3 to timer mode
	
	TIMER3->TCR &= ~1; // Disables TC and PC for Timer3
	
	TIMER3->TCR |= 1<<1; // Resets TC and PC for Timer3	
	
	TIMER3->TCR &= ~(1<<1); // Removes the reset on counters of Timer3
	
	NVIC_SetPriority(TIMER3_IRQn, 5);
}

void TIMER3_Start() {
	TIMER3->TCR |= 1<<1; // Resets TC and PC for Timer3
	
	// Enables Timer3 TC and PC for counting and removes the reset on counters
	uint32_t value = TIMER3->TCR;
	value &= ~(1<<1);
	value |= 1;
	TIMER3->TCR = value;
	
	NVIC_ClearPendingIRQ(TIMER3_IRQn);
	NVIC_EnableIRQ(TIMER3_IRQn);
}

void TIMER3_Stop() {
	NVIC_DisableIRQ(TIMER3_IRQn);
	TIMER3->TCR &= ~1; // Disables Timer3
}
