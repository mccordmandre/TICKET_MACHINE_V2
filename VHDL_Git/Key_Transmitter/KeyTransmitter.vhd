library ieee;
use ieee.std_logic_1164.all;


entity KeyTransmitter is
   port(
	
     --Input port
		  
     Load, TXclk, CLK, RESET : in std_logic;
	  D : in std_logic_vector (3 downto 0);
	  
            
     --Output port
		  
     TXd, KBfree : out std_logic); 
		  
end KeyTransmitter;


architecture structural of KeyTransmitter is

component KT_Hold_Rg is
   port(
	 
        --input port
        
		  D: in std_logic_vector (3 downto 0);
		  CLK, reset, en: in std_logic;

        -- output port
        Q : out std_logic_vector (3 downto 0));
		  	  
end component;

component MUX8to1 is
   port(
    
	--Input port
	 
	 A: in std_logic_vector( 7 downto 0);   
    S: in std_logic_vector( 2 downto 0);
	 
	 --Output port
	 
    Y : out std_logic);
		  	  
end component;

component KT_Cont is
  port(
   
	--Input Port
     
	  CE, CLK, Reset : in std_logic;
    
	--Output Port
     
	  Q: out std_logic_vector(2 downto 0));
	  
  
end component;

component TerminalCont is
   port(
   
	--Input Port
     
	 A: in std_logic_vector(2 downto 0);
    
	--Output Port
     
	 TC: out std_logic);
	  
  
end component;

component KtControl is
     port(
        --Input port

        Load, Enviado, Reset, CLK: in std_logic;
		  

        -- Output port
        KBfree, Reset_Counter, Enable_Send: out std_logic);

end component;


signal Hrg_Mux: std_logic_vector(3 downto 0);
signal Cont_MuxTC: std_logic_vector(2 downto 0);
signal KtC_MuxCont,  TC_KtC, KtC_KBfree, KtC_Cont : std_logic;
signal inv : std_logic; 



begin

U1 : KT_Hold_Rg port map ( CLK => CLK,  en => Load, reset => RESET, D => D, Q => Hrg_Mux);

U2 : MUX8to1 port map ( A(0) => inv, A(1) => '1', A(5 downto 2) => Hrg_Mux, A(6) => '0', A(7) => '1', S => Cont_MuxTC, Y => TXd);

U3 : KT_Cont port map ( CLK => TXclk, Reset => KtC_Cont, CE => KtC_MuxCont, Q => Cont_MuxTC);

U4 : TerminalCont port map ( A => Cont_MuxTC, TC => TC_Ktc);

U5 : KtControl port map ( CLK => CLK, Enviado => TC_KtC, Load => Load, Reset => RESET, KBfree => KtC_KBfree, Reset_Counter => KtC_Cont, Enable_Send => KtC_MuxCont);

inv <= not KtC_MuxCont;
KBfree <= KtC_KBfree;

end structural;