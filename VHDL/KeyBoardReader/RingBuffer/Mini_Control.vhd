library ieee;
use ieee.std_logic_1164.all;

entity Mini_Control is
    port(
        --Input port

        IncPut, IncGet, equal ,Reset, CLK: in std_logic;

        -- Output port
        Full, Empty: out std_logic);

end Mini_Control;


architecture behavioral of Mini_Control is

type STATE_TYPE is (ST_Empty, ST_mid, ST_Full);

signal CurrentState, NextState : STATE_TYPE;


begin

-- Flip-Flop'
CurrentState <= ST_Empty when Reset = '1' else NextState when rising_edge(CLK);


-- Generate Next State
GenerateNextState:

process (CurrentState, IncPut, IncGet)
    begin 
        case CurrentState is
            when ST_Empty        =>    if (equal = '1') then
                                            NextState <= ST_Empty;													  
                                        else 												 
                                            NextState <= ST_mid;														 
                                        end if;
													 
            when ST_mid     =>         if (equal = '1'and IncPut = '1') then			
                                            NextState <= ST_Full;	  
                                        elsif (equal = '1'and IncGet = '1') then
                                            NextState <= ST_Empty;
													 else
													     NextState <= ST_mid;     	 
													 end if;	  
													 
			   when ST_Full    =>         if (equal = '1') then
			                                   NextState <= ST_Full;
												    else 
													     NextState <= ST_mid;
													 end if;
		end case;
    end process;

    -- Generate outputs

    Empty <= '1' when ((CurrentState = ST_Empty))
            else '0';
    Full <= '1' when ((CurrentState = ST_Full))
            else '0';

end behavioral;