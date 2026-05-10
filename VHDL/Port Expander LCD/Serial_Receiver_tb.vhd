library IEEE;
use IEEE.std_logic_1164.all;

entity Serial_Receiver_tb is
end Serial_Receiver_tb;

architecture structural of Serial_Receiver_tb is

component Serial_Receiver is
 port(
	
     --Input port
		   
     SS, SCLK, SDX, reset : in std_logic;
            
     --Output port
		 
     D : out std_logic_vector (9 downto 0)
		);

  end  component;
  
  
  constant MCLK_PERIOD : time := 20 ns;
  constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
  
  signal SS_tb : std_logic;
  signal SCLK_tb : std_logic;
  signal SDX_tb : std_logic;
  signal reset_tb : std_logic;
  signal D_tb : std_logic_vector(9 downto 0);

  
begin 
UUT: Serial_Receiver 
  port map ( 
    SS => SS_tb, 
	 SCLK => SCLK_tb , 
	 SDX => SDX_tb,
	 reset => reset_tb, 
	 D => D_tb);


clk_gen : process
begin
        SCLK_tb <= '0';
        wait for MCLK_HALF_PERIOD;
	
        SCLK_tb <= '1';
        wait for MCLK_HALF_PERIOD;
		  
		  
end process;


stimulus: process
begin 

--Inicializar
   reset_tb  <= '1';
	SS_tb <= '0';
	SDX_tb  <= '0';
	wait for MCLK_PERIOD;
	
	
	reset_tb  <= '0';
	SDX_tb  <= '1';
	wait for MCLK_PERIOD*10;
	
	SS_tb <= '1';
	wait for MCLK_PERIOD*2;
	
	
   --Reset	
	reset_tb  <= '1';
	SS_tb <= '0';
	wait for MCLK_PERIOD*1;
	
	
	reset_tb  <= '0';
	SDX_tb  <= '0';
	wait for MCLK_PERIOD*2;
	SDX_tb  <= '1';
	wait for MCLK_PERIOD*2;
	SDX_tb  <= '0';
	wait for MCLK_PERIOD*2;
	SDX_tb  <= '1';
	wait for MCLK_PERIOD*2;
	SDX_tb  <= '0';
	wait for MCLK_PERIOD*2;
	
	SS_tb <= '1';
	wait for MCLK_PERIOD*2;
	
	
   --Reset	
	reset_tb  <= '1';
	SS_tb <= '0';
	wait for MCLK_PERIOD*1;
	
	reset_tb  <= '0';
	SDX_tb  <= '0';
	wait for MCLK_PERIOD*1;
	SDX_tb  <= '1';
	wait for MCLK_PERIOD*2;
	SDX_tb  <= '0';
	wait for MCLK_PERIOD*1;
	SDX_tb  <= '1';
	wait for MCLK_PERIOD*3;
	SDX_tb  <= '0';
	wait for MCLK_PERIOD*3;
	
	SS_tb <= '1';
	wait for MCLK_PERIOD*2;
	
  wait;
end process;

end  architecture;
