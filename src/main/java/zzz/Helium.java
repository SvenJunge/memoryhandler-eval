package zzz;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public final
class Helium
{
  private static final Logger LOG = Logger.getLogger( Helium.class.getName() );
  static
  {
    System.out.println( "Helium.class.getName() = " + Helium.class.getName() );
    System.out.println( "LOG.getName() = " + LOG.getName() );
    LogManager.getLogManager().getLoggerNames()
      .asIterator().forEachRemaining( s -> System.out.println( " * logger: " + s ) );
  }
  
  
  static void
  doStaticStuff()
  {
    System.out.println( "-::doingStaticStuff()" );

    LOG.finest( () -> "s-finest" );
    LOG.finer( () -> "s-finer" );
    LOG.fine( () -> "s-fine" );
    LOG.config( () -> "s-config" );
    LOG.info( () -> "s-info" );
    LOG.warning( () -> "s-warning" );
    LOG.severe( () -> "s-severe" );

  }
  
  void
  doStuff()
  {
    System.out.println( "-::doingStuff()" );
    final var log = Logger.getLogger( Helium.class.getName() );
    log.finest( () -> "finest" );
    log.finer( () -> "finer" );
    log.fine( () -> "fine" );
    log.config( () -> "config" );
    log.info( () -> "info" );
    log.warning( () -> "warning" );
    log.severe( () -> "severe" );
    
  }
  
  
  public static void
  main( final String[] args ) throws IOException
  {
//    LOG.setLevel( null );
    System.out.println( "-::before" );
    Helium.doStaticStuff();
    new Helium().doStuff();


    try ( final InputStream is = Helium.class.getClassLoader()
                                             .getResourceAsStream( "logging.properties" ) )
    //                                         .getResourceAsStream( "logging.properties.customformat" ) )
    {
      LogManager.getLogManager()
                .readConfiguration( is );
    }
    
//    final var consoleHandler = new ConsoleHandler();
//    consoleHandler.setLevel( Level.ALL );
//    Logger.getLogger( "" )
//          .addHandler( consoleHandler );
    
  
    
    System.out.println( "-::after" );
    Helium.doStaticStuff();
    new Helium().doStuff();
  }
  
  
  
}
























