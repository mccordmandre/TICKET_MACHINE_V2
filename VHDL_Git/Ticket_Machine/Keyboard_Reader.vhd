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



component KeyTransmitter is
  port(
	
     --Input port
		  
     Load, TXclk, CLK, RESET : in std_logic;
	  D : in std_logic_vector (3 downto 0);
	  
            
     --Output port
		  
     TXd, KBfree : out std_logic); 
	  
  
end component;

	

signal Kval_Load, KBfree_Kack, inv : std_logic;
signal K_D : std_logic_vector(3 downto 0);




begin

U1 : KeyDecode port map (CLK => CLK, Reset => RESET, Kack => KBfree_Kack, L => L, Kval => Kval_Load, K => K_D, C => C);


U2 : KeyTransmitter port map (CLK => CLK, RESET => RESET, Load => Kval_Load, TXclk => TXclk, D => K_D, TXd => TXd, KBfree => inv);

KBfree_Kack <= not inv;

end structural;