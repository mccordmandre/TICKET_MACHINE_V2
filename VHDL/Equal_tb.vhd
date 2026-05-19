library IEEE;
use IEEE.std_logic_1164.all;

entity Equal_tb is
end Equal_tb;

architecture behavioral of Equal_tb is

component Equal is
 port(
 
      --Input port
	
	  A, B: in std_logic_vector(3 downto 0);
	
	 
	--Output port
	
     Q: out std_logic);
		  
  end component;

 -- UUT signals
 constant MCLK_PERIOD : time := 20 ns;
 constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;

signal A_tb : std_logic;
signal B_tb : std_logic;
signal Q_tb : std_logic;

begin
	-- Unit Under Test
	UUT: Equal
		port map (
			A => A_tb,
			B => B_tb,
			Q => S_tb,
			);

	clk_gen : process
	begin
		clk_tb <= '0';
		wait for MCLK_HALF_PERIOD;
		clk_tb <= '1';
		wait for MCLK_HALF_PERIOD;
	end process;


  
	stimulus: process
	begin 

		-- SAIDAS ESPERADAS:
		--  Q = 1
		
	A_tb <= '0000';
	B_tb <= '0000'; 
	wait for MCLK_PERIOD*2; 
	
		-- SAIDAS ESPERADAS:
		--  Q = 1
		
	A_tb <= '0001';
	B_tb <= '0001'; 
	wait for MCLK_PERIOD*2;
	
		-- SAIDAS ESPERADAS:
		--  Q = 1
		
	A_tb <= '0011';
	B_tb <= '0011'; 
	wait for MCLK_PERIOD*2;
	
	-- SAIDAS ESPERADAS:
		--  Q = 1
		
	A_tb <= '0110';
	B_tb <= '0110'; 
	wait for MCLK_PERIOD*2;
	
		-- SAIDAS ESPERADAS:
		--  Q = 1
		
	A_tb <= '0101';
	B_tb <= '0101'; 
	wait for MCLK_PERIOD*2;
	
	-- SAIDAS ESPERADAS:
		--  Q = 1
		
	A_tb <= '0111';
	B_tb <= '0111'; 
	wait for MCLK_PERIOD*2;
	
		-- SAIDAS ESPERADAS:
		--  Q = 1
		
	A_tb <= '1111';
	B_tb <= '1111'; 
	wait for MCLK_PERIOD*2;
	
	
      -- SAIDAS ESPERADAS:
		--  Q = 0
		
	A_tb <= '0010';
	B_tb <= '0000'; 
	wait for MCLK_PERIOD*2; 
	
	   -- SAIDAS ESPERADAS:
		--  Q = 0
		
	A_tb <= '0010';
	B_tb <= '0100'; 
	wait for MCLK_PERIOD*2;
	
		-- SAIDAS ESPERADAS:
		--  Q = 0
		
	A_tb <= '1010';
	B_tb <= '0010'; 
	wait for MCLK_PERIOD*2;
	
		-- SAIDAS ESPERADAS:
		--  Q = 0
		
	A_tb <= '1011';
	B_tb <= '0010'; 
	wait for MCLK_PERIOD*2;

		-- SAIDAS ESPERADAS:
		--  Q = 0
		
	A_tb <= '1010';
	B_tb <= '0011'; 
	wait for MCLK_PERIOD*2;
	
		-- SAIDAS ESPERADAS:
		--  Q = 0
		
	A_tb <= '1010';
	B_tb <= '1111'; 
	wait for MCLK_PERIOD*2;
	
		-- SAIDAS ESPERADAS:
		--  Q = 0
		
	A_tb <= '1110';
	B_tb <= '1111'; 
	wait for MCLK_PERIOD*2;

		
	wait;

	end process;

end  architecture;