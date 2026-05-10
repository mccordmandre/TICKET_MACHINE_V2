library ieee;
use ieee.std_logic_1164.all;


entity MUX4to1 is
  port(
    
	--Input port
	 
	 A: in std_logic_vector( 3 downto 0);   
    S: in std_logic_vector( 1 downto 0);
	 
	 --Output port
	 
     Y : out std_logic);
		  
end MUX4to1;


architecture structural of MUX4to1 is
component MUX is
  port(
  
   --Input port
	
	  A, B : in std_logic;
     S : in std_logic;
	
	--Output port
	
     Y : out std_logic);

 end component;
  

signal fio0, fio1 : std_logic;


begin

U1 : MUX port map( A => A(0) ,  B => A(1) , S => S(0), Y => fio0);

U2 : MUX port map( A => A(2), B => A(3),  S => S(0), Y => fio1);

U3 : MUX port map( A => fio0, B => fio1 , S => S(1), Y => Y);



end structural;
