#ifndef ADC_H
#define ADC_H

#include "LPC407x_8x_177x_8x.h"
#include "SystemStructures.h"

typedef struct {
  volatile	uint32_t CR;
  volatile	uint32_t GDR;
			uint32_t RESERVED0;
  volatile	uint32_t INTEN;
  volatile	uint32_t DR[8];
  volatile	uint32_t STAT;
  volatile	uint32_t TRM;
} ADC_TypeDef;


#define ADC_MAX_VALUE 0xFFF
#define ADC_CLOCK_FREQUENCY 1000000 // Hertz
#define ADC_CLKDIV (PERIPHERAL_CLOCK_FREQUENCY/ADC_CLOCK_FREQUENCY - 1)

#define ADC	((ADC_TypeDef*) 0x40034000)

#define IOCON_TRIMPOT	*((volatile uint32_t*) 0x4002C05C) // P0_23 (P15) ADC0[0]
#define IOCON_LDR_LEFT	*((volatile uint32_t*) 0x4002C064) // P0_25 (P17) ADC0[2]
#define IOCON_LDR_RIGHT	*((volatile uint32_t*) 0x4002C068) // P0_26 (P18) ADC0[3]

#define TRIMPOT_CHANNEL_NUMBER		((uint8_t) 0)
#define LDR_LEFT_CHANNEL_NUMBER		((uint8_t) 2)
#define LDR_RIGHT_CHANNEL_NUMBER	((uint8_t) 3)

void ADC_Init(void);
uint32_t TRIMPOT_Read(void);	// Returns Trimpot pin value in 0-100. Assumes: Trimpot min. -> 0 & Trimpot max. -> 100
uint32_t LDR_LEFT_Read(void);	// Returns left LDR pin value in 0-100. Assumes: Min. Light ->0 & Max. Light -> 100
uint32_t LDR_RIGHT_Read(void);	// Returns right LDR pin value in 0-100. Assumes: Min. Light ->0 & Max. Light -> 100

#endif
