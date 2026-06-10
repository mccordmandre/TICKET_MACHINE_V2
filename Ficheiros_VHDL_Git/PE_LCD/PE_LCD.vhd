library ieee;
use ieee.std_logic_1164.all;


entity PE_LCD is
   port(
	
     --Input port
		   
     LCDsel, SCLK, SDX, reset : in std_logic;
            
     --Output port
		  
     D : out std_logic_vector (7 downto 0);
	  RS, E : out std_logic); 
		  
end PE_LCD;


architecture structural of PE_LCD is
component PE_LCD_Serial_Receiver is
    port(
	
     --Input port
		   
     SS, SCLK, SDX, reset : in std_logic;
            
     --Output port
		  
     D : out std_logic_vector (9 downto 0)
	  ); 
		  
end component;

signal invLCDsel : std_logic;

begin

U1 : PE_LCD_Serial_Receiver port map ( SCLK => SCLK, reset => reset, SS => invLCDsel, SDX => SDX, D(8 downto 1) => D, D(0) => RS, D(9) => E);

invLCDsel <= not LCDsel;

end structural;