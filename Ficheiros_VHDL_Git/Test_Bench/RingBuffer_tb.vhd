library IEEE;
use IEEE.std_logic_1164.all;

entity RingBuffer_tb is
end RingBuffer_tb;

architecture structural of RingBuffer_tb is

component RingBuffer is
 port(
	
     --Input port
		  
     CTS, DAV, RESET, CLK : in std_logic;
	  D : in std_logic_vector (3 downto 0);
	  
            
     --Output port
		  
     Wreg, DAC : out std_logic;
	  Q : out std_logic_vector (3 downto 0));
		  
  end  component;
  
  
  constant MCLK_PERIOD : time := 20 ns;
  constant MCLK_HALF_PERIOD : time := MCLK_PERIOD / 2;
  
  signal CLK_tb : std_logic;
  signal RESET_tb : std_logic;
  signal CTS_tb : std_logic;
  signal DAV_tb : std_logic;
  signal D_tb : std_logic_vector(3 downto 0);
  signal Wreg_tb : std_logic;
  signal DAC_tb : std_logic;
  signal Q_tb : std_logic_vector(3 downto 0);
  

begin 
UUT: RingBuffer 
       port map ( 
		    CLK => CLK_tb,
			 RESET => RESET_tb, 
          CTS => CTS_tb, 
			 DAV => DAV_tb, 
			 D => D_tb,  
			 Wreg => Wreg_tb, 
			 DAC => DAC_tb,
			 Q => Q_tb);


clk_gen : process
begin
        CLK_tb <= '0';
        wait for MCLK_HALF_PERIOD;
        CLK_tb <= '1';
        wait for MCLK_HALF_PERIOD;
end process;
  

stimulus: process
begin 

--Inicializar + Reset
   RESET_tb  <= '1';
	CTS_tb  <= '0';
	DAV_tb  <= '0';
	D_tb <= "0000";
	wait for MCLK_PERIOD*2;
	
	--SE -> Saidas Esperadas
--Wreg = '0'
--DAC = '0'
--Q = "UUUU"
	
	--Escrever na Ram
	RESET_tb  <= '0';
	DAV_tb  <= '1';
	D_tb <= "0001";
	wait for MCLK_PERIOD*3;
	
	--SE
--Wreg = '0'
--DAC = '1'
--Q = "UUUU"

   --Encher a Ram
    
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1011";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1011";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1000";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	DAV_tb <= '1';
	D_tb <= "1000";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
	--Aqui a Ram já encheu
	--o próximo valor deverá
	--substituir o valor
	--no primeiro ídice
	--da Ram
	
	DAV_tb <= '1';
	D_tb <= "1001";
	wait for MCLK_PERIOD;
	
	DAV_tb <= '0';
	wait for MCLK_PERIOD;
	
   --Ler da Ram
	CTS_tb  <= '1';
	DAV_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '1';
	wait for MCLK_PERIOD;
	
	CTS_tb  <= '0';
	wait for MCLK_PERIOD;
	
	
	
	
	
	
	
	
	 
  wait;
end process;

end  architecture;
