#include "Car.h"
#include "Parameters.h"
#include "UART.h"

uint8_t first6Read = 0;

void UART0_Init() {
	// Changes the functions of TX and RX pins for UART (FUNC (2:0) -> 001)	
	uint32_t value = IOCON_SERIAL_TX;
	value &= ~7;
	value |= 1;
	IOCON_SERIAL_TX = value;
	
	value = IOCON_SERIAL_RX;
	value &= ~7;
	value |= 1;
	IOCON_SERIAL_RX = value;
	
	PCONP |= 1<<3; // Turns on UART0
	
	UART0->FCR |= 1; // Enables FIFO for UART0
	
	UART0->LCR |= 1<<7; // Enables the access to Divisor Latches
	
	// Writes DLM, DLL and FDR values for 9600 baudrate
	UART0->DLM = 0x01;
	UART0->DLL = 0x25;
	UART0->FDR = (0x01<<0) | (0x03<<4);

	UART0->LCR &= ~(1<<7); // Disables the access to Divisor Latches
	
	// Changes LCR register value for 8-bit character transfer, 1 stop bits and Even Parity
	value = UART0->LCR;
	value &= ~0x3F; // Makes bits (5:0) all zero
	value |= 3; // 8-bit data
	value |= 1<<3; // Enables parity
	value |= 1<<4; // PS (5:4) -> 01; even parity
	UART0->LCR = value;
	
	UART0->IER |= 1; // Enables the Receive Data Available interrupt
	
	NVIC_ClearPendingIRQ(UART0_IRQn);
	NVIC_EnableIRQ(UART0_IRQn);
}

void UART0_WriteData(char data) {
	while (!(UART0->LSR & 1<<5)); // Waits until Transmit Holding Register become empty
	UART0->THR = data; // Writes data to Transmit Holding Register
}

void UART0_Write(char* data) {
	while(*data > 0)  {
		UART0_WriteData(*data++);
	}
}

char UART0_ReadData() {
	char data;
	
	while (!(UART0->LSR & 1<<0)); // Waits until Receiver Data Ready
	data = UART0->RBR; // Reads data from Receiver Buffer Register
	
	return data;
}

void UART0_IRQHandler(void) {
	uint8_t interruptSource = UART0->IIR & (7<<1);
	interruptSource = interruptSource >> 1;
	if (interruptSource == 2) { // Receive Data Available
		char data = UART0->RBR;
		
		UART0_WriteData(data);
		UART0_Write("\r\n");
		
		if (START_MODE == MODE_AUTO) {
			if (data == '6') {
				if (first6Read) {
					CAR_Forward();
					first6Read = 0;
				}
				else {
					first6Read = 1;
					return;
				}
			}
			else {
				first6Read = 0;
			}
		}
		
		CAR_ChangeModeTo(data);
	}
}
