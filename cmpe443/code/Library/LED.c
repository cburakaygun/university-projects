#include "LED.h"
#include "Parameters.h"
#include "SystemStructures.h"
#include "Timer.h"
#include "Wait.h"

void LED_Init() {
	TIMER3_Init();	// Timer3 will be used for blink action
	TIMER3->PR = PERIPHERAL_CLOCK_FREQUENCY / 1000 - 1; // TC increments every 1 milliseconds
	TIMER3->MR0 = LED_BLINK_T_ON; // milliseconds
	TIMER3->MCR |= 3; // Resets TC on MR0 match and Interrupts
	
	GPIO_DIR_Write(LED_PORT, LED_MASK, OUTPUT);
	LED_Off();
	waitMilliseconds(5); // When this method is not called, LEDs don't start in turned off state
}

void LED_FRONT_On() {
	LED_Off(); // Turns off all the LEDs
	GPIO_PIN_Write(LED_PORT, LED_FRONT_MASK, HIGH); // Turns on front LEDs
}

void LED_BACK_On() {
	LED_Off(); // Turns off all the LEDs
	GPIO_PIN_Write(LED_PORT, LED_BACK_MASK, HIGH); // Turns on back LEDs
}

void LED_LEFT_Blink() {
	LED_Off(); // Turns off all the LEDs
	PREVIOUS_LED_STATE = 0;
	IS_LEFT = 1; // Blinks left LEDs
	TIMER3_Start(); // Timer3 will cause blink action
}

void LED_RIGHT_Blink() {
	LED_Off(); // Turns off all the LEDs
	PREVIOUS_LED_STATE = 0;
	IS_LEFT = 0; // Blinks right LEDs
	TIMER3_Start(); // Timer3 will cause blink action
}

void LED_Off() {
	GPIO_PIN_Write(LED_PORT, LED_MASK, LOW);
	TIMER3_Stop(); // Timer3 is used for blink action
}

// Toggles the PIN value of left or right LEDs
void TIMER3_IRQHandler(void) {
	PREVIOUS_LED_STATE = ~PREVIOUS_LED_STATE; // Toggles PIN value for LED
	if (IS_LEFT) {
		GPIO_PIN_Write(LED_PORT, LED_LEFT_MASK, PREVIOUS_LED_STATE);
	} else {
		GPIO_PIN_Write(LED_PORT, LED_RIGHT_MASK, PREVIOUS_LED_STATE);
	}
	
	NVIC_ClearPendingIRQ(TIMER3_IRQn);
	TIMER3->IR |= 1<<0; // Clears interrupt bit for MAT0
}
