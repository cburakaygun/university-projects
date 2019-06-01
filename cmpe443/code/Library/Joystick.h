#ifndef JOYSTICK_H
#define JOYSTICK_H

#include "GPIO.h"
#include "LPC407x_8x_177x_8x.h"

#define JOYSTICK_PORT	PORT5

#define JOYSTICK_LEFT_MASK		((uint32_t) 1<<0)	// P5_0 (P39)
#define JOYSTICK_DOWN_MASK		((uint32_t) 1<<1)	// P5_1 (P38)
#define JOYSTICK_UP_MASK		((uint32_t) 1<<2)	// P5_2 (P32)
#define JOYSTICK_CENTER_MASK	((uint32_t) 1<<3)	// P5_3 (P31)
#define JOYSTICK_RIGHT_MASK		((uint32_t) 1<<4)	// P5_4 (P37)

#define IOCON_JOYSTICK_LEFT 	*((volatile uint32_t *) 0x4002C280)
#define IOCON_JOYSTICK_DOWN 	*((volatile uint32_t *) 0x4002C284)
#define IOCON_JOYSTICK_UP 		*((volatile uint32_t *) 0x4002C288)
#define IOCON_JOYSTICK_CENTER	*((volatile uint32_t *) 0x4002C28C)
#define IOCON_JOYSTICK_RIGHT 	*((volatile uint32_t *) 0x4002C290)

void JOYSTICK_Init(void);

uint8_t JOYSTICK_LEFT_Pressed(void);	// If left Joystick button is pressed, returns 1; else returns 0
uint8_t JOYSTICK_DOWN_Pressed(void);	// If down Joystick button is pressed, returns 1; else returns 0
uint8_t JOYSTICK_UP_Pressed(void);		// If up Joystick button is pressed, returns 1; else returns 0
uint8_t JOYSTICK_CENTER_Pressed(void);	// If center Joystick button is pressed, returns 1; else returns 0
uint8_t JOYSTICK_RIGHT_Pressed(void);	// If right Joystick button is pressed, returns 1; else returns 0

#endif
