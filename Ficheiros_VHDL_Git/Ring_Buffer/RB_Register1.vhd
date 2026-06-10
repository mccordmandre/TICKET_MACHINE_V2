library ieee;

use ieee.std_logic_1164.all;

entity RB_Register1 is
  port(
   
	--Input port
	
	  A: in std_logic_vector(3 downto 0);
	  Clk, Reset, En: in std_logic;
	 
     
	--Output port
	
     Q: out std_logic_vector(3 downto 0));
	  
  
  end RB_Register1;
  
  architecture structural of RB_Register1 is
component FFD is
 port(
   
	--Input Port
    
	 CLK, RESET, SET, D, EN : in std_logic;
		
	 --Output Port
	 
     Q : out std_logic);

end component;


begin

U1 : FFD port map ( CLK => Clk, EN => En, RESET => Reset, SET => '0', D => A(0), Q => Q(0));
U2 : FFD port map ( CLK => Clk, EN => En, RESET => Reset, SET => '0', D => A(1), Q => Q(1));
U3 : FFD port map ( CLK => Clk, EN => En, RESET => Reset, SET => '0', D => A(2), Q => Q(2));
U4 : FFD port map ( CLK => Clk, EN => En, RESET => Reset, SET => '0', D => A(3), Q => Q(3));





end structural;
