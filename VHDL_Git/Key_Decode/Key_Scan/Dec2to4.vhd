library ieee;
use ieee.std_logic_1164.all;


entity Dec2to4 is
  port(
  --Input port

	 S1, S0 : in  std_logic;

  --Output port 

    o : out std_logic_vector(3 downto 0));


  end  Dec2to4;
  
  
  
 architecture structural of Dec2to4 is

 component Dec1to2 is
  port(
  --Input port
    
	 a0,e : in  std_logic;

  --Output port 

    o0,o1 : out std_logic);

  end  component;
  
  
  
signal f_dec1, f_dec2 : std_logic;

  begin
  U1: Dec1to2 port map ( a0 => S1, E => '1', o0 => f_dec1 , o1 => f_dec2);
  
  U2: Dec1to2 port map ( a0 => S0, E => f_dec1, o0 => o(0) , o1 => o(1));
  
  U3: Dec1to2 port map ( a0 => S0, E => f_dec2, o0 => o(2) , o1 => o(3));
  
end architecture;