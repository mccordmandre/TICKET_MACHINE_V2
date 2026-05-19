library ieee;
use ieee.std_logic_1164.all;

entity RB_Control is
    port(
        --Input port

        DAV, CTS, Full, Empty, CLK, Reset: in std_logic;

        -- Output port
        Wr, selPG, Wreg, DAC, incPut, incGet : out std_logic);

end RB_Control;


architecture behavioral of RB_Control is

type STATE_TYPE is (ST_Idle, ST_Read, ST_Write);

signal CurrentState, NextState : STATE_TYPE;


begin

-- Flip-Flop'
CurrentState <= ST_Idle when Reset = '1' else NextState when rising_edge(CLK);


-- Generate Next State
GenerateNextState:

process (CurrentState, DAV, CTS, Full, Empty)
    begin 
        case CurrentState is
            when ST_Idle        =>    if (DAV = '1' and Full = '0') then
                                            NextState <= ST_Write;													  
                                        elsif ((CTS = '1' and Full = '1') or (CTS = '1' and Empty = '0' and DAV = '0')) then												 
                                            NextState <= ST_Read;	
		                                  else
                                            NextState <= ST_Idle;													 
                                        end if;
													 
            when ST_Write     =>         if (DAV = '0') then			
                                            NextState <= ST_Idle;	  
													 else
													     NextState <= ST_Write;     	 
													 end if;	  
													 
			   when ST_Read    =>         if (CTS = '0') then
			                                   NextState <= ST_Idle;
												    else 
													     NextState <= ST_Read;
													 end if;
		end case;
    end process;

    -- Generate outputs

    Wr <= '1' when ((CurrentState = ST_Write))
            else '0';
    selPG <= '1' when ((CurrentState = ST_Read))
            else '0';
	 Wreg <= '1' when ((CurrentState = ST_Read))
            else '0';		
	 incPut <= '1' when ((CurrentState = ST_Idle and DAV = '1' and Full = '0')) 
	         else '0';
    incGet <= '1' when (((CurrentState = ST_Idle and CTS = '1' and Full = '1') or (CurrentState = ST_Idle and CTS = '1' and Empty = '0' and DAV = '0'))) 
	         else '0';
	 DAC <= '1' when ((CurrentState = ST_Write and DAV = '0'))
            else '0';
	 

end behavioral;