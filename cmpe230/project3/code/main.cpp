#include "HexadecimalCalculator.h"
#include <QApplication>

int main(int argc , char *argv[]){

	QApplication app(argc , argv);

	HexadecimalCalculator hexCalc;
	hexCalc.show();

	return app.exec();

}