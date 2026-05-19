library IEEE;
use IEEE.std_logic_1164.all;

entity MUX4_tb is
end MUX4_tb;

architecture behavioral of MUX4_tb is

component MUX4 is
 port(
 
        A : in  STD_LOGIC_VECTOR(3 downto 0);
        B : in  STD_LOGIC_VECTOR(3 downto 0);
        S : in  STD_LOGIC;
        Y : out STD_LOGIC_VECTOR(3 downto 0)
		  );
		  
  end component;

 -- UUT signals
 constant MCLK_PERIOD : time := 20 ns;
 constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;

signal A_tb : std_logic;
signal B_tb : std_logic;
signal S_tb : std_logic;
signal Y_tb : std_logic;

begin
	-- Unit Under Test
	UUT: MUX4
		port map (
			A => A_tb,
			B => B_tb,
			S => S_tb,
			Y => Y_tb,
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
		--  Y = A (0000)
		
	A_tb <= '0000';
	B_tb <= '1010'; 
	S_tb <= '0';
	wait for MCLK_PERIOD*2; 
	
	-- SAIDAS ESPERADAS:
		--  Y = B (0000)
		
	A_tb <= '1010';
	B_tb <= '0000';
	S_tb <= '1';
	wait for MCLK_PERIOD*2; 

		
	wait;



	end process;

end  architecture;