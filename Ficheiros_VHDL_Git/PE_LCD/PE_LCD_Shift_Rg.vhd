library ieee;
use ieee.std_logic_1164.all;


entity PE_LCD_Shift_Rg is
   port(
	 
        -- input port
		  
        Serialin, CLK, en, reset: in std_logic;
       
        --Output port
	 
	     Q : out std_logic_vector (9 downto 0)
		  ); 
		  
end PE_LCD_Shift_Rg;

 architecture structural of PE_LCD_Shift_Rg is
component FFD is
 port(
   
	--Input Port
    
	 CLK, RESET, SET, D, EN : in std_logic;
		
	 --Output Port
	 
     Q : out std_logic);

end component;

signal f1, f2, f3, f4, f5, f6, f7, f8, f9, f10 : std_logic;


begin

U1 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => Serialin, Q => f1);
U2 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f1,  Q => f2);
U3 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f2,  Q => f3);
U4 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f3,  Q => f4);
U5 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f4,  Q => f5);
U6 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f5,  Q => f6);
U7 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f6,  Q => f7);
U8 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f7,  Q => f8);
U9 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f8,  Q => f9);
U10: FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => f9,  Q => f10);

Q(0) <= f1;
Q(1) <= f2;
Q(2) <= f3;
Q(3) <= f4;
Q(4) <= f5;
Q(5) <= f6;
Q(6) <= f7;
Q(7) <= f8;
Q(8) <= f9;
Q(9) <= f10;

end structural;
