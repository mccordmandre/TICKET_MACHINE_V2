library ieee;
use ieee.std_logic_1164.all;


entity Dec1to2 is
  port(
  --Input port

    a0,e : in  std_logic;

  --Output port 

    o0,o1 : out std_logic);


  end  Dec1to2;
 
architecture structural of Dec1to2 is

  begin
 o0 <= e and not a0;
 o1 <= e and  a0;
end architecture;