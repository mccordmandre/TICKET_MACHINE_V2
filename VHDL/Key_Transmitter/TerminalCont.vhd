library ieee;

use ieee.std_logic_1164.all;

entity TerminalCont is
  port(
   
	--Input Port
     
	 A: in std_logic_vector(2 downto 0);
    
	--Output Port
     
	 TC: out std_logic);
	  
  
  end TerminalCont;
  
  architecture structural of TerminalCont is
begin

TC  <=  A(0) and A(1) and A(2);

end structural;
