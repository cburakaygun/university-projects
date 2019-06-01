#include "Wait.h"

void waitMilliseconds(uint32_t miliseconds) {
	waitMicroseconds(miliseconds*1000);
}

void waitMicroseconds(uint32_t microseconds) {
	uint32_t i;
	uint32_t totalDuration = microseconds*24;
	for(i=0; i<totalDuration; i++);
}
