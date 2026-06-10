library ieee;
use ieee.std_logic_1164.all;


entity RingBuffer is
   port(
	
     --Input port
		  
     CTS, DAV, RESET, CLK : in std_logic;
	  D : in std_logic_vector (3 downto 0);
	  
            
     --Output port
		  
     Wreg, DAC : out std_logic;
	  Q : out std_logic_vector (3 downto 0)); 
		  
end RingBuffer;


architecture structural of RingBuffer is

component RB_Control is
   port(
        --Input port

        DAV, CTS, Full, Empty, CLK, Reset: in std_logic;

        -- Output port
        Wr, selPG, Wreg, DAC, incPut, incGet : out std_logic);
		  	  
end component;

component MA_Control is
   port(
	
     --Input port
		  
     putget, incPut, incGet, CLK, reset : in std_logic;
            
     --Output port
		  
	  A : out std_logic_vector(3 downto 0);  
     full, empty : out std_logic); 
		  
		  	  
end component;

component RAM is
  generic(
		ADDRESS_WIDTH : natural := 4;
		DATA_WIDTH : natural := 4
	);
	port(
		address : in std_logic_vector(ADDRESS_WIDTH - 1 downto 0);
		wr: in std_logic;
		din: in std_logic_vector(DATA_WIDTH - 1 downto 0);
		dout: out std_logic_vector(DATA_WIDTH - 1 downto 0)
	);
	  
  
end component;

	

signal MA_RAM: std_logic_vector(3 downto 0);
signal F_full, F_empty, F_Wr, F_selPG, F_incPut, F_incGet : std_logic;




begin

U1 : RB_Control port map (CLK => CLK, reset => RESET, DAV => DAV, CTS => CTS, Full => F_full, Empty => F_empty, Wr => F_Wr, selPG => F_selPG, Wreg => Wreg, DAC => DAC, incPut => F_incPut, incGet => F_incGet);

U2 : MA_Control port map ( CLK => CLK, reset => RESET, putget => F_selPG, incPut => F_incPut, incGet => F_incGet, A => MA_RAM, full => F_full, empty => F_empty);

U3 : RAM port map ( address => MA_RAM, wr => F_Wr, din => D, dout => Q);


end structural;