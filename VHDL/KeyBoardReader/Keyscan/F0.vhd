library ieee;

use ieee.std_logic_1164.all;

entity F0 is
  port(
   
	--Input Port
     
	 A: in std_logic_vector(3 downto 0);
    
	--Output Port
     
	 TC: out std_logic);
	  
  
  end F0;
  
  architecture structural of F0 is
begin

TC  <= not A(0) and not A(1) and not A(2) and not A(3);


end structural;
  
  
