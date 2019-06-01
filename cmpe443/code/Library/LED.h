#ifndef LED_H
#define LED_H

#include "GPIO.h"
#include "LPC407x_8x_177x_8x.h"

#define LED_PORT PORT0

#define LED_FRONT_LEFT_MASK		((uint32_t) 1<<0) // P0_0 (P9)
#define LED_FRONT_RIGHT_MASK	((uint32_t) 1<<1) // P0_1 (P10)
#define LED_BACK_LEFT_MASK		((uint32_t) 1<<8) // P0_8 (P12)
#define LED_BACK_RIGHT_MASK		((uint32_t) 1<<7) // P0_7 (P13)

#define LED_FRONT_MASK	(LED_FRONT_LEFT_MASK | LED_FRONT_RIGHT_MASK)
#define LED_BACK_MASK	(LED_BACK_LEFT_MASK | LED_BACK_RIGHT_MASK)
#define LED_LEFT_MASK	(LED_FRONT_LEFT_MASK | LED_BACK_LEFT_MASK)
#define LED_RIGHT_MASK	(LED_FRONT_RIGHT_MASK | LED_BACK_RIGHT_MASK)
#define LED_MASK (LED_FRONT_LEFT_MASK | LED_FRONT_RIGHT_MASK | LED_BACK_LEFT_MASK | LED_BACK_RIGHT_MASK)

void LED_Init(void);

void LED_FRONT_On(void);	// Turns on FRONT_LEFT and FRONT_RIGHT LEDs
void LED_BACK_On(void);		// Turns on BACK_LEFT and BACK_RIGHT LEDs

void LED_LEFT_Blink(void);	// Blinks FRONT_LEFT and BACK_LEFT LEDs
void LED_RIGHT_Blink(void);	// Blinks FRONT_RIGHT and BACK_RIGHT LEDs

void LED_Off(void);	// Turns off all the LEDs

#endif
