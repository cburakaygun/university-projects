#include "Car.h"
#include "External.h"
#include "Parameters.h"

void EXTERNAL_Init() {
	// Changes the functionality of the push button as EINT0 (FUNC (2:0) -> 001)
	uint32_t value = IOCON_PUSH_BUTTON;
	value &= ~7;
	value |= 1;
	IOCON_PUSH_BUTTON = value;
	
	EXT->EXTMODE |= 1; // Changes the External interrupt MODE as Edge Sensitive
	
	NVIC_EnableIRQ(EINT0_IRQn);
	NVIC_ClearPendingIRQ(EINT0_IRQn);
}

void EINT0_IRQHandler() {
	if (START_MODE == '*') { // manual
		CAR_ChangeModeTo('#'); // auto
	} else {
		CAR_ChangeModeTo('*');
	}
	
	// Clears interrupt for EINT0
	EXT->EXTINT |= 1;
	NVIC_ClearPendingIRQ(EINT0_IRQn);
}
