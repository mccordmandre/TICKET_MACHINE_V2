library ieee;

use ieee.std_logic_1164.all;

entity KD_Cont is
  port(
   
	--Input Port
     
	  CE, CLK, Reset : in std_logic;
    
	--Output Port
     
	  Q: out std_logic_vector(3 downto 0));
	  
  
 end KD_Cont;

architecture structural of KD_Cont is


component KD_adder is
 port(
   
	--Input Port
     A, B: in std_logic_vector(3 downto 0);
	  Ci: in std_logic;
    
	 --Output Port
     S: out std_logic_vector(3 downto 0);
	  Co: out std_logic);

end component;



component KD_Register1 is
port(
   
	--Input port
	
	  A: in std_logic_vector(3 downto 0);
	  Clk, Reset, En: in std_logic;
	 
     
	--Output port
	
     Q: out std_logic_vector(3 downto 0));
	  
end component;

signal fioregisteradder, fioadderregister : std_logic_vector(3 downto 0);

begin

U1 : KD_adder port map ( A => "0000", B => fioregisteradder, Ci => '1', S => fioadderregister, Co => open);
U2 : KD_Register1 port map ( A => fioadderregister, En => CE, Clk => Clk, Reset => Reset, Q => fioregisteradder);

Q <= fioregisteradder;

end structural;
