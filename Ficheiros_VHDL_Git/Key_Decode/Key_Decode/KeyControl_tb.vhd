library IEEE;
use IEEE.std_logic_1164.all;

entity KeyControl_tb is
end KeyControl_tb;

architecture behavioral of KeyControl_tb is

component KeyControl is
 port(
 
   --Input port
	  
   CLK, Kack, Kpress, Reset: in std_logic;
   -- Tdelay : in std_logic(1 downto 0);
            
   -- Output port
	  
   Kval, Kscan: out std_logic);  
		  
  end component;

 -- UUT signals
 constant MCLK_PERIOD : time := 20 ns;
 constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;

signal CLK_tb : std_logic;
signal Kack_tb : std_logic;
signal Kpress_tb : std_logic;
signal Reset_tb : std_logic;
signal Kval_tb : std_logic;
signal Kscan_tb : std_logic;

begin
	-- Unit Under Test
	UUT: KeyControl
		port map (
			CLK => CLK_tb,
			Kack => Kack_tb,
			Kpress => Kpress_tb,
			Reset => Reset_tb,
			Kval => Kval_tb,
			Kscan => Kscan_tb);

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
	Kack_tb <= '0';
	Kpress_tb <= '0';
	Reset_tb <= '1';
	wait for MCLK_PERIOD*2;


	Reset_tb <= '0';
	wait for MCLK_PERIOD*2;

		-- CurrentState = SCANNING:
			-- SAIDAS ESPERADAS:
				-- KScan = 1
				-- Kval = 0
				
		Kack_tb <= '1';
		wait for MCLK_PERIOD*2;
			
	   Kpress_tb <= '1';
		wait for MCLK_PERIOD*2;
			
		Kack_tb <= '0';
	   wait for MCLK_PERIOD*2;
		-- next -> STATE_REGISTERING
		
		
	
		-- STATE_REGISTERING
			-- SAIDAS ESPERADAS:
				-- KScan = 0
				-- KVAL = 1
				
		
		Kpress_tb <= '0';
		wait for MCLK_PERIOD*2;
		
		Kpress_tb <= '1';
		wait for MCLK_PERIOD*2;
			
		Kack_tb <= '1';
		wait for MCLK_PERIOD*2;
		-- next -> STATE_WAITING_FOR_RELEASE
		
			
		
		-- WAITING FOR RELEASE
			-- SAIDAS ESPERADAS:
				-- KVal = 0
				-- KScan = 0
				
		
		Kack_tb <= '0';
		wait for MCLK_PERIOD*2;

		Kpress_tb <= '0';
		wait for MCLK_PERIOD*2;
		-- next -> STATE_SCANNING
		
	wait;



	end process;

end  architecture;