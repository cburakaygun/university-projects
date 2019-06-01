#include "PWM.h"
#include "SystemStructures.h"

void PWM0_Init() {	
	PCONP |= 1<<5; // Powers up PWM0
	
	PWM0->PCR |= (1<<10 | 1<<9); // Gives HIGH to bit 10:9 to enable channels 2 and 1
	
	PWM0->TCR |= (1<<1); // Resets PWM0 TC and PC
	
	PWM0->MCR |= (1<<1); // Resets TC on MR0 match
	
	PWM0_SetPeriod(20);
	
	PWM0->TCR = (1<<3 | 1<<0); // Enables Counter and PWM0, Clears Reset
}

void PWM0_SetPeriod(uint32_t period_in_ms) {
	PWM0->MR0 = (PERIPHERAL_CLOCK_FREQUENCY / 1000000) * period_in_ms * 1000;
	PWM0->LER |= 1<<0; // Enables PWM0 MR0 Latch
}

void PWM0_MR_Write(uint8_t mr_number, uint32_t t_on) {
	if(t_on > 100) {
		t_on = 100;
	}
	
	t_on = (uint32_t)((PWM0->MR0 * t_on) / 100);
	
	if (t_on == PWM0->MR0) {
		t_on++;
	}
	
	switch(mr_number) {
		case 1:
			PWM0->MR1 = t_on;
			break;
		case 2:
			PWM0->MR2 = t_on;
			break;
		case 3:
			PWM0->MR3 = t_on;
			break;
		case 4:
			PWM0->MR4 = t_on;
			break;
		case 5:
			PWM0->MR5 = t_on;
			break;
		case 6:
			PWM0->MR6 = t_on;
			break;
	}
	
	PWM0->LER |= (1<<mr_number); // Enables PWM0 M<mr_number> Latch
}
