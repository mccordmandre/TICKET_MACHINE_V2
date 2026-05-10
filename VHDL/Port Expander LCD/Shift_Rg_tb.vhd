library IEEE;
use IEEE.std_logic_1164.all;

entity Shift_Rg_tb is
end Shift_Rg_tb;

architecture structural of Shift_Rg_tb is

component Shift_Rg is
 port(
	 
     --Input port
		  
    Serialin, CLK, en, reset: in std_logic;
       
     --Output port
	 
	  Q : out std_logic_vector (9 downto 0)
	  ); 

  end  component;
  
  
  constant MCLK_PERIOD : time := 20 ns;
  constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
  
  signal Serialin_tb : std_logic;
  signal CLK_tb : std_logic;
  signal en_tb : std_logic;
  signal reset_tb : std_logic;
  signal Q_tb : std_logic_vector(9 downto 0);

  
begin 
UUT: Shift_Rg 
  port map ( 
    Serialin => Serialin_tb, 
	 CLK => CLK_tb, 
	 en => en_tb,
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
   reset_tb  <= '1';
	Serialin_tb  <= '0';
	en_tb <= '0';
	wait for MCLK_PERIOD;
	
	
	reset_tb  <= '0';
	en_tb <= '1';
	Serialin_tb  <= '1';
	wait for MCLK_PERIOD*10;
	
	
--Reset	
	reset_tb  <= '1';
	wait for MCLK_PERIOD*1;
	
	
	reset_tb  <= '0';
	Serialin_tb  <= '0';
	wait for MCLK_PERIOD*2;
	Serialin_tb  <= '1';
	wait for MCLK_PERIOD*2;
	Serialin_tb  <= '0';
	wait for MCLK_PERIOD*2;
	Serialin_tb  <= '1';
	wait for MCLK_PERIOD*2;
	Serialin_tb  <= '0';
	wait for MCLK_PERIOD*2;
	
	--Reset	
	reset_tb  <= '1';
	wait for MCLK_PERIOD*2;
	
	
	reset_tb  <= '0';
	Serialin_tb  <= '0';
	wait for MCLK_PERIOD*1;
	Serialin_tb  <= '1';
	wait for MCLK_PERIOD*2;
	Serialin_tb  <= '0';
	wait for MCLK_PERIOD*1;
	Serialin_tb  <= '1';
	wait for MCLK_PERIOD*3;
	Serialin_tb  <= '0';
	wait for MCLK_PERIOD*3;
			
	 
  wait;
end process;

end  architecture;
