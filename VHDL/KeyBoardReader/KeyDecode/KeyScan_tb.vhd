library IEEE;
use IEEE.std_logic_1164.all;

entity KeyScan_tb is
end KeyScan_tb;

architecture structural of KeyScan_tb is

component KeyScan is
 port(
    
	--Input port
	    
    Kscan, CLK, Reset: in std_logic;
	 L : in std_logic_vector(3 downto 0);
	 
	 --Output port
	 
	 C : out std_logic_vector(3 downto 0);
    K : out std_logic_vector(3 downto 0);
	 Kpress : out std_logic);

  end  component;
  
  
  constant MCLK_PERIOD : time := 20 ns;
  constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
  
  signal Kscan_tb : std_logic;
  signal CLK_tb : std_logic;
  signal Reset_tb : std_logic;
  signal Kpress_tb : std_logic;
  signal L_tb : std_logic_vector(3 downto 0);
  signal C_tb : std_logic_vector(3 downto 0);
  signal K_tb : std_logic_vector(3 downto 0);
  

begin 
UUT: KeyScan port map ( Kscan => Kscan_tb , CLK => CLK_tb, Reset => Reset_tb, L => L_tb, C => C_tb, K => K_tb, Kpress => Kpress_tb);


clk_gen : process
begin
        CLK_tb <= '0';
        wait for MCLK_HALF_PERIOD;
        CLK_tb <= '1';
        wait for MCLK_HALF_PERIOD;
end process;
  

stimulus: process
begin 

   Reset_tb  <= '1';
	Kscan_tb  <= '0';
	L_tb  <= "1111";
	wait for MCLK_PERIOD;
	
	Reset_tb  <= '0';
	Kscan_tb  <= '1';
	L_tb  <= "1110";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "1101";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "1011";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0111";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "1100";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "1001";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0011";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0110";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0101";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "1010";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "1000";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0001";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0100";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0010";
	wait for MCLK_PERIOD*16;
	
	L_tb  <= "0000";
	wait for MCLK_PERIOD*16;

	 
  wait;
end process;

end  architecture;
