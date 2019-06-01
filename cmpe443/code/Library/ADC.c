#include "ADC.h"

void ADC_Init() {
	// Changes the function value of TRIMPOT to ADC (FUNC (2:0) -> 001)
	uint32_t value = IOCON_TRIMPOT;
	value &= ~(3<<1);
	value |= 1<<0;
	IOCON_TRIMPOT = value;
	
	// Changes the function value of LDR_LEFT to ADC (FUNC (2:0) -> 001)
	value = IOCON_LDR_LEFT;
	value &= ~(3<<1);
	value |= 1<<0;
	IOCON_LDR_LEFT = value;
	
	// Changes the function value of LDR_RIGHT to ADC (FUNC (2:0) -> 001)
	value = IOCON_LDR_RIGHT;
	value &= ~(3<<1);
	value |= 1<<0;
	IOCON_LDR_RIGHT = value;
	
	IOCON_TRIMPOT &= ~(3<<3);   // MODE(4:3) -> 00
	IOCON_LDR_LEFT &= ~(3<<3);  // MODE(4:3) -> 00
	IOCON_LDR_RIGHT &= ~(3<<3); // MODE(4:3) -> 00
	
	IOCON_TRIMPOT &= ~(1<<7);   // ADMODE (7) -> 0 (analog)
	IOCON_LDR_LEFT &= ~(1<<7);  // ADMODE (7) -> 0 (analog)
	IOCON_LDR_RIGHT &= ~(1<<7); // ADMODE (7) -> 0 (analog)
	
	PCONP |= 1<<12; // Turns on ADC
	
	// Sets the CLKDIV (15:8) pins and makes the A/D converter operational without Burst mode
	value = ADC->CR;
	value &= ~0xFFFF; // Clears CLKDIV (15:8) and SEL (7:0) bits
	value |= (ADC_CLKDIV << 8); // Sets CLKDIV (15:8)
	value &= ~(1<<16); // Operation without Burst Mode
	value |= 1<<21; // Sets PDN bit (power down mode) to make ADC operational
	value &= ~(7<<24); // START (26:24) -> 000; no start
	ADC->CR = value;
}

// Starts the A/D conversion now START (26:24) -> 001
void ADC_Start() {
	uint32_t value = ADC->CR;
	value &= ~(7<<24);
	value |= 1<<24;
	ADC->CR = value;
}

// Stops the A/D conversion
void ADC_Stop() {
	ADC->CR &= ~(7<<24); // START (26:24) -> 000; no start
}

// Converts the analog value present on channel 'channelNumber'
// and scales it to 0-100 range
uint32_t ADC_Read(uint8_t channelNumber) {
	uint32_t data;
	
	// Configures SEL (7:0) bits for sampling and converting for ADC0[channelNumber]
	uint32_t value = ADC->CR;
	value &= ~0xFF;
	value |= 1<<channelNumber;
	ADC->CR = value;
	
	ADC_Start();
	while(!(ADC->DR[channelNumber] & (1u<<31))); // Waits until A/D conversion is completed
	data = ADC->DR[channelNumber] & 0xFFF0; 	// Extracts RESULT (15:4) bits
	ADC_Stop();
	
	data = data >> 4;
	return (data*100) / ADC_MAX_VALUE; // Converts the data RESULT (15:4) to 0 - 100 range
}

uint32_t TRIMPOT_Read() {
	return ADC_Read(TRIMPOT_CHANNEL_NUMBER);
}

uint32_t LDR_LEFT_Read() {
	return ADC_Read(LDR_LEFT_CHANNEL_NUMBER);
}

uint32_t LDR_RIGHT_Read() {
	return ADC_Read(LDR_RIGHT_CHANNEL_NUMBER);
}
