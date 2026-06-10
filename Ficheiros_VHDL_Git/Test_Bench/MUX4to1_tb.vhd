library IEEE;
use IEEE.std_logic_1164.all;

entity MUX4to1_tb  is
end MUX4to1_tb;

architecture structural of MUX4to1_tb is

component MUX4to1 is
port(

  --Input port

    A: in std_logic_vector(3 downto 0);
    S: in std_logic_vector(1 downto 0);

  --Output port 

    Y : out std_logic
	 );

 end component;
 
 
 constant MCLK_PERIOD : time := 20 ns;
  
  signal A_tb: std_logic_vector(3 downto 0);
  signal S_tb: std_logic_vector(1 downto 0);
  signal Y_tb: std_logic; 
 
  
  begin 
UUT: MUX4to1 port map ( A => A_tb, S => S_tb, Y => Y_tb );

 
stimulus: process
begin 

    -- nada a ser pressed 
	 A_tb <= "1111"; 
	 S_tb <= "00";
    wait for MCLK_PERIOD*2;
	  
	 S_tb <= "01";
    wait for MCLK_PERIOD*3;
	  
	 S_tb <= "10";
    wait for MCLK_PERIOD*3;
	  
	 S_tb <= "11";
    wait for MCLK_PERIOD*3;
 
 -- para cada button pressed 
 
    A_tb <= "1110"; 
	 S_tb <= "00";
    wait for MCLK_PERIOD*3;
	 
    A_tb <= "1101";
	 S_tb <= "01";  
    wait for MCLK_PERIOD*3;
	 
    A_tb <= "1011"; 
	 S_tb <= "10";
    wait for MCLK_PERIOD*3;

    A_tb <= "0111"; 
	 S_tb <= "11";
    wait for MCLK_PERIOD*3; 
	
	
    wait;
end process; 

end  architecture;