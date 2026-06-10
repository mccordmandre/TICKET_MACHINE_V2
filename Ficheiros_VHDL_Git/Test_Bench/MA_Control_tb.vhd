library IEEE;
use IEEE.std_logic_1164.all;

entity MA_Control_tb is
end MA_Control_tb;

architecture structural of MA_Control_tb is

component MA_Control is
  port(
	
     --Input port
		  
     putget, incPut, incGet, CLK, reset : in std_logic;
            
     --Output port
		  
	  A : out std_logic_vector(3 downto 0);  
     full, empty : out std_logic);

  end  component;
  
  
  constant MCLK_PERIOD : time := 20 ns;
  constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
  
  signal putget_tb, incPut_tb, incGet_tb, CLK_tb, reset_tb, full_tb, empty_tb : std_logic;
  signal A_tb : std_logic_vector(3 downto 0); 
  

begin 
  UUT: MA_Control 
      port map ( 
		    putget => putget_tb, 
			 incPut => incPut_tb, 
			 incGet => incGet_tb, 
			 CLK => CLK_tb, 
			 reset => reset_tb, 
			 full => full_tb, 
			 empty => empty_tb, 
			 A => A_tb);


clk_gen : process
begin
        CLK_tb <= '0';
        wait for MCLK_HALF_PERIOD;
        CLK_tb <= '1';
        wait for MCLK_HALF_PERIOD;
end process;
  

stimulus: process
begin 

--Reset
   reset_tb  <= '1';
	putget_tb  <= '0';
	incPut_tb  <= '0';
	incGet_tb  <= '0';
	wait for MCLK_PERIOD*3;
	
	reset_tb  <= '0';
	wait for MCLK_PERIOD;
	
   -- Saidas esperadas:
--Empty = 1
--Full = 0
--A = 0000

--Escrever um endereço:
   incPut_tb  <= '1';
	wait for MCLK_PERIOD;
	 
	-- SE:
--Empty = 0
--Full = 0
--A = 0001

--Escrever até dar Full
   wait for MCLK_PERIOD*15; 
	
	--SE:
--Empty = 0 
--Full = 1
--A = 0000	


   incPut_tb <= '0';
	wait for MCLK_PERIOD;
	
--Ler até dar Empty
	incGet_tb <= '1';
        putget_tb <= '1';
   wait for MCLK_PERIOD*16; 
	
	--SE
--Full = 0
--Empty = 1
-- A = 	0001


--Escrever
       incGet_tb <= '0';
       incPut_tb <= '1';
       putget_tb <= '0';
   wait for MCLK_PERIOD*7;

--Ler
       incPut_tb <= '0';
       incGet_tb <= '1';
       putget_tb <= '1';
   wait for MCLK_PERIOD*4;

       --SE
--Full = 0
--Empty = 0
--A = 0101
        

	wait;
	 
  wait;
end process;

end  architecture;
