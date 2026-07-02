library ieee;
use ieee.std_logic_1164.all;


entity Manut is
   port(
	
     --Input port
		
	   M_in : in std_logic;
            
     --Output port
		  
	   M_out : out std_logic); 
		  
end Manut;


architecture structural of Manut is
begin

M_out <= M_in;

end structural;