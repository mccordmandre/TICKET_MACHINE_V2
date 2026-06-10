library ieee;
use ieee.std_logic_1164.all;

entity Rg_sem_En is
    port(
	 
        --Input port
        A: in std_logic;
        Clk, Reset: in std_logic;

        --Output port
        Q: out std_logic);

end Rg_sem_En;

architecture structural of Rg_sem_En is
component FFD is
    port(
	 
        --Input Port
        CLK, RESET, SET, D, EN : in std_logic;

        --Output Port
        Q : out std_logic);
		  
end component;

begin

U1 : FFD port map ( CLK => Clk, EN => '1', RESET => Reset, SET => '0', D => A, Q => Q);

end structural;