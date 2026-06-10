library IEEE;
use IEEE.std_logic_1164.all;

entity RB_Control_tb is
end RB_Control_tb;

architecture behavioral of RB_Control_tb is

component RB_Control is
    port(
        --Input port

        DAV, CTS, Full, Empty, CLK, Reset: in std_logic;

        -- Output port
        Wr, selPG, Wreg, DAC, incPut, incGet : out std_logic);

		  
  end component;

 -- UUT signals
 constant MCLK_PERIOD : time := 20 ns;
 constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;

signal CLK_tb : std_logic;
signal Reset_tb : std_logic;
signal DAV_tb : std_logic;
signal CTS_tb : std_logic;
signal Full_tb : std_logic;
signal Empty_tb : std_logic;
signal Wr_tb : std_logic;
signal selPG_tb : std_logic;
signal Wreg_tb : std_logic;
signal DAC_tb : std_logic;
signal incPut_tb : std_logic;
signal incGet_tb : std_logic;

begin
	-- Unit Under Test
	UUT: RB_Control
		port map (
		
		   Reset => Reset_tb,
		   CLK => CLK_tb,
			DAV => DAV_tb,
			CTS => CTS_tb,
			Full => Full_tb,
			Empty => Empty_tb,
			Wr => Wr_tb,
			selPG => selPG_tb,
			Wreg => Wreg_tb,
			DAC => DAC_tb,
			incPut => incPut_tb,
			incGet => incGet_tb);

	clk_gen : process
	begin
		clk_tb <= '0';
		wait for MCLK_HALF_PERIOD;
		clk_tb <= '1';
		wait for MCLK_HALF_PERIOD;
	end process;


  
	stimulus: process
	begin 

	
   -- Inicializar + Reset
	Reset_tb <= '1';
	DAV_tb <= '0';
	CTS_tb <= '0';
	Full_tb <= '0';
	Empty_tb <= '1';
	wait for MCLK_PERIOD*2;	
	
	--SE -> Saidas Esperadas
--Saidas Condicionadas incPut, IncGet = '0'	
--CurrentState = Idle	
--Wr = '0'
--selPG = '0'
--Wreg = '0'
--DAC = '0'

		-- DAV
	Reset_tb <= '0';
	DAV_tb <= '1';
	wait for MCLK_PERIOD*5;
	
	--SE
--Saida Condicionada	incPut = '1'
--CurrentState = Write
--Wr = '1'
--selPG = '0'
--Wreg = '0'
--DAC = '1'


   -- Reset 
	Reset_tb <= '1';
	DAV_tb <= '0';
	CTS_tb <= '0';
	Full_tb <= '0';
	Empty_tb <= '1';
	wait for MCLK_PERIOD;
	
	--SE
--CurrentState = Idle
--Wr = '0'
--selPG = '0'
--Wreg = '0'
--DAC = '0'

	
	--CTS se estiver Full
	Reset_tb <= '0';
	Empty_tb <= '0';
	Full_tb <= '1';
	CTS_tb <= '1';
	wait for MCLK_PERIOD*5;
	
	--SE
--Saida Condicionada incGet = '1'	
--CurrentState = Read
--Wr = '0'
--selPG = '1'
--Wreg = '1'
--DAC = '0'

    -- Reset 
	Reset_tb <= '1';
	DAV_tb <= '0';
	CTS_tb <= '0';
	Full_tb <= '0';
	Empty_tb <= '1';
	wait for MCLK_PERIOD;
	
	--CurrentState = Idle
	
	--CTS se estiver mid
   Reset_tb <= '0';
	Empty_tb <= '0';
	CTS_tb <= '1';
	wait for MCLK_PERIOD*5;
	
	
	--SE
--Saida Condicionada incGet = '1'	
--CurrentState = Read
--Wr = '0'
--selPG = '1'
--Wreg = '1'
--DAC = '0'
	
	--not CTS
	CTS_tb <= '0';
	wait for MCLK_PERIOD*3;
	
	--SE
--CurrentState = Idle
--Wr = '0'
--selPG = '0'
--Wreg = '0'
--DAC = '0'
	
	
	
	
	
		wait;
		
	wait;



	end process;

end  architecture;
		