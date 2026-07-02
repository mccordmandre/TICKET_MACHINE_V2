library ieee;
use ieee.std_logic_1164.all;


entity Serial_Receiver is
   port(
	
     --Input port
		   
     SS, SCLK, SDX, reset : in std_logic;
            
     --Output port
		  
     D : out std_logic_vector (9 downto 0)); 
		  
end Serial_Receiver;


architecture structural of Serial_Receiver is
component Shift_Rg is
    port(
	 
        -- input port
		  
        Serialin, CLK, en, reset: in std_logic;
       
        --Output port
	 
	     Q : out std_logic_vector (9 downto 0));
		  
end component;



component Hold_Rg is
    port(
	 
        --input port
        
		  D : in std_logic_vector (9 downto 0);
		  CLK, reset: in std_logic;

        -- output port
        Q : out std_logic_vector (9 downto 0)
		  );

end component;


signal fioSS : std_logic;
signal fioQ : std_logic_vector(9 downto 0);


begin

U1 : Shift_Rg port map ( CLK => SCLK, reset => reset, Serialin  => SDX, en => fioSS, Q => fioQ);

U2 : Hold_Rg port map ( CLK => SS, reset => reset, D => fioQ, Q => D);

fioSS <= not SS;

end structural;