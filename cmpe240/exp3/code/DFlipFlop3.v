`timescale 1ns / 1ns
module DFlipFlop3(out , in , rst , clk);

	output reg [2:0] out;
	input [2:0] in;
	input rst;
	input clk;

always @(posedge clk)
	begin
		if(rst) // Pressing reset is 1
			out <= 3'b000;
		else
			out <= in;
	end

endmodule
