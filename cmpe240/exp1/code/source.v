module source(y, x);

input wire [2:0] x;
output wire [0:0] y;

// Fill here with gate level verilog code
wire w, nx2;

not( nx2 , x[2] );
and( w , nx2 , x[1] );
or( y , w , x[0] );

endmodule
