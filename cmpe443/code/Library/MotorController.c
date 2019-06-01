#include "LPC407x_8x_177x_8x.h"
#include "MotorController.h"
#include "Parameters.h"
#include "PWM.h"

// Sets the FUNC bits of IOCON_REG to 011
void IOCON_Set_Function_To_011(volatile uint32_t * IOCON_REG) {
	uint32_t value = *IOCON_REG;
	value &= ~(1<<2);
	value |= (1<<1 | 1<<0);
	*IOCON_REG = value;
}

void MOTORCONTROLLER_Init() {
	IOCON_Set_Function_To_011(&IOCON_MOTOR_ENA);	// PWM functionality
	IOCON_Set_Function_To_011(&IOCON_MOTOR_ENB);	// PWM functionality
	PWM0_Init();
	
	PWM0_MR_Write(MOTOR_ENA_PWM0_MR, 0);	// Speed should be set using CAR_SetSpeed method
	PWM0_MR_Write(MOTOR_ENB_PWM0_MR, 0);	// Speed should be set using CAR_SetSpeed method
	
	IOCON_MOTOR_IN1 &= ~7; // GPIO functionality
	IOCON_MOTOR_IN2 &= ~7; // GPIO functionality
	IOCON_MOTOR_IN3 &= ~7; // GPIO functionality
	IOCON_MOTOR_IN4 &= ~7; // GPIO functionality
	
	GPIO_DIR_Write(MOTOR_DIRECTION_PORT, MOTOR_DIRECTION_MASK, OUTPUT);
	
	MOTOR_Stop();
}

void MOTOR_LEFT_SetSpeed(uint32_t speed) {
	PWM0_MR_Write(MOTOR_ENA_PWM0_MR, speed);
}

void MOTOR_RIGHT_SetSpeed(uint32_t speed) {
	PWM0_MR_Write(MOTOR_ENB_PWM0_MR, speed);
}

void MOTOR_LEFT_Forward() {
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN1_MASK, LOW);
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN2_MASK, HIGH);
}

void MOTOR_RIGHT_Forward() {
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN3_MASK, LOW);
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN4_MASK, HIGH);
}

void MOTOR_LEFT_Backward() {
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN1_MASK, HIGH);
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN2_MASK, LOW);
}

void MOTOR_RIGHT_Backward() {
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN3_MASK, HIGH);
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_IN4_MASK, LOW);
}

void MOTOR_Stop() {
	GPIO_PIN_Write(MOTOR_DIRECTION_PORT, MOTOR_DIRECTION_MASK, LOW);
}
