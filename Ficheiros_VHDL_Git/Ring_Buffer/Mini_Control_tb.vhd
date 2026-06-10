library IEEE;
use IEEE.std_logic_1164.all;

entity Mini_Control_tb is
end Mini_Control_tb;

architecture behavioral of Mini_Control_tb is

component Mini_Control is
 port(
 
        --Input port

       putget, equal ,Reset, CLK: in std_logic;

        -- Output port
        Full, Empty: out std_logic);
		  
  end component;

 -- UUT signals
 constant MCLK_PERIOD : time := 20 ns;
 constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;

signal equal_tb : std_logic;
signal Reset_tb : std_logic;
signal CLK_tb : std_logic;
signal Full_tb : std_logic;
signal Empty_tb : std_logic;
signal putget_tb : std_logic;

begin
	-- Unit Under Test
	UUT: Mini_Control
		port map (
		
			putget => putget_tb,
			equal => equal_tb,
			Reset => Reset_tb,
			CLK => CLK_tb,
			Full => Full_tb,
			Empty => Empty_tb);

	clk_gen : process
	begin
		clk_tb <= '0';
		wait for MCLK_HALF_PERIOD;
		clk_tb <= '1';
		wait for MCLK_HALF_PERIOD;
	end process;


  
	stimulus: process
	begin 

	-- reset
	putget_tb <= '0';
	equal_tb <= '1';
	Reset_tb <= '1';
	wait for MCLK_PERIOD*3;

	-- CurrentState = ST_EMPTY:
			-- SAIDAS ESPERADAS:
				-- Full = 0
				-- Empty = 1 
		
	Reset_tb <= '0';
	equal_tb <= '0';
	wait for MCLK_PERIOD*3;
	
	-- CurrentState = ST_mid:
			-- SAIDAS ESPERADAS:
				-- Full = 0
				-- Empty = 0
	
	equal_tb <= '1';
	wait for MCLK_PERIOD*3;
	
	-- CurrentState = ST_Full:
			-- SAIDAS ESPERADAS:
				-- Full = 1
				-- Empty = 0
				
	-- reset
	equal_tb <= '1';
	Reset_tb <= '1';
	wait for MCLK_PERIOD*3;

	-- CurrentState = ST_EMPTY:
			-- SAIDAS ESPERADAS:
				-- Full = 0
				-- Empty = 1 
		
	Reset_tb <= '0';
	equal_tb <= '0';
	wait for MCLK_PERIOD*3;
	
	-- CurrentState = ST_mid:
			-- SAIDAS ESPERADAS:
				-- Full = 0
				-- Empty = 0
	   
		
	equal_tb <= '1';
	putget_tb <= '1';
	wait for MCLK_PERIOD*3;
	
	-- CurrentState = ST_Empty:
			-- SAIDAS ESPERADAS:
				-- Full = 0
				-- Empty = 1
	
   -- reset
	putget_tb <= '0';
	equal_tb <= '1';
	Reset_tb <= '1';
	wait for MCLK_PERIOD*3;	
	
	
		wait;
		
	wait;



	end process;

end  architecture;
		
