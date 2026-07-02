library ieee;
use ieee.std_logic_1164.all;


entity Coin_Acceptor is
   port(
	
     --Input port
		   
     accept_in, collect_in, eject_in, Coin_in : in std_logic;
	  
	  Coinid_in : in std_logic_vector (2 downto 0);
	   
            
     --Output port
	  
	  accept_out, collect_out, eject_out, Coin_out : out std_logic;
	  
	  Coinid_out : out std_logic_vector (2 downto 0)
	  );  
	  
		  
end Coin_Acceptor;


architecture structural of Coin_Acceptor is
begin

Coinid_out  <= Coinid_in;
Coin_out    <= Coin_in;
accept_out  <= accept_in; 
collect_out <= collect_in;
eject_out   <= eject_in;

end structural;


