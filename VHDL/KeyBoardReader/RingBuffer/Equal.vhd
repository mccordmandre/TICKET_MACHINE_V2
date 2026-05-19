library ieee;

use ieee.std_logic_1164.all;

entity Equal is
  port(
   
	--Input port
	
	  A, B: in std_logic_vector(3 downto 0);
	
	 
	--Output port
	
     Q: out std_logic);
	  
 end Equal;
  
architecture structural of Equal is

begin

Q <= (A(0) xnor B(0)) and (A(1) xnor B(1)) and (A(2) xnor B(2)) and (A(3) xnor B(3)) ;

end structural;
  