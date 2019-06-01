#include "Joystick.h"

void JOYSTICK_Init() {
	IOCON_JOYSTICK_LEFT		&= ~7;
	IOCON_JOYSTICK_DOWN		&= ~7;
	IOCON_JOYSTICK_UP			&= ~7;
	IOCON_JOYSTICK_CENTER	&= ~7;
	IOCON_JOYSTICK_RIGHT	&= ~7;
	
	GPIO_DIR_Write(
									JOYSTICK_PORT,
									(JOYSTICK_LEFT_MASK | JOYSTICK_DOWN_MASK | JOYSTICK_UP_MASK | JOYSTICK_CENTER_MASK | JOYSTICK_RIGHT_MASK),
									INPUT
								);
}

uint8_t JOYSTICK_LEFT_Pressed(void) {
	return (GPIO_PIN_Read(JOYSTICK_PORT, JOYSTICK_LEFT_MASK) == 0);
}

uint8_t JOYSTICK_DOWN_Pressed(void) {
	return (GPIO_PIN_Read(JOYSTICK_PORT, JOYSTICK_DOWN_MASK) == 0);
}

uint8_t JOYSTICK_UP_Pressed(void) {
	return (GPIO_PIN_Read(JOYSTICK_PORT, JOYSTICK_UP_MASK) == 0);
}

uint8_t JOYSTICK_CENTER_Pressed(void) {
	return (GPIO_PIN_Read(JOYSTICK_PORT ,JOYSTICK_CENTER_MASK) == 0);
}

uint8_t JOYSTICK_RIGHT_Pressed(void) {
	return (GPIO_PIN_Read(JOYSTICK_PORT, JOYSTICK_RIGHT_MASK) == 0);
}
