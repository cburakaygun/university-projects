## GRAPHICAL DESIGN OF THE CALCULATOR

We used a grid (6x4) for the main layout of the calculator.

The display (`QLineEdit`) is at the 1st row of the grid and it occupies 4 columns.

At the 2nd row of the grid, there are **+**, **-**, **=** and **Clr** buttons.


At the 3rd row of the grid, there are **0**, **1**, **2** and **3** buttons.

At the 4th row of the grid, there are **4**, **5**, **6** and **7** buttons.

At the 5th row of the grid, there are **8**, **9**, **A** and **B** buttons.

At the 6th row of the grid, there are **C**, **D**, **E** and **F** buttons.

```
|  D i s p l a y  |
| + | - | = | Clr |
| 0 | 1 | 2 |  3  |
| 4 | 5 | 6 |  7  |
| 8 | 9 | A |  B  |
| C | D | E |  F  |
```


## IMPLEMENTATION OF THE CALCULATOR

### SOME INFORMATION ABOUT THE CALCULATOR

There are only 2 BINARY operators in the calculator: **addition (+)** and **subtraction (-)**

Firstly, we made the display (`QLineEdit`) read-only so that the only way the user can give input to the calculator 
is through clicking the buttons of the calculator.
We made it this way to prevent the calculator from getting ivalid inputs like `(++52-4)`, `(13*5)`, `(5-K)`.

In addition to the read-only feature of the display, we put some restrictions on the buttons of the calculator
to prevent it from getting invalid input. These are as follows:

1. When the calculator is first executed, you will see that **+**, **-** and **=** buttons are disabled (unclickable)
because you need to give an operand (at least 1 digit) before a binary operator (**+** or **-**) or **=**.

2. When one of the binary operators (**+** and **-**) is clicked, **+**, **-** and **=** buttons becomes disabled
because you need to give an operand (at least 1 digit) after a binary operator.


**Clr** and digit (**0-9 A-F**) buttons are always enabled throughout the execution of the calculator.
Also, when one of the digit buttons (0-9 A-F) is clicked, the disabled buttons (**+**, **-** and **=**) becomes enabled.


**Clr** button sets the calculator to initial state.


The user can use the result of a calculation as the left operand of another expression by clicking a binary operator (**+** or **-**)
button after clicking **=** button and getting the result.


If a digit (0-9 A-F) button is clicked after clicking **=** button and getting the result,
the calculator goes to the initial state with that clicked digit button as the first input of the new state.


### HOW THE CALCULATOR WORKS?

The user can write as many operands and operators (**+** or **-**) as he/she wants on the display before clicking **=** button.
When the user writes an expression, after every click of a digit (0-9 A-F) button, the decimal value of the newly created
operand on the display is stored in the variable `operand` (`operand = operand*16 + <decimalValueOfDigit>`).

When the user clicks one of the binary operators (**+** or **-**), the value of the variable `operand` is added to or subtracted from
the variable `sumSoFar` according to the operator given before the last clicked operator button (At the beginning, the calculator
assumes that the given operator is **+** and the left operand is **0**.) and the value of `operand` becomes **0**. It goes like that until
the user clicks **=** button.

When **=** button is clicked the value of the variable `operand` is added to or subtracted from the variable `sumSoFar` 
according to the operator given before **=** (At the beginning, the calculator assumes that the given operator is **+** and 
the left operand is **0**.). Then, the value of `sumSoFar` is converted to hexadecimal and printed on the display. Also, the value
of `operand` becomes **0**.
