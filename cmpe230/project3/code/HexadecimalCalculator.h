#ifndef HEXADECIMALCALCULATOR_H  
#define HEXADECIMALCALCULATOR_H	

#include <QLineEdit>
#include <QGridLayout>
#include <QButtonGroup>
#include <QPushButton> 

  
class HexadecimalCalculator : public QWidget{

  Q_OBJECT

  public:    
    HexadecimalCalculator(QWidget *parent=0);
    ~HexadecimalCalculator();


  private slots:    
    void plusClicked();     // Slot to be executed when '+' button of the calculator is clicked.

    void minusClicked();    // Slot to be executed when '-' button of the calculator is clicked.

    void equalClicked();    // Slot to be executed when '=' button of the calculator is clicked.
    
    void clearClicked();    // Slot to be executed when 'Clr' button of the calculator is clicked.
                            // This method is also executed before the first use of the calculator.

    void digitClicked(int digit);   // Slot to be executed when a digit button (0-9 A-F) of the calculator is clicked.
                                    // digit    : The decimal value of the digit button. For example, the decimal value of 'C' is 12.

  
  private:
    long sumSoFar;      // The result to be shown after '=' button is clicked. Stores decimal value.
    long operand;       // The operand for the binary operator. ('+' or '-'). Stores decimal value.
    bool lastOperator;  // If '+' is clicked lastly, true. Otherwise, false.
    bool isEqualLastOperation;  // If the last operation is to click '=' button, true. Otherwise, false.

    QFont font;     // The font of the text in QLineEdit and QPushButton(s).
    
    QGridLayout gridLayout;     // Main layout of the calculator. It is a 6x4 grid.
    
    QLineEdit display;      // The display of the calculator.
    
    QPushButton *plusButton;    // '+' button of the calculator.
    QPushButton *minusButton;   // '-' button of the calculator.
    QPushButton *equalButton;   // '=' button of the calculator.
    QPushButton *clearButton;   // 'Clr' button of the calculator.
    
    QButtonGroup buttonGroup;   // A QButtonGroup which includes only digit buttons (0-9 A-F) of the calculator.
    
    QPushButton *digitButtons[16];  // Every pointer represents a digit button (0-9 A-F) of the calculator.

    void updateSumAndResetOperand();    // Updates the variable sumSoFar and sets the variable operand to 0.
    void disablePlusMinusEqual();       // Disables '+', '-' and '=' buttons of the calculator.

};

#endif