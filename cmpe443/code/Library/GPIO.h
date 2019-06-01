#ifndef GPIO_H
#define GPIO_H

#include "LPC407x_8x_177x_8x.h"

typedef struct {
  volatile	uint32_t DIR;
						uint32_t RESERVED0[3];
  volatile	uint32_t MASK;
  volatile	uint32_t PIN;
  volatile	uint32_t SET;
  volatile  uint32_t CLR;
} GPIO_TypeDef;

typedef enum {
	INPUT = 0,
	OUTPUT = 1
} GPIO_Direction;

typedef enum {
	LOW = 0,
	HIGH = 1
} GPIO_Value;

#define PORT0	((GPIO_TypeDef*) 0x20098000)
#define PORT1	((GPIO_TypeDef*) 0x20098020)
//#define PORT2	((GPIO_TypeDef*) 0x20098040)	// unused
//#define PORT3	((GPIO_TypeDef*) 0x20098060)	// unused
//#define PORT4	((GPIO_TypeDef*) 0x20098080)	// unused
#define PORT5	((GPIO_TypeDef*) 0x200980A0)

void GPIO_DIR_Write(GPIO_TypeDef* PORT, uint32_t MASK, uint8_t value);
void GPIO_PIN_Write(GPIO_TypeDef* PORT, uint32_t MASK,  uint8_t value);
uint32_t GPIO_PIN_Read(GPIO_TypeDef* PORT, uint32_t MASK);

#endif
