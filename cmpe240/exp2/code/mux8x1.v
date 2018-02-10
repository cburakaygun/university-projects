module mux8x1( sl2 , sl1 , sl0 , in0 , in1 , in2 , in3 , in4 , in5 , in6 , in7 , out );

    
    input sl2 , sl1 , sl0;     // Select bits
    
    input in0 , in1 , in2 , in3 , in4 , in5 , in6 , in7;    // Input bits
    
    output reg out;     // Output bit
    
    always @(sl2 , sl1 , sl0 , in0 , in1 , in2 , in3 , in4 , in5 , in6 , in7)
    begin
    
        if( sl2 == 0 && sl1 == 0 && sl0 == 0 )
            out <= in0;
        
        else if( sl2 == 0 && sl1 == 0 && sl0 == 1 )
            out <= in1;
        
        else if( sl2 == 0 && sl1 == 1 && sl0 == 0 )
            out <= in2;
        
        else if( sl2 == 0 && sl1 == 1 && sl0 == 1 )
            out <= in3;
        
        else if( sl2 == 1 && sl1 == 0 && sl0 == 0 )
            out <= in4;
        
        else if( sl2 == 1 && sl1 == 0 && sl0 == 1 )
            out <= in5;
        
        else if( sl2 == 1 && sl1 == 1 && sl0 == 0 )
            out <= in6;
        
        else    // if( sl2 == 1 && sl1 == 1 && sl0 == 1 )
            out <= in7;
            
     end

     
endmodule