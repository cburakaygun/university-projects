`timescale 1ns/1ns
module testbench();
   
   reg x;
   reg rst;
   reg clk;
   wire [1:0] y;

   source s(.y(y),.x(x),.rst(rst),.clk(clk));

   always begin
      
      #10
	   clk = ~clk;
   
   end


   initial begin
   
      $dumpfile("TimingDiagram.vcd");
      $dumpvars(0, y, x, rst, clk);

      clk = 1;
      x = 0;

      // Set state to the initial state (S0) at the beginning
      rst = 1;
      #10
      rst = 0;

      #5

      // Sequence: 101
      x = 1;
      #20
      x = 0;
      #20
      x = 1;
      #20

      // Reset
      rst = 1;
      #10
      rst = 0;
      #10

      // Sequence: 0010100
      x = 0;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 0;
      #20
      x = 0;
      #20

      // Reset
      rst = 1;
      #10
      rst = 0;
      #10

      // Sequence: 1001
      x = 1;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 1;
      #20

      // Reset
      rst = 1;
      #10
      rst = 0;
      #10

      // Sequence: 1010001
      x = 1;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 1;
      #20

      // Reset
      rst = 1;
      #10
      rst = 0;
      #10

      // Sequence: 1000001
      x = 1;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 1;
      #20

      // Reset
      rst = 1;
      #10
      rst = 0;
      #10

      // Sequence: 000111
      x = 0;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 1;
      #20

      // Reset
      rst = 1;
      #10
      rst = 0;
      #10

      // Sequence: 111000
      x = 1;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 0;
      #20

      $finish;

   end


endmodule