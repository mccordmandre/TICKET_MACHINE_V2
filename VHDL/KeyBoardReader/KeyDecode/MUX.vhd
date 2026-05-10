library ieee;

use ieee.std_logic_1164.all;

entity MUX is
 port(
   --Input port
	
	A, B : in std_logic;
   S : in std_logic;
	
	--Output port
	
     Y : out std_logic);

 end MUX;
  
architecture structural of MUX is

begin

Y <= (not S and A) or (S and B);

end structural;
  