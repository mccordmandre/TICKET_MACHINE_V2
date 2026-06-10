library ieee;
use ieee.std_logic_1164.all;


entity Keyboard_Reader is
  port(
    
	--Input port
	    
    TXclk, CLK, RESET: in std_logic;
	 L : in std_logic_vector(3 downto 0);
  --Tdelay : in std_logic_vector(1 downto 0); 
  
	 --Output port
	 
	 C : out std_logic_vector(3 downto 0);
	 TXd : out std_logic);
		  
end Keyboard_Reader;


architecture structural of Keyboard_Reader is

component KeyDecode is
   port(
	
     --Input port
		  
	  --Tdelay : in std_logic(1 downto 0);  
     CLK, Kack, Reset : in std_logic;
	  L : in std_logic_vector (3 downto 0);
            
     --Output port
		  
     Kval : out std_logic;
     K, C : out std_logic_vector (3 downto 0));
		  	  
end component;

component RingBuffer is
    port(
	
     --Input port
		  
     CTS, DAV, RESET, CLK : in std_logic;
	  D : in std_logic_vector (3 downto 0);
	  
            
     --Output port
		  
     Wreg, DAC : out std_logic;
	  Q : out std_logic_vector (3 downto 0)); 
		  
		  	  
end component;

component KeyTransmitter is
  port(
	
     --Input port
		  
     Load, TXclk, CLK, RESET : in std_logic;
	  D : in std_logic_vector (3 downto 0);
	  
            
     --Output port
		  
     TXd, KBfree : out std_logic); 
	  
  
end component;

	

signal Kval_DAV, DAC_Kack, KBfree_CTS, Wreg_Load : std_logic;
signal K_D, Q_D : std_logic_vector(3 downto 0);




begin

U1 : KeyDecode port map (CLK => CLK, Reset => RESET, Kack => DAC_Kack, L => L, Kval => Kval_DAV, K => K_D, C => C);

U2 : RingBuffer port map ( RESET => RESET, CLK => CLK, CTS => KBfree_CTS, DAV => Kval_DAV, D => K_D, Wreg => Wreg_Load, DAC => DAC_Kack, Q => Q_D);

U3 : KeyTransmitter port map (CLK => CLK, RESET => RESET, Load => Wreg_Load, TXclk => TXclk, D => Q_D, TXd => TXd, KBfree => KBfree_CTS);


end structural;