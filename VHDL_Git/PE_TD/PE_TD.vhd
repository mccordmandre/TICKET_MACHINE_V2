library ieee;
use ieee.std_logic_1164.all;


entity PE_TD is
   port(
	
     --Input port
		   
     TDsel, SCLK, SDX, reset : in std_logic;
            
     --Output port
	  O : out std_logic_vector (3 downto 0);
     D : out std_logic_vector (3 downto 0);
	  PRT, RT : out std_logic); 
		  
end PE_TD;


architecture structural of PE_TD is
component Serial_Receiver is
    port(
	
     --Input port
		   
     SS, SCLK, SDX, reset : in std_logic;
            
     --Output port
		  
     D : out std_logic_vector (9 downto 0)
	  ); 
		  
end component;

signal invTDsel : std_logic;

begin

U1 : Serial_Receiver port map ( SCLK => SCLK, reset => reset, SS => TDsel, SDX => SDX, D(4 downto 1) => D, D(0) => RT, D(9) => PRT, D(8 downto 5) => O);


end structural;