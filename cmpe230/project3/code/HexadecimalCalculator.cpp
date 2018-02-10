#include "HexadecimalCalculator.h"

#define PLUS true
#define MINUS false

using namespace std;

HexadecimalCalculator::HexadecimalCalculator(QWidget *parent) : QWidget(parent){

	// Setting font to "24 pt Verdana Bold"
    font.setFamily(QString::fromUtf8("Verdana"));
    font.setPointSize(24);
    font.setBold(true);


    // Setting the display of the calculator
	display.setReadOnly(true);	// Makes display read-only to allow input only from the buttons of the calculator.
	display.setAlignment(Qt::AlignRight);	// Aligns the text in the display to the right.
	display.setFont(font);		// Sets the font of the text in the display.
	display.setCursor(Qt::IBeamCursor);		// Sets the cursor to look like I when the mouse is over the display.
	display.setStyleSheet("QLineEdit{background: white;}");	// Sets the background color of the display to white.

	gridLayout.addWidget(&display , 0 , 0 , 1 , 4);		// Adds the display to the top of the main layout.
														// It occupies 1 row and 4 columns in the 6x4 grid.


	// Setting '+' button of the calculator
	plusButton = new QPushButton("+");
	plusButton -> setFont(font);
	
	// Setting '-' button of the calculator
	minusButton = new QPushButton("-");
	minusButton -> setFont(font);
	
	// Setting '=' button of the calculator
	equalButton = new QPushButton("=");
	equalButton -> setFont(font);
	
	// Setting 'Clr' button of the calculator
	clearButton = new QPushButton("Clr");
	clearButton -> setFont(font);

	gridLayout.addWidget(plusButton , 1 , 0);	// Adds '+' button to the 2nd row and 1st column of the 6x4 grid.
	gridLayout.addWidget(minusButton , 1 , 1);	// Adds '-' button to the 2nd row and 2nd column of the 6x4 grid.
	gridLayout.addWidget(equalButton , 1 , 2);	// Adds '=' button to the 2nd row and 3rd column of the 6x4 grid.
	gridLayout.addWidget(clearButton , 1 , 3);	// Adds 'Clr' button to the 2nd row and 4th column of the 6x4 grid.

	
	QString *str = new QString();	// This is used for giving text to the digit buttons (0-9 A-F).

	// The loop below creates digit buttons (0-9 A-F) of the calculator and
	// adds them to the main grid.
	// Also, in buttonGroup, associates every digit button with its decimal value.
	// For example, associates the button whose text is 'C' with 12.
	for(int i = 0 ; i < 16 ; ++i){
		*str = QString::number(i,16).toUpper();		// Text of the button.
		digitButtons[i] = new QPushButton(*str);
		(*digitButtons[i]).setFont(font);
		gridLayout.addWidget(digitButtons[i] , i/4+2 , i%4);	// Adds the button to the proper location in the 6x4 grid.
		buttonGroup.addButton(digitButtons[i] , i);	// Associates the button with its decimal value.
	}

	delete str;

	// Connections of the button signals to the slots.
	QObject::connect(&buttonGroup , SIGNAL(buttonClicked(int)) , this , SLOT(digitClicked(int)));
	QObject::connect(plusButton , SIGNAL(clicked()) , this , SLOT(plusClicked()));
	QObject::connect(minusButton , SIGNAL(clicked()) , this , SLOT(minusClicked()));
	QObject::connect(equalButton , SIGNAL(clicked()) , this , SLOT(equalClicked()));
	QObject::connect(clearButton , SIGNAL(clicked()) , this , SLOT(clearClicked()));

	setWindowTitle("HEXADECIMAL CALCULATOR");	// Title of the calculator window.
	clearClicked();		// Prepares the calculator for the first use.
	setLayout(&gridLayout);
	setFixedHeight(sizeHint().height());	// Fixes the height of the calculator window.

}



/*
	Destructor
*/
HexadecimalCalculator::~HexadecimalCalculator(){

	delete plusButton;
	delete minusButton;
	delete equalButton;
	delete clearButton;

	for(int i=0 ; i<16 ; ++i)
		delete digitButtons[i];

}



/*
	Slot to be executed when '+' button of the calculator is clicked.
*/
void HexadecimalCalculator::plusClicked(){
	
	disablePlusMinusEqual();	// Disables '+' , '-' and '=' buttons.
	updateSumAndResetOperand();	// Updates sumSoFar and operand variables.

	display.insert("+");	// Inserts the text '+' to the right of the text in the display.
	lastOperator = PLUS;		
	isEqualLastOperation = false;

}



/*
	Slot to be executed when '-' button of the calculator is clicked.
*/
void HexadecimalCalculator::minusClicked(){

	disablePlusMinusEqual();	// Disables '+' , '-' and '=' buttons.
	updateSumAndResetOperand();	// Updates sumSoFar and operand variables.

	display.insert("-");	// Inserts the text '-' to the right of the text in the display.
	lastOperator = MINUS;
	isEqualLastOperation = false;

}



/*
	Slot to be executed when '=' button of the calculator is clicked.
*/
void HexadecimalCalculator::equalClicked(){

	isEqualLastOperation = true;	
	updateSumAndResetOperand();	// Updates sumSoFar and operand variables.

	// Clears the text in the display and prints the result of the calculation.
	if(sumSoFar < 0)
		display.setText( "-" + QString::number(-1*sumSoFar,16).toUpper() );
	else	
		display.setText( QString::number(sumSoFar,16).toUpper() );

}



/*
	Slot to be executed when 'Clr' button of the calculator is clicked.
	This method is also executed before the first use of the calculator.
*/
void HexadecimalCalculator::clearClicked(){

	disablePlusMinusEqual();	// Disables '+' , '-' and '=' buttons.
	
	sumSoFar = 0;
	operand = 0;
	lastOperator = PLUS;	// It is safe to think that the calculation starts with addition (to 0).
	isEqualLastOperation = false;
	display.clear();	// Clears the text in the display.

}



/*
	Slot to be executed when a digit button (0-9 A-F) of the calculator is clicked.

	digit 	: The decimal value of the digit button. For example, the decimal value of 'C' (button) is 12.
*/
void HexadecimalCalculator::digitClicked(int digit){

	if(isEqualLastOperation)
		clearClicked();

	operand *= 16;		// Multiplies the value of operand calculated so far and
	operand += digit;	// and adds the decimal value of last written digit to it.
	display.insert( QString::number(digit,16).toUpper() );	// Adds the digit (in hex) to the right of the text in the display.

	plusButton -> setEnabled(true);		// Enables '+' button
	minusButton -> setEnabled(true);	// Enables '-' button
	equalButton -> setEnabled(true);	// Enables '=' button

}



/*
	Updates the variable sumSoFar and sets the variable operand to 0.
*/
void HexadecimalCalculator::updateSumAndResetOperand(){

	if(lastOperator)			// If lastOperator is true, it means addition.
		sumSoFar += operand;
	else 						// If lastOperator is false, it means subtraction.
		sumSoFar -= operand;

	operand = 0;

}



/*
	Disables '+', '-' and '=' buttons of the calculator.
*/
void HexadecimalCalculator::disablePlusMinusEqual(){

	plusButton -> setEnabled(false);
	minusButton -> setEnabled(false);
	equalButton -> setEnabled(false);

}