library ieee;
use ieee.std_logic_1164.all;


entity Coin_Acceptor is
   port(
	
     --Input port
		   
     accept, collect, eject : in std_logic;
            
     --Output port
		  
     Coinid : out std_logic_vector (2 downto 0);
	  Coin: out std_logic); 
		  
end Coin_Acceptor;


architecture structural of Coin_Acceptor is
begin
end structural;


