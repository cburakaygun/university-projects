#ifndef PARAMETERS_H
#define PARAMETERS_H

#include "LPC407x_8x_177x_8x.h"

#define LED_BLINK_T_ON ((uint32_t) 250) // ms, blinks 2 times in a second

#define OBSTACLE_DISTANCE ((uint32_t) 15) // cm
#define OBSTACLE_ESCAPE_DISTANCE ((uint32_t) 30) // cm

#define MODE_MANUAL '*'
#define MODE_AUTO	'#'

// Those are initialized in Car.c
extern char START_MODE; // Current mode of the car

extern uint8_t SHOULD_ESCAPE_OBSTACLE;	// Flag to indicate if the car should escape an obstacle
extern uint8_t ESCAPED_OBSTACLE;		// Flag to indicate if the car has escaped the obstacle

extern uint8_t TURN_LEFT_FLAG;
extern uint8_t TURN_RIGHT_FLAG;
extern uint8_t FORWARD_FLAG;
extern uint8_t BACKWARD_FLAG;

#endif
