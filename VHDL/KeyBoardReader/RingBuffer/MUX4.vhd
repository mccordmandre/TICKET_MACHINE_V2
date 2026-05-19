library ieee;
use ieee.std_logic_1164.all;

entity MUX4 is
    port (
        A : in  STD_LOGIC_VECTOR(3 downto 0);
        B : in  STD_LOGIC_VECTOR(3 downto 0);
        S : in  STD_LOGIC;
        Y : out STD_LOGIC_VECTOR(3 downto 0)
    );
end MUX4;

architecture LogicFunction of MUX4 is
begin
    Y <= A when (S = '0') else B;
end LogicFunction;