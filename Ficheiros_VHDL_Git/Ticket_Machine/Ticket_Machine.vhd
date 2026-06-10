library ieee;
use ieee.std_logic_1164.all;

entity Ticket_Machine is
    port (
	 
	  --Input port
	  
        CLK, RESET : in std_logic;
        KR_L : in std_logic_vector(3 downto 0);
		  
		--Output port  
		
		  PE_LCD_RS, PE_LCD_E : out std_logic;
        KR_C : out std_logic_vector(3 downto 0);
        HEX0, HEX2, HEX3, HEX4, HEX5, PE_LCD_D : out std_logic_vector(7 downto 0);
        LEDR : out std_logic_vector(9 downto 0)
    );
end entity Ticket_Machine;

architecture structural of Ticket_Machine is

    component UsbPort is
        port (
            inputPort  : in  std_logic_vector(7 downto 0);
            outputPort : out std_logic_vector(7 downto 0)
        );
    end component;

    component Keyboard_Reader is
         port(
    
	--Input port
	    
    TXclk, CLK, RESET: in std_logic;
	 L : in std_logic_vector(3 downto 0);
  --Tdelay : in std_logic_vector(1 downto 0); 
  
	 --Output port
	 
	 C : out std_logic_vector(3 downto 0);
	 TXd : out std_logic);
	 
    end component;

    component PE_LCD is
       port(
	
     --Input port
		   
     LCDsel, SCLK, SDX, reset : in std_logic;
            
     --Output port
		  
     D : out std_logic_vector (7 downto 0);
	  RS, E : out std_logic); 
	  
    end component;

    component PE_TD is
       port(
	
     --Input port
		   
     TDsel, SCLK, SDX, reset : in std_logic;
            
     --Output port
	  O : out std_logic_vector (3 downto 0);
     D : out std_logic_vector (3 downto 0);
	  PRT, RT : out std_logic);

    end component;


    signal usb_in          : std_logic_vector(7 downto 0);
    signal usb_out         : std_logic_vector(7 downto 0);

begin

    U1 : UsbPort port map (inputPort  => usb_in, outputPort => usb_out);

    U2 : Keyboard_Reader port map (
            CLK    => CLK,
            RESET  => RESET,
      --    Tdelay => SW(2 downto 1),   -- SW2=T_DELAY[1], SW1=T_DELAY[0]
            L      => KR_L,
            C      => KR_C,
            TXd    => usb_in(7),
            TXclk  => usb_out(7),
        );

    U3 : PE_LCD
        port map (
            RESET  => RESET,
            LCDsel => usb_out(2),
            SCLK   => usb_out(1),
            SDX    => usb_out(0),
            RS     => LCD_RS,
            RW     => lcd_rw_internal,
            E      => LCD_E,
            D      => LCD_D
        );

    U4 : PE_TD
        port map (
            CLK       => CLK,
            RESET     => RESET,
            TICKETsel => usb_out(3),
            SCLK      => usb_out(1),
            SDX       => usb_out(0),
            Prt       => ticket_prt_s,
            Fn        => ticket_fn,
            HEX0      => HEX0,
            HEX1      => HEX1,
            HEX2      => HEX2,
            HEX3      => HEX3,
            HEX4      => HEX4,
            HEX5      => HEX5
        );

    usb_in(0) <= SW(5);           -- COIN INSERT  (SW5)
    usb_in(1) <= SW(6);           -- COINID[0] (SW6)
    usb_in(2) <= SW(7);           -- COINID[1] (SW7)
    usb_in(3) <= SW(8);           -- COINID[2] (SW8)
    usb_in(4) <= SW(9);           -- COLLECT SW9 up 
                                 
                                  
    usb_in(5) <= key_tx_d_s;
    usb_in(6) <= key_tx_clk_s;
    usb_in(7) <= SW(4);           -- M (SW4)

    -- LEDs
    LEDR(3 downto 0) <= k_s;
    LEDR(4)          <= key_tx_d_s;
    LEDR(5)          <= usb_out(5);           -- ACCEPT
    LEDR(6)          <= usb_out(6);           -- EJECT
    LEDR(7)          <= usb_out(4) or SW(9);  -- COLLECT
    LEDR(8)          <= usb_out(2);           
    LEDR(9)          <= ticket_prt_s;         -- PRT

end architecture structural;
