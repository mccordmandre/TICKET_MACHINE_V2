library IEEE;
use IEEE.std_logic_1164.all;

entity KD_Cont_tb  is
end KD_Cont_tb;

architecture structural of KD_Cont_tb is

component KD_Cont is
port(
   
	--Input Port
     
	  CE, CLK, Reset : in std_logic;
    
	--Output Port
     
	  Q: out std_logic_vector(3 downto 0)
	  );
	  
  
 end component;
 
 
  constant MCLK_PERIOD : time := 20 ns;
  constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
  
  signal CE_tb: std_logic;
  signal CLK_tb: std_logic;
  signal Reset_tb : std_logic;
  signal Q_tb : std_logic_vector(3 downto 0);  
  
  begin 
UUT: KD_Cont port map ( CE => CE_tb, CLK => CLK_tb, Reset => Reset_tb, Q => Q_tb );
  
clk_gen : process
begin
        CLK_tb <= '0';
        wait for MCLK_HALF_PERIOD;
        CLK_tb <= '1';
        wait for MCLK_HALF_PERIOD;
end process;
  
stimulus: process
begin 

 
    --Reset
	 CE_tb <= '0'; 
	 Reset_tb <= '1';
    wait for MCLK_PERIOD*2;
	 
	 CE_tb <= '1'; 
    wait for MCLK_PERIOD*2;
	 
	 Reset_tb <= '0';
    wait for MCLK_PERIOD*18;
	 
	 CE_tb <= '0'; 
    wait for MCLK_PERIOD*3;
 
	
    wait;
end process;

end  architecture;