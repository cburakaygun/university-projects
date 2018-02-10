`timescale 1ns/1ns
module testbench();

reg [4:0] X , Y;
reg [1:0] S;

wire Overflow , Cout;
wire [4:0] F;

source s( .Overflow(Overflow) , .Cout(Cout) , .F(F) , .X(X) , .Y(Y)  , .S(S) );

initial begin

    $dumpfile("TimingDiagram.vcd");
    $dumpvars(0, Overflow , Cout , F , X , Y , S);

    X = 5'b00011;
    Y = 5'b01010;

    S = 2'b00;
    #10
    S = 2'b01;
    #10
    S = 2'b10;
    #10
    S = 2'b11;
    #10
    
    X = 5'b10000;
    Y = 5'b00010;

    S = 2'b00;
    #10
    S = 2'b01;
    #10
    S = 2'b10;
    #10
    S = 2'b11;
    #10
    
    X = 5'b01011;
    Y = 5'b00100;

    S = 2'b00;
    #10
    S = 2'b01;
    #10
    S = 2'b10;
    #10
    S = 2'b11;
    #10

    X = 5'b11111;
    Y = 5'b00000;

    #10

    $finish;

end    

endmodule