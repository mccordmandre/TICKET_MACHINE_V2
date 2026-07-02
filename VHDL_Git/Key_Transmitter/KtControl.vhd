library ieee;
use ieee.std_logic_1164.all;

entity KtControl is
    port(
        --Input port

        Load, Enviado, Reset, CLK: in std_logic;

        -- Output port
        KBfree, Reset_Counter, Enable_Send: out std_logic);

end KtControl;


architecture behavioral of KtControl is

type STATE_TYPE is (WAITING_LOAD, SENDING);

signal CurrentState, NextState : STATE_TYPE;


begin

-- Flip-Flop'
CurrentState <= WAITING_LOAD when Reset = '1' else NextState when rising_edge(CLK);


-- Generate Next State
GenerateNextState:

process (CurrentState, Load, Enviado)
    begin 
        case CurrentState is
            when WAITING_LOAD        =>    if (Load = '1') then
                                            NextState <= SENDING;
                                        else 
                                            NextState <= WAITING_LOAD;
                                        end if;
													 
            when SENDING     =>      if(Enviado = '1'and Load = '0') then
                                            NextState <= WAITING_LOAD;
                                        else
                                            NextState <= SENDING;													 end if;	  
           
        end case;
    end process;

    -- Generate outputs

    Reset_Counter <= '1' when ((CurrentState = WAITING_LOAD))
            else '0';
    KBfree <= '1' when ((CurrentState = WAITING_LOAD))
            else '0';
    Enable_Send <= '1' when ((CurrentState = SENDING))
            else '0';

end behavioral;