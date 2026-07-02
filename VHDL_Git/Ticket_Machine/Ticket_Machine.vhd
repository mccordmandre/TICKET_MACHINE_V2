library ieee;
use ieee.std_logic_1164.all;

entity Ticket_Machine is
    port (
	 
	  --Input port
	  
        CLK, RESET, CollectTicket, M_in: in std_logic;
        KR_L : in std_logic_vector(3 downto 0);
		  Coin_in: in std_logic;
		  Coinid_in: in std_logic_vector (2 downto 0);
	  -- Tdelay : in std_logic_vector(1 downto 0);
		  
		--Output port  
		  accept_out, collect_out, eject_out, PRT: out std_logic;
        KR_C: out std_logic_vector(3 downto 0);
        HEX0: out std_logic_vector(7 downto 0); 
		  HEX1: out std_logic_vector(7 downto 0); 
		  HEX2: out std_logic_vector(7 downto 0); 
		  HEX3: out std_logic_vector(7 downto 0); 
		  HEX4: out std_logic_vector(7 downto 0); 
		  HEX5: out std_logic_vector(7 downto 0); 
        LCD_D: out std_logic_vector(9 downto 0)

    );
end entity Ticket_Machine;

architecture structural of Ticket_Machine is

    component UsbPort is
        port (
            inputPort  : in  std_logic_vector(7 downto 0);
            outputPort : out std_logic_vector(7 downto 0)
        );
    end component;
	 
	 component Manut is
         port(
	
     --Input port
		
	   M_in	: in std_logic;
            
     --Output port
		  
	   M_out : out std_logic);
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
	 
	 component Coin_Acceptor is
       port(
	
     --Input port
		   
     accept_in, collect_in, eject_in, Coin_in : in std_logic;
	  
	  Coinid_in : in std_logic_vector (2 downto 0);
	   
            
     --Output port
	  
	  accept_out, collect_out, eject_out, Coin_out : out std_logic;
	  
	  Coinid_out : out std_logic_vector (2 downto 0)
	  );  
	  
	  
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
	 
	 
	 component TICKET_DISPENSER is
       port ( 
		 
		 --Input port
		 
		 RT, Prt, CollectTicket: in STD_LOGIC;
		 O, D: in STD_LOGIC_VECTOR(3 downto 0);                                            
		 --Output port
		 
		 Fn: out STD_LOGIC;
		 HEX0, HEX1, HEX2, HEX3, HEX4, HEX5: out STD_LOGIC_VECTOR(7 downto 0) );

    end component;


    signal CLK_signal : std_logic;
	 signal usb_in, usb_out : std_logic_vector (7 downto 0);
	 signal PE_TD_TD : std_logic_vector (9 downto 0);
	 
	 begin

    U1 : UsbPort port map (inputPort  => usb_in, outputPort => usb_out);

	 U2 : Coin_Acceptor port map( 
	         accept_in   => usb_out(4), 
				collect_in  => usb_out(6), 
				eject_in    => usb_out(5), 
				Coinid_in   => Coinid_in, 
				Coin_in     => Coin_in, 
				Coinid_out  => usb_in(2 downto 0), 
				Coin_out    => usb_in(3),
				accept_out  => accept_out, 
				collect_out => collect_out, 
				eject_out   => eject_out
				);
	 
    U3 : Keyboard_Reader port map (
            CLK    => CLK,
            RESET  => RESET,
      --    Tdelay => Tdelay,   -- SW2=T_DELAY[1], SW1=T_DELAY[0]
            L      => KR_L,
            C      => KR_C,
            TXd    => usb_in(7),
            TXclk  => usb_out(7)
        );
		  
	 U4 : PE_LCD port map (
            reset  => RESET,
            LCDsel => usb_out(2),
            SCLK   => usb_out(1),
            SDX    => usb_out(0),
            RS     => LCD_D(0),
            E      => LCD_D(9),
            D      => LCD_D(8 downto 1)
        );
		  
	 U5 : PE_TD port map (
            reset     => RESET,
            TDsel     => usb_out(3),
            SCLK      => usb_out(1),
            SDX       => usb_out(0),
            Prt       => PE_TD_TD(9),
            RT        => PE_TD_TD(0),
				O         => PE_TD_TD(4 downto 1),
				D         => PE_TD_TD(8 downto 5)
			);
			
			
	 U6 : TICKET_DISPENSER port map(
		  
		    RT            => PE_TD_TD(0),
			 Prt           => PE_TD_TD(9),
			 CollectTicket => CollectTicket,
			 O             => PE_TD_TD(4 downto 1),
			 D             => PE_TD_TD(8 downto 5),
			 Fn            => usb_in(4),
			 HEX0          => HEX0,
			 HEX1          => HEX1,
			 HEX2          => HEX2,
			 HEX3          => HEX3,
			 HEX4          => HEX4,
			 HEX5          => HEX5
			 
		  );
		  
	 U7 : Manut port map(  M_in => M_in, M_out => usb_in(6));  
		  
	 PRT <= PE_TD_TD(9);
		  
		  
end architecture structural;
	 