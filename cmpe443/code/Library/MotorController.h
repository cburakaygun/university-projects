#ifndef MOTOR_H
#define MOTOR_H

#include "GPIO.h"
#include "LPC407x_8x_177x_8x.h"

/*
	Assumes the followings:
  * Left motor is connected to OUTA of Motor Controller and right motor is connected to OUTB of Motor Controller
  * When IN1 is LOW and IN2 is HIGH, left motor runs in forward direction
  * When IN3 is LOW and IN4 is HIGH, right motor runs in forward direction
*/

#define MOTOR_ENA_PWM0_MR ((uint8_t) 1) // ENA pin uses PWM0 channel 1
#define MOTOR_ENB_PWM0_MR ((uint8_t) 2) // ENB pin uses PWM0 channel 2

#define IOCON_MOTOR_ENA	*((volatile uint32_t *) 0x4002C088)	// P1_2 (P30)
#define IOCON_MOTOR_ENB *((volatile uint32_t *) 0x4002C08C)	// P1_3 (P29)

#define MOTOR_DIRECTION_PORT PORT1

#define IOCON_MOTOR_IN1  *((volatile uint32_t *) 0x4002C094)	// P1_5 (P28)
#define IOCON_MOTOR_IN2  *((volatile uint32_t *) 0x4002C098)	// P1_6 (P27)
#define IOCON_MOTOR_IN3  *((volatile uint32_t *) 0x4002C09C)	// P1_7 (P26)
#define IOCON_MOTOR_IN4  *((volatile uint32_t *) 0x4002C0AC)	// P1_11 (P25)

#define MOTOR_IN1_MASK	((uint32_t) (1<<5))		// P1_5 (P28)
#define MOTOR_IN2_MASK	((uint32_t) (1<<6))		// P1_6 (P27)
#define MOTOR_IN3_MASK	((uint32_t) (1<<7))		// P1_7 (P26)
#define MOTOR_IN4_MASK	((uint32_t) (1<<11))	// P1_11 (P25)

#define MOTOR_DIRECTION_MASK (MOTOR_IN1_MASK | MOTOR_IN2_MASK | MOTOR_IN3_MASK | MOTOR_IN4_MASK)

void MOTORCONTROLLER_Init(void);

void MOTOR_LEFT_Forward(void);
void MOTOR_RIGHT_Forward(void);
void MOTOR_LEFT_Backward(void);
void MOTOR_RIGHT_Backward(void);

void MOTOR_LEFT_SetSpeed(uint32_t speed);
void MOTOR_RIGHT_SetSpeed(uint32_t speed);

void MOTOR_Stop(void);

#endif
