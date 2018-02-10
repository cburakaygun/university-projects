`timescale 1ns/1ns
module source(y,x,rst,clk);

output [1:0] y;
input x , rst , clk;

wire [2:0] s;	// Output of DFlipFlop
wire [2:0] n;	// Input of DFlipFlop

// Gates for y[1]
wire nS2_S1_S0 , S2_nS1_nS0;
and(nS2_S1_S0, ~s[2], s[1], s[0]);
and(S2_nS1_nS0, s[2], ~s[1], ~s[0]);
or(y[1], nS2_S1_S0, S2_nS1_nS0);


// Gates for y[0]
and(y[0], s[1], s[0]);


// Gates for n[2]
wire nX_S1_nS0 , S2_nS1_S0 , S2_S1_nS0;
and(nX_S1_nS0 , ~x , s[1] , ~s[0]);
and(S2_nS1_S0 , s[2] , ~s[1] , s[0]);
and(S2_S1_nS0 , s[2] , s[1] , ~s[0]);
or(n[2] , nX_S1_nS0 , S2_nS1_S0 , S2_S1_nS0);


// Gates for n[1]
wire nX_S0 , nX_S2 , X_S1_nS0;
and(nX_S0 , ~x , s[0]);
and(nX_S2 , ~x , s[2]);
and(X_S1_nS0 , x , s[1] , ~s[0]);
or(n[1] , nX_S0 , nX_S2, X_S1_nS0);


// Gates for n[0]
wire X_nS2 , X_nS0 , X_S1 , nS2_S1_nS0;
and(X_nS2, x , ~s[2]);
and(X_nS0 , x ,~s[0]);
and(X_S1 , x , s[1]);
and(nS2_S1_nS0 , ~s[2] , s[1] , ~s[0]);
or(n[0] ,X_nS2, X_nS0, X_S1, nS2_S1_nS0);


DFlipFlop3 dff(s , n , rst , clk);

endmodule