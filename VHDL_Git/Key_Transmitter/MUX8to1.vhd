library ieee;
use ieee.std_logic_1164.all;


entity MUX8to1 is
  port(
    
	--Input port
	 
	 A: in std_logic_vector( 7 downto 0);   
    S: in std_logic_vector( 2 downto 0);
	 
	 --Output port
	 
     Y : out std_logic);
		  
end MUX8to1;


architecture structural of MUX8to1 is
component KT_MUX is
  port(
  
   --Input port
	
	  A, B : in std_logic;
     S : in std_logic;
	
	--Output port
	
     Y : out std_logic);

 end component;
  

signal fio0, fio1, fio2, fio3, fio4, fio5 : std_logic;


begin

U1 : KT_MUX port map( A => A(0) ,  B => A(1) , S => S(0), Y => fio0);

U2 : KT_MUX port map( A => A(2), B => A(3),  S => S(0), Y => fio1);

U3 : KT_MUX port map( A => A(4), B => A(5), S => S(0), Y => fio2);

U4 : KT_MUX port map( A => A(6), B => A(7), S => S(0), Y => fio3);

U5 : KT_MUX port map( A => fio0, B => fio1, S => S(1), Y => fio4);

U6 : KT_MUX port map( A => fio2, B => fio3, S => S(1), Y => fio5);

U7 : KT_MUX port map( A => fio4, B => fio5, S => S(2), Y => Y);



end structural;