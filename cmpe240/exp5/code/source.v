`timescale 1ns/1ns
module source(Overflow , Cout , F , X , Y , S);

output reg Overflow , Cout;
output reg [4:0] F;

input [4:0] X , Y;
input [1:0] S;

reg [4:0] nYmultBy4;

initial begin
	Overflow <= 1'b0;
	Cout <= 1'b0;
	F <= 5'b00000;
end

always @(X , Y , S) begin

	if( S == 2'b00 ) begin

		{Cout,F} <= X[3:1] * Y[2:0];
		Overflow <= 1'b0;

	end

	else if( S == 2'b01 ) begin
		
		Overflow <= 1'b0;
		F = 5'b00000;

		if( X > Y )
			Cout <= 1'b1;
		else
			Cout <= 1'b0;

	end
    
    else if( S == 2'b10 ) begin
		
		{Cout,F} <= X + Y;

		if( ~X[4] && ~Y[4] && F[4] )	// Overflow when operands are positive and result is negative
			Overflow <= 1'b1;
		
		else if( X[4] && Y[4] && ~F[4] )	// Overflow when operands are negative and result is positive
			Overflow <= 1'b1;
		
		else
			Overflow <= 1'b0;

	end


	else if( S == 2'b11 )begin
		
		nYmultBy4 = {~Y[2] , ~Y[1] , ~Y[0] , 1'b1 , 1'b1} + 1'b1;	// Multiply Y[2:0] by 4 in 2's Complement, take inverse and add 1 
		{Cout,F} = X + nYmultBy4;

		if( ~X[4] && ~nYmultBy4[4] && F[4] )	// Overflow when operands are positive and result is negative
			Overflow <= 1'b1;
		
		else if( X[4] && nYmultBy4[4] && ~F[4] )	// Overflow when operands are negative and result is positive
			Overflow <= 1'b1;
		
		else
			Overflow <= 1'b0;

	end
    
end


endmodule