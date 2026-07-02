library ieee;
use ieee.std_logic_1164.all;


entity KeyDecode is
   port(
	
     --Input port
		  
	  --Tdelay : in std_logic(1 downto 0);  
     CLK, Kack, Reset : in std_logic;
	  L : in std_logic_vector (3 downto 0);
            
     --Output port
		  
     Kval : out std_logic;
     K, C : out std_logic_vector (3 downto 0)); 
		  
end KeyDecode;


architecture structural of KeyDecode is
component KeyScan is
    port(
	 
        -- input port
		  
        Kscan, CLK, Reset : in std_logic;
        L : in std_logic_vector (3 downto 0);

        --Output port
	 
	     C : out std_logic_vector(3 downto 0);
        K : out std_logic_vector(3 downto 0);
	     Kpress : out std_logic);
		  
end component;



component KeyControl is
    port(
	 
        --input port
        CLK, Kack, Kpress, Reset : in std_logic;
        -- Tdelay : in_std_logic( 2 downto 0);

        -- output port
        Kval, Kscan : out std_logic);

end component;

  component CLKDIV is
   generic(div: natural := 50000000);   
  port ( clk_in: in std_logic;
		 clk_out: out std_logic);
end component;


signal f_SC, f_Kp, f_Ks, clk_signal: std_logic;


begin

U1 : KeyScan port map ( CLK => clk_signal,  Kscan => f_Ks, Reset => Reset, Kpress => f_Kp, K => K, L => L, C => C);

U2 : KeyControl port map ( CLK => not clk_signal, Reset => Reset, Kpress => f_Kp, Kscan => f_Ks, Kval => Kval, Kack => Kack);

U3: CLKDIV generic map ( div => 50000) port map ( clk_in => CLK, clk_out => clk_signal);


end structural;