library ieee;
use ieee.std_logic_1164.all;


entity KT_Hold_Rg is
    port(
	 
        --input port
        
		  D: in std_logic_vector (3 downto 0);
		  CLK, reset, en: in std_logic;

        -- output port
        Q : out std_logic_vector (3 downto 0)); 
		  
end KT_Hold_Rg;

 architecture structural of KT_Hold_Rg is
component FFD is
 port(
   
	--Input Port
    
	 CLK, RESET, SET, D, EN : in std_logic;
		
	 --Output Port
	 
     Q : out std_logic);

end component;


begin

U1 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => D(0),  Q => Q(0));
U2 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => D(1),  Q => Q(1));
U3 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => D(2),  Q => Q(2));
U4 : FFD port map ( CLK => CLK, EN => en, RESET => reset, SET => '0', D => D(3),  Q => Q(3));

end structural;
