library ieee;

use ieee.std_logic_1164.all;

entity Cont is
  port(
   
	--Input Port
     
	 dataIn : in std_logic_vector(3 downto 0);
	 PL, CE, CLK, Clear, Reset : in std_logic;
    
	--Output Port
     
	  Q3, Q2, Q1, Q0: out std_logic);
	  
  
  end Cont;

architecture structural of Cont is



component ClkDiv is
  port(

  --Input Port
    
	clk_in: in std_logic; 
    
  --Output Port
    
	 clk_out: out std_logic);

end component;



component CounterDown is
port(

   --Input Port
     
	  Clk, PL, CE, Reset: in std_logic;
	  dataIn: in std_logic_vector(3 downto 0);
    
	 --Output Port
     
	  Q: out std_logic_vector(3 downto 0));
	 
end component;



component F0 is
port(

    --Input ports
      
	   A: in std_logic_vector(3 downto 0);
    
	 --Output port
    
		TC : out std_logic);
		
end  component;



component decoderHex is
port(

   --Input port
	
	  A: in std_logic_vector(3 downto 0);
     Clear : in std_logic;
	
	--Output port
	
     HEX0 : out std_logic_vector(7 downto 0));	

  
end component;

signal fioClkCD: std_logic;
signal fioCDSD: std_logic_vector(3 downto 0);
signal fioSD : std_logic_vector(7 downto 0);


begin

U1 : ClkDiv  port map ( clk_in => CLK, clk_out => fioClkCD);
       
U2 : CounterDown port map ( dataIn => dataIn, Clk => fioClkCD , PL => PL , CE => CE, Q => fioCDSD, Reset => Reset);

U3 : F0 port map ( A => fioCDSD  , TC => TC );

U4 : decoderHex port map ( A => fioCDSD , Clear => Clear , HEX0 => SEG7);


CountValue <= fioCDSD ;

end structural;