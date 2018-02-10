`timescale 1ns/1ns
module testbench();

reg x , clk , rst;
wire [1:0] y;

source s(.y(y),.x(x),.rst(rst),.clk(clk));

always begin
    #10
    clk = ~clk;
end

initial begin

    $dumpfile("TimingDiagram.vcd");
    $dumpvars(0, y, x, rst, clk);
    
    clk = 0;
    x = 0;
    rst = 0;
    
    // Set state to the initial state (S0) at the beginning
      #5
      rst = 1;
      #20
      rst = 0;     
      

      // Sequence: 010
      x = 0;
      #15
      x = 1;
      #20
      x = 0;
      #15

      // Reset
      rst = 1;
      #10
      rst = 0;

      // Sequence: 1101011
      x = 1;
      #15
      x = 1;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 1;
      #15

      // Reset
      rst = 1;
      #10
      rst = 0;

      // Sequence: 0110
      x = 0;
      #15
      x = 1;
      #20
      x = 1;
      #20
      x = 0;
      #15

      // Reset
      rst = 1;
      #10
      rst = 0;

      // Sequence: 0101110
      x = 0;
      #15
      x = 1;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 0;
      #15

      // Reset
      rst = 1;
      #10
      rst = 0;

      // Sequence: 0111110
      x = 0;
      #15
      x = 1;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 0;
      #15

      // Reset
      rst = 1;
      #10
      rst = 0;

      // Sequence: 000111
      x = 0;
      #15
      x = 0;
      #20
      x = 0;
      #20
      x = 1;
      #20
      x = 1;
      #20
      x = 1;
      #15

      // Reset
      rst = 1;
      #10
      rst = 0;

      // Sequence: 111000
      x = 1;
      #15
      x = 1;
      #20
      x = 1;
      #20
      x = 0;
      #20
      x = 0;
      #20
      x = 0;
      #15

      $finish;

end    

endmodule