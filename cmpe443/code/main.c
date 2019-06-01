#include "Library/ADC.h"
#include "Library/Car.h"
#include "Library/Joystick.h"
#include "Library/Parameters.h"
#include "Library/Ultrasonic.h"
#include "Library/Wait.h"

uint32_t leftLDR = 0;
uint32_t rightLDR = 0;

uint32_t leftLDRThreshold = 40;
uint32_t rightLDRThreshold = 50;

uint8_t escapingFromLight = 0;		// Flag to state if the car is currently escaping from the light or not

uint32_t currentTrimpotValue = 0;

void init() {
	JOYSTICK_Init();
	CAR_Init();
}

void update() {
	currentTrimpotValue = TRIMPOT_Read();
	
	if (FORWARD_FLAG == 1) {
		CAR_SetSpeed(currentTrimpotValue, currentTrimpotValue);
		
		if (SHOULD_ESCAPE_OBSTACLE == 1) {
			CAR_Backward(); // Stops ultrasonic
			ULTRASONIC_Start();
		}
		else {
			leftLDR = LDR_LEFT_Read();
			rightLDR = LDR_RIGHT_Read();
			
			if ((leftLDR > leftLDRThreshold) || (rightLDRThreshold-30 < rightLDR && rightLDR < rightLDRThreshold-20)) {
				CAR_SetSpeed(currentTrimpotValue, currentTrimpotValue/2);
				CAR_RotateRight();
				escapingFromLight = 1;
			}
			else if ((rightLDR > rightLDRThreshold) || (leftLDRThreshold-30 < leftLDR && leftLDR < leftLDRThreshold-20)) {
				CAR_SetSpeed(currentTrimpotValue/2, currentTrimpotValue);
				CAR_RotateLeft();
				escapingFromLight = 1;
			}
		}
	}
	else if (BACKWARD_FLAG == 1) {
		CAR_SetSpeed(currentTrimpotValue, currentTrimpotValue);
		
		if (ESCAPED_OBSTACLE == 1) {
			ESCAPED_OBSTACLE = 0;
			CAR_Forward();
		}
	}
	else if (TURN_LEFT_FLAG == 1) {
		if (escapingFromLight == 1) {
			rightLDR = LDR_RIGHT_Read();
			
			if (rightLDR <= rightLDRThreshold){
				escapingFromLight = 0;
				CAR_Forward();
			}
		}
		else {
			CAR_SetSpeed(currentTrimpotValue, currentTrimpotValue);
		}
	}
	else if (TURN_RIGHT_FLAG == 1) {
		if (escapingFromLight == 1) {
			leftLDR = LDR_LEFT_Read();
			
			if (leftLDR <= leftLDRThreshold){
				escapingFromLight = 0;
				CAR_Forward();
			}
		}
		else {
			CAR_SetSpeed(currentTrimpotValue, currentTrimpotValue);
		}
	}	
	
	if (START_MODE == MODE_MANUAL) {
		if (JOYSTICK_LEFT_Pressed()) {
			CAR_RotateLeft();
		}
		else if (JOYSTICK_RIGHT_Pressed()) {
			CAR_RotateRight();
		}
		else if (JOYSTICK_UP_Pressed()) {
			CAR_Forward();
		}
		else if (JOYSTICK_DOWN_Pressed()) {
			CAR_Backward();
		}
		else if (JOYSTICK_CENTER_Pressed()) {
			CAR_Stop();
		}
	}
	else { // MODE_AUTO
		if (JOYSTICK_UP_Pressed()) {
			CAR_Forward();
		}
	}
}

int main() {
	__enable_irq();
	init();
	
	while(1) {	
		update();
	}
}
