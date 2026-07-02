library ieee;
use ieee.std_logic_1164.all;

entity KeyControl is
    port(
        --Input port
		  
        CLK, Kack, Kpress, Reset: in std_logic;
        -- Tdelay : in std_logic(1 downto 0);
            
        -- Output port
		  
        Kval, Kscan: out std_logic);   
		  
end KeyControl;


architecture behavioral of KeyControl is

type STATE_TYPE is (STATE_SCANNING, STATE_REGISTERING, STATE_WAITING_RELEASE);

signal CurrentState, NextState : STATE_TYPE;


begin

-- Flip-Flop'
CurrentState <= STATE_SCANNING when Reset = '1' else NextState when rising_edge(CLK);


-- Generate Next State
GenerateNextState:

process (CurrentState, Kack, Kpress)
    begin 
        case CurrentState is
            when STATE_SCANNING        =>    if (Kack = '0' and Kpress = '1') then
                                            NextState <= STATE_REGISTERING;
                                        else 
                                            NextState <= STATE_SCANNING;
                                        end if;


            when STATE_REGISTERING    =>    if (Kack = '1') then
                                            NextState <= STATE_WAITING_RELEASE;
                                        else 
                                            NextState <= STATE_REGISTERING;
                                        end if;


            when STATE_WAITING_RELEASE =>  if (Kpress = '0') then
                                            NextState <= STATE_SCANNING;
                                        else 
                                            NextState <= STATE_WAITING_RELEASE;
                                        end if;
        end case;
    end process;    
    
    -- Generate outputs

    Kscan <= '1' when ((CurrentState = STATE_SCANNING))
            else '0';
    Kval <= '1' when ((CurrentState = STATE_REGISTERING))
            else '0';

end behavioral;