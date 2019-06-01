#include "ADC.h"
#include "Car.h"
#include "ExternalInterrupt.h"
#include "LED.h"
#include "MotorController.h"
#include "Parameters.h"
#include "UART.h"
#include "Ultrasonic.h"

char START_MODE = MODE_MANUAL;

uint8_t SHOULD_ESCAPE_OBSTACLE = 0;	// Flag to indicate if the car should escape an obstacle
uint8_t ESCAPED_OBSTACLE = 0;		// Flag to indicate if the car has escaped the obstacle

uint8_t	TURN_LEFT_FLAG 	= 0;
uint8_t	TURN_RIGHT_FLAG	= 0;
uint8_t	FORWARD_FLAG 	= 0;
uint8_t	BACKWARD_FLAG 	= 0;

void FLAG_TURN_LEFT_Set() {
	TURN_LEFT_FLAG = 1;
	TURN_RIGHT_FLAG = 0;
	FORWARD_FLAG = 0;
	BACKWARD_FLAG = 0;
}

void FLAG_TURN_RIGHT_Set() {
	TURN_LEFT_FLAG = 0;
	TURN_RIGHT_FLAG = 1;
	FORWARD_FLAG = 0;
	BACKWARD_FLAG = 0;
}

void FLAG_FORWARD_Set() {
	TURN_LEFT_FLAG = 0;
	TURN_RIGHT_FLAG = 0;
	FORWARD_FLAG = 1;
	BACKWARD_FLAG = 0;
}

void FLAG_BACKWARD_Set() {
	TURN_LEFT_FLAG = 0;
	TURN_RIGHT_FLAG = 0;
	FORWARD_FLAG = 0;
	BACKWARD_FLAG = 1;
}

void FLAG_Clear() {
	TURN_LEFT_FLAG = 0;
	TURN_RIGHT_FLAG = 0;
	FORWARD_FLAG = 0;
	BACKWARD_FLAG = 0;
}

void CAR_Init() {
	ULTRASONIC_Init();
	LED_Init();
	MOTORCONTROLLER_Init();
	EXTERNAL_Init();
	UART0_Init();
	ADC_Init();
	
	FLAG_Clear();
}

void CAR_Forward() {
	FLAG_FORWARD_Set();
	ULTRASONIC_Start();
	LED_FRONT_On();
	MOTOR_LEFT_Forward();
	MOTOR_RIGHT_Forward();
}

void CAR_Backward() {
	FLAG_BACKWARD_Set();
	ULTRASONIC_Stop();
	LED_BACK_On();
	MOTOR_LEFT_Backward();
	MOTOR_RIGHT_Backward();
}

void CAR_RotateLeft() {
	FLAG_TURN_LEFT_Set();
	ULTRASONIC_Stop();
	LED_LEFT_Blink();
	MOTOR_LEFT_Backward();
	MOTOR_RIGHT_Forward();
}

void CAR_RotateRight() {
	FLAG_TURN_RIGHT_Set();
	ULTRASONIC_Stop();
	LED_RIGHT_Blink();
	MOTOR_LEFT_Forward();
	MOTOR_RIGHT_Backward();
}

void CAR_Stop() {
	FLAG_Clear();
	ULTRASONIC_Stop();
	MOTOR_Stop();
	LED_Off();
}

void CAR_SetSpeed(uint32_t leftMotorSpeed, uint32_t rightMotorSpeed) {
	MOTOR_LEFT_SetSpeed(leftMotorSpeed);
	MOTOR_RIGHT_SetSpeed(rightMotorSpeed);
}

void CAR_ChangeModeTo(char newMode) {	
	if (START_MODE == MODE_AUTO && newMode == MODE_MANUAL) {
		CAR_Stop();
		START_MODE = MODE_MANUAL;
		UART0_Write("MANUAL\r\n");
	}
	else if (START_MODE == MODE_MANUAL && newMode == MODE_AUTO) {
		CAR_Stop();
		START_MODE = MODE_AUTO;
		UART0_Write("AUTO\r\n");
	}
}
