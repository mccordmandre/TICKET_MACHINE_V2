library ieee;
use ieee.std_logic_1164.all;


entity PE_LCD_Hold_Rg is
    port(
	 
        --input port
        
		  D: in std_logic_vector (9 downto 0);
		  CLK, reset: in std_logic;

        -- output port
        Q : out std_logic_vector (9 downto 0)
		  ); 
		  
end PE_LCD_Hold_Rg;

 architecture structural of PE_LCD_Hold_Rg is
component FFD is
 port(
   
	--Input Port
    
	 CLK, RESET, SET, D, EN : in std_logic;
		
	 --Output Port
	 
     Q : out std_logic);

end component;


begin

U1 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(0),  Q => Q(0));
U2 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(1),  Q => Q(1));
U3 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(2),  Q => Q(2));
U4 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(3),  Q => Q(3));
U5 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(4),  Q => Q(4));
U6 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(5),  Q => Q(5));
U7 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(6),  Q => Q(6));
U8 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(7),  Q => Q(7));
U9 : FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(8),  Q => Q(8));
U10: FFD port map ( CLK => CLK, EN => '1', RESET => reset, SET => '0', D => D(9),  Q => Q(9));

end structural;
