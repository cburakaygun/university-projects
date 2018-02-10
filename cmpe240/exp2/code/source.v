`timescale 1ns/1ns
module source(y, x);


input wire [4:0] x;
output wire [0:0] y;

// Fill here with gate level verilog code
wire not_x0 , and_x0x1 , or_x0x1 , not_or_x0x1;

and( and_x0x1 , x[0] , x[1] );
or( or_x0x1 , x[0] , x[1] );
not( not_x0 , x[0] );
not( not_or_x0x1 , or_x0x1 );

mux8x1 m( x[4] , x[3] , x[2] , x[1] , x[0] , not_x0 , not_x0 , and_x0x1 , or_x0x1 , not_or_x0x1 , and_x0x1 , y );


endmodule