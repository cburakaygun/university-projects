#ifndef EXTERNAL_INTERRUPT_H
#define EXTERNAL_INTERRUPT_H

#include "LPC407x_8x_177x_8x.h"

typedef struct
{
	volatile	uint32_t EXTINT;
				uint32_t RESERVED0[1];
	volatile	uint32_t EXTMODE;
	volatile	uint32_t EXTPOLAR;
} EXT_TypeDef;

#define IOCON_PUSH_BUTTON *((volatile uint32_t*) 0x4002C128) // P2_10 (P23)

#define EXT	((EXT_TypeDef *) 0x400FC140) // External Interrupt Flag Register (EXTINT)

void EXTERNAL_Init(void);

#endif
