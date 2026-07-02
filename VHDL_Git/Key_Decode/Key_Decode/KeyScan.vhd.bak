library ieee;
use ieee.std_logic_1164.all;


entity KeyScan is
  port(
    
	--Input port
	    
    Kscan, CLK, Reset: in std_logic;
	 L : in std_logic_vector(3 downto 0);
	 
	 --Output port
	 
	 C : out std_logic_vector(3 downto 0);
    K : out std_logic_vector(3 downto 0);
	 Kpress : out std_logic);
		  
end KeyScan;


architecture structural of KeyScan is
component MUX4to1 is
  port(
    
	--Input port
	 
	 A: in std_logic_vector( 3 downto 0);   
    S: in std_logic_vector( 1 downto 0);
	 
	 --Output port
	 
     Y : out std_logic);
	  
 end component;
 
 
 
 component Dec2to4 is
  port(
  --Input port

	 S1, S0 : in  std_logic;

  --Output port 

    o : out std_logic_vector(3 downto 0));	

 end component;
 
 
 
 component KD_Cont is
  port(
   
	--Input Port
     
	  CE, CLK, Reset : in std_logic;
    
	--Output Port
     
	  Q: out std_logic_vector);	

 end component;
 

  
signal f_mux: std_logic;
signal counterValue, decValue : std_logic_vector(3 downto 0);


begin


U1 : MUX4to1 port map( A(0) => L(0) ,  A(1) => L(1) , A(2) => L(2), A(3) => L(3), S => counterValue(1 downto 0), Y => f_mux);

U2 : KD_Cont port map( CE => Kscan, CLK => CLK,  Reset => Reset, Q => counterValue);

U3 : Dec2to4 port map( S1 => counterValue(3) , S0 => counterValue(2) , o => decValue);

C <= not decValue;
Kpress <= not f_mux;
K <= counterValue;

end structural;
