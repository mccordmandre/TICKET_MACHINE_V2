library ieee;

use ieee.std_logic_1164.all;

entity KT_MUX is
 port(
   --Input port
	
	A, B : in std_logic;
   S : in std_logic;
	
	--Output port
	
     Y : out std_logic);

 end KT_MUX;
  
architecture structural of KT_MUX is

begin

Y <= (not S and A) or (S and B);

end structural;
  