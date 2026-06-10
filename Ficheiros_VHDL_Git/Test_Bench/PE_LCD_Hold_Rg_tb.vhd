library IEEE;
use IEEE.std_logic_1164.all;

entity PE_LCD_Hold_Rg_tb is
end PE_LCD_Hold_Rg_tb;

architecture structural of PE_LCD_Hold_Rg_tb is

component PE_LCD_Hold_Rg is
  port(
	 
    --input port
        
	 D: in std_logic_vector (9 downto 0);
	 CLK, reset: in std_logic;

    -- Output port
    Q : out std_logic_vector (9 downto 0)
	 );

  end  component;
  
  
  constant MCLK_PERIOD : time := 20 ns;
  constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
  
  
  signal D_tb : std_logic_vector (9 downto 0);
  signal CLK_tb : std_logic;
  signal reset_tb : std_logic;
  signal Q_tb : std_logic_vector(9 downto 0);

  
begin 
UUT: PE_LCD_Hold_Rg 
  port map ( 
    D => D_tb, 
	 CLK => CLK_tb, 
	 reset => reset_tb,
	 Q => Q_tb);


clk_gen : process
begin
        CLK_tb <= '0';
        wait for MCLK_HALF_PERIOD;
        CLK_tb <= '1';
        wait for MCLK_HALF_PERIOD;
end process;
  

stimulus: process
begin 

--Inicializar
   D_tb  <= "0000000000";
	reset_tb <= '1';
	wait for MCLK_PERIOD;
	
	reset_tb  <= '0';
	D_tb  <= "1111111111";
	wait for MCLK_PERIOD*2;
	
	
	--Reset	
	reset_tb  <= '1';
	wait for MCLK_PERIOD*2;
	
	reset_tb  <= '0';
	D_tb  <= "0101010101";
	wait for MCLK_PERIOD*2;
	
	
	--Reset	
	reset_tb  <= '1';
	wait for MCLK_PERIOD*2;
	
	reset_tb  <= '0';
	D_tb  <= "1100101001";
	wait for MCLK_PERIOD*2;
	 
  wait;
end process;

end architecture;
