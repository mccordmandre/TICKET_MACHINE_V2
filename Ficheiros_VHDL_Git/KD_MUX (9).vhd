library IEEE;
use IEEE.std_logic_1164.all;

entity Dec2to4_tb is
end Dec2to4_tb;

architecture structural of Dec2to4_tb is

component Dec2to4 is
port(
  --Input port

	 S1, S0 : in  std_logic;

  --Output port 

    o : out std_logic_vector(3 downto 0));


  end  component;
  
  
  signal S1_tb : std_logic;
  signal S0_tb : std_logic;
  signal o_tb : std_logic_vector(3 downto 0);

  constant Clk_Period : time := 20 ns;
  

begin 
UUT: Dec2to4 port map ( S1 => S1_tb , S0 => S0_tb, o => o_tb );

stimulus: process
begin 

 -- valores a sair a bem 
   S1_tb  <= '0';
	S0_tb  <= '0';
	wait for Clk_Period*3;
	
	S1_tb  <= '0';
	S0_tb  <= '1';
	wait for Clk_Period*3;
	
	S1_tb  <= '1';
	S0_tb  <= '0';
	wait for Clk_Period*3;
	
	S1_tb  <= '1';
	S0_tb  <= '1';
	wait for Clk_Period*3;
	 
	 
  wait;
end process;

end  architecture;

 