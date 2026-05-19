library IEEE;
use IEEE.std_logic_1164.all;

entity Mini_Control_tb is
end Mini_Control_tb;

architecture behavioral of Mini_Control_tb is

component Mini_Control is
 port(
 
        --Input port

        IncPut, IncGet, equal ,Reset, CLK: in std_logic;

        -- Output port
        Full, Empty: out std_logic);
		  
  end component;

 -- UUT signals
 constant MCLK_PERIOD : time := 20 ns;
 constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;

signal IncPut_tb : std_logic;
signal IncGet_tb : std_logic;
signal equal_tb : std_logic;
signal Reset_tb : std_logic;
signal CLK_tb : std_logic;
signal Full_tb : std_logic;
signal Empty_tb : std_logic;

begin
	-- Unit Under Test
	UUT: Mini_Control
		port map (
			IncPut => IncPut_tb,
			IncGet => IncGet_tb,
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
	IncPut_tb <= '0';
	IncGet_tb <= '0';
	equal_tb <= '1';
	Reset_tb <= '1';
	wait for MCLK_PERIOD*2;


	Reset_tb <= '0';
	wait for MCLK_PERIOD*2;

		-- CurrentState = ST_EMPTY:
			-- SAIDAS ESPERADAS:
				-- Full = 0
				-- Empty = 1
					
		equal_tb <= '1';
		wait for MCLK_PERIOD*2; 
		
		
	   equal_tb <= '0';
		wait for MCLK_PERIOD*2;
		
		-- next -> ST_mid
		
		
	
		-- ST_mid
			-- SAIDAS ESPERADAS:
				-- Full = 1
				-- Empty = 0
				
				
				
		equal_tb <= '1';
		wait for MCLK_PERIOD*1;
		
		IncPut_tb <= '1';
		wait for MCLK_PERIOD*2; 
		-- next -> ST_Full
			
		IncPut_tb <= '0';
		wait for MCLK_PERIOD; 
		
		IncGet_tb <= '1';
		wait for MCLK_PERIOD*2;
		-- next -> ST_EMPTY
		
		IncGet_tb <= '0';
		wait for MCLK_PERIOD;
		-- next -> ST_EMPTY
		
		-- ST_Full
			-- SAIDAS ESPERADAS:
				-- Full = 1
				-- Empty = 0

		
	   equal_tb <= '0';
		wait for MCLK_PERIOD*2;
	
		-- next -> ST_mid
		
	wait;



	end process;

end  architecture;