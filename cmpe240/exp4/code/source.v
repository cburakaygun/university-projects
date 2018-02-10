`timescale 1ns/1ns
module source(y , x , rst , clk);

output reg [1:0] y;
input wire x , rst , clk;

reg [2:0] s;


initial
    s <= 3'b000;


// RESET
always @(negedge clk) begin

    if( rst )     // Pressing rst is 1
        s <= 3'b000;
    
end


// OUTPUT CHANGES WITH RESPECT TO INPUT AND STATES
always @(x , s) begin
    
    if( s == 3'b000 )        
        y <= 2'b00;			// for both x values, y = 00
        
    else if( s == 3'b001 )     
        y <= 2'b00;			// for both x values, y = 00
        
    else if( s == 3'b010 ) begin
    
        if( x == 1'b0 )
            y <= 2'b01;

        else
            y <= 2'b00;
        
    end
    
    else if( s == 3'b011 ) begin
    
        if( x == 1'b0 )
            y <= 2'b10;
        
        else
            y <= 2'b00;
        
    end
    
    else if( s == 3'b100 ) begin
    
        if( x == 1'b0 )
            y <= 2'b11;
        
        else
            y <= 2'b00;
        
    end
    
end


// STATES CHANGE WITH RESPECT TO INPUT AND CLOCK
always @(posedge clk) begin
    
    if( s == 3'b000 ) begin
        
        if( x == 1'b0 )		// if x = 0
            s <= 3'b001;	// goes to another state
            				// else goes nowhere (goes to itself)
        
     end
     else if( s == 3'b001 ) begin
     
        if( x == 1'b1 )		// if x = 1
            s <= 3'b010;	// goes to another state
            				// else goes nowhere (goes to itself)
        
    end
    else if( s == 3'b010 ) begin
    
        if( x == 1'b0 )
            s <= 3'b001;

        else
            s <= 3'b011;
        
    end
    else if( s == 3'b011 ) begin
    
        if( x == 1'b0 )
            s <= 3'b001;

        else
            s <= 3'b100;
        
    end
    else if( s == 3'b100 ) begin
    
        if( x == 1'b0 )		// if x = 0
            s <= 3'b001;	// goes to another state
            				// else goes nowhere (goes to itself)
        
    end
    
end


endmodule