#ifndef TIMER_H
#define TIMER_H

#include "LPC407x_8x_177x_8x.h"

typedef struct {
  volatile	uint32_t IR;
  volatile	uint32_t TCR;
  volatile	uint32_t TC;
  volatile	uint32_t PR;
  volatile	uint32_t PC;
  volatile	uint32_t MCR;
  volatile	uint32_t MR0;
  volatile	uint32_t MR1;
  volatile	uint32_t MR2;
  volatile	uint32_t MR3;
  volatile	uint32_t CCR;
  volatile	uint32_t CR0;
  volatile	uint32_t CR1;
			uint32_t RESERVED0[2];
  volatile	uint32_t EMR;
			uint32_t RESERVED1[12];
  volatile	uint32_t CTCR;
} TIMER_TypeDef;

//#define TIMER0	((TIMER_TypeDef*) 0x40004000) // unused
//#define TIMER1	((TIMER_TypeDef*) 0x40008000) // unused
#define TIMER2	((TIMER_TypeDef*) 0x40090000)		// Used for Ultrasonic Sensor in timer mode
#define TIMER3	((TIMER_TypeDef*) 0x40094000)		// Used for LED blink in timer mode

extern uint8_t PREVIOUS_LED_STATE;	// Toggles between 0 and 1 to blink LEDs
extern uint8_t IS_LEFT;				// States which side of LEDs (left or right) should blink (1 for left, 0 for right) 

void TIMER2_Init(void);
void TIMER2_Start(void);			// Resets TC and PC and enables Timer2
void TIMER2_Stop(void);				// Disables Timer2

void TIMER3_Init(void);
void TIMER3_Start(void);			// Resets TC and PC and enables Timer3
void TIMER3_Stop(void);				// Disables Timer3

#endif
