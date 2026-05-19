library ieee;
use ieee.std_logic_1164.all;


entity MA_Control is
   port(
	
     --Input port
		  
     putget, incPut, incGet, CLK, reset : in std_logic;
            
     --Output port
		  
	  A : out std_logic_vector(3 downto 0);  
     full, empty : out std_logic); 
		  
end MA_Control;


architecture structural of MA_Control is

component Cont is
   port(
   
	--Input Port
     
	  En, CLK, reset : in std_logic;
    
	--Output Port
     
	  Q: out std_logic_vector(3 downto 0));
		  	  
end component;

component Mini_Control is
   port(
        --Input port

        IncPut, IncGet, equal ,reset, CLK: in std_logic;

        -- Output port
        Full, Empty: out std_logic);
		  	  
end component;



component MUX4 is
 port(
    
	--Input port
	 
	  A, B : in std_logic_vector(3 downto 0);
     S : in std_logic;
	 
	 --Output port
	 
     Y : out std_logic_vector(3 downto 0));
  
end component;
	  
component Equal is
  port(
   
	--Input port
	
	  A, B: in std_logic_vector(3 downto 0);
	
	 
	--Output port
	
     Q: out std_logic);
	    
end component;





signal RC_MUX, WC_MUX: std_logic_vector(3 downto 0);
signal Equal_MC, incGet_MC, incPut_MC : std_logic; 



begin

Reader : Cont port map ( CLK => CLK,  en => incGet, reset => reset, Q => WC_MUX);

Writer : Cont port map (CLK => CLK, en => incPut, reset => reset, Q => RC_MUX);

U1 : MUX4 port map ( A => WC_MUX, B => RC_MUX, S => putget, Y => A);

U2 : Equal port map ( A => WC_MUX, B => RC_MUX, Q => Equal_MC);

U3 : Mini_Control port map (reset => reset, CLK => CLK, IncPut => incPut, IncGet => incGet, equal => Equal_MC, Full => full, Empty => empty);


end structural;