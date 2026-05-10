library ieee;

use ieee.std_logic_1164.all;

entity CounterDown is
 port(

   --Input Port
     
	  dataIn : in std_logic_vector(3 downto 0);
	  PL, CE, Reset, Clk: in std_logic;
    
	 --Output Port
     
	  Q: out std_logic_vector(3 downto 0));

	  
  end CounterDown;
  
  architecture structural of CounterDown is
component adder is
 port(
   
	--Input Port
     A, B: in std_logic_vector(3 downto 0);
	  Ci: in std_logic;
    
	 --Output Port
     S: out std_logic_vector(3 downto 0);
	  Co: out std_logic);

end component;

component MUX is
port(
   --Input port
	
	A, B : in std_logic_vector(3 downto 0);
   S : in std_logic;
	
	--Output port
	
     Y : out std_logic_vector(3 downto 0));
	  
end component;


component Register1 is
port(
   
	--Input port
	
	  Clk, Reset : in std_logic;
	  A: in std_logic_vector(3 downto 0);
     
	  
	--Output port
	
     Q: out std_logic_vector(3 downto 0));
	  
end component;

signal fiomuxadder, fioregisteradder, fioaddermux, fiomuxregister: std_logic_vector(3 downto 0);

begin

U1 : MUX port map ( A => "0000", B => "1111", S => CE , Y => fiomuxadder);
U2 : adder port map ( A => fioregisteradder, B => fiomuxadder , Ci => '0', S => fioaddermux, Co => open);
U3 : MUX port map ( A => fioaddermux , B => dataIn , S => PL, Y => fiomuxregister);
U4 : Register1 port map ( A => fiomuxregister, Clk => Clk, Reset => Reset, Q => fioregisteradder);

Q <= fioregisteradder;

end structural;
