#ifndef SERIAL_H
#define SERIAL_H

#include "LPC407x_8x_177x_8x.h"
#include "SystemStructures.h"

#pragma anon_unions

typedef struct
{
	union {
		volatile	uint8_t  RBR;
		volatile 	uint8_t  THR;
		volatile	uint8_t  DLL;
					uint32_t RESERVED0;
	};
	
	union {
		volatile	uint8_t  DLM;
		volatile	uint32_t IER;
	};
	
	union {
		volatile  uint32_t IIR;
		volatile  uint8_t  FCR;
	};
	
	volatile	uint8_t  LCR;
				uint8_t  RESERVED1[7];
	volatile	uint8_t  LSR;
				uint8_t  RESERVED2[7];
	volatile	uint8_t  SSCR;
				uint8_t  RESERVED3[3];
	volatile	uint32_t ACR;
	volatile	uint8_t  ICR;
				uint8_t  RESERVED4[3];
	volatile	uint8_t  FDR;
				uint8_t  RESERVED5[7];
	volatile	uint8_t  TER;
				uint8_t  RESERVED8[27];
	volatile	uint8_t  RS485CTRL;
				uint8_t  RESERVED9[3];
	volatile	uint8_t  ADRMATCH;
				uint8_t  RESERVED10[3];
	volatile	uint8_t  RS485DLY;
				uint8_t  RESERVED11[3];
	volatile	uint8_t  FIFOLVL;
} UART_TypeDef;

#define UART0 ((UART_TypeDef *) 0x4000C000)

#define IOCON_SERIAL_TX	*((volatile uint32_t *) 0x4002C008) // P0_2 (P42)
#define IOCON_SERIAL_RX	*((volatile uint32_t *) 0x4002C00C) // P0_3 (P41)

//#define IOCON_SERIAL_TX	*((volatile uint32_t*) 0x4002C000) // P0_0 (P9)
//#define IOCON_SERIAL_RX	*((volatile uint32_t*) 0x4002C004) // P0_1 (P10)

void SERIAL_Init(void);
char SERIAL_ReadData(void);
void SERIAL_WriteData(char data);
void SERIAL_Write(char* data);

#endif
