#ifndef SPEED_SENSOR_H
#define SPEED_SENSOR_H

#include "LPC407x_8x_177x_8x.h"

#define IOCON_SPEEDSENSOR	*((volatile uint32_t *) 0x4002C010)	// P0_4 (P34) Used for input capture from speed sensor, T2_CAP0

void SPEEDSENSOR_Init(void);
void SPEEDSENSOR_Start(void);
void SPEEDSENSOR_Stop(void);

#endif
