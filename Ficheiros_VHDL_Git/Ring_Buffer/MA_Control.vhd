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

component RB_Cont is
   port(
   
	--Input Port
     
	  En, CLK, reset : in std_logic;
    
	--Output Port
     
	  Q: out std_logic_vector(3 downto 0));
		  	  
end component;

component Mini_Control is
   port(
        --Input port

        putget, equal ,reset, CLK: in std_logic;

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

component Rg_bit is
  port(
   
	--Input port
	
	  A: in std_logic;
	  Clk, Reset, En: in std_logic;
	 
     
	--Output port
	
     Q: out std_logic);
	    
end component;


component Rg_sem_En is
    port(
	 
        --Input port
        A: in std_logic;
        Clk, Reset: in std_logic;

        --Output port
        Q: out std_logic);

end component;






signal RC_MUX, WC_MUX: std_logic_vector(3 downto 0);
signal Equal_MC, fio_teste : std_logic; 



begin

Reader : RB_Cont port map ( CLK => CLK,  en => incGet, reset => reset, Q => RC_MUX);

Writer : RB_Cont port map (CLK => CLK, en => incPut, reset => reset, Q => WC_MUX);

U1 : MUX4 port map ( A => WC_MUX, B => RC_MUX, S => putget, Y => A);

U2 : Equal port map ( A => WC_MUX, B => RC_MUX, Q => Equal_MC);

U3 : Mini_Control port map (reset => reset, CLK => CLK, putget => fio_teste, equal => Equal_MC, Full => full, Empty => empty);

U4 : Rg_sem_En port map(reset => reset, CLK => CLK, A => putget, Q => fio_teste);


end structural;