package foo;


import java.io.IOException;
import java.io.InputStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;


public final
class Main
{

  public static void
  main( final String[] args )
      throws InterruptedException
           , IOException
  {
    System.out.println( "Starting ..." );

//    try ( final InputStream is = Main.class.getClassLoader()
//                                           .getResourceAsStream( "logging.properties.nocustomformat" ) )
////                                           .getResourceAsStream( "logging.properties.customformat" ) )
//    {
//      LogManager.getLogManager()
//                .readConfiguration( is );
//    }

    final var consolehandler = new ConsoleHandler();
    consolehandler.setLevel( Level.ALL );
    
    final MemoryHandler aHandler = new CustomMemoryHandler( consolehandler, 10, Level.WARNING );
//    final MemoryHandler aHandler = new CustomMemoryHandler();
    aHandler.setFormatter( new CustomFormatter() );
    
    final Logger log = Logger.getLogger( Main.class.getName() );
//    log.setUseParentHandlers( false );
//    log.addHandler( aHandler );

    for( int i = 1; i <= 3; i++ )
    {
      System.out.println( "run: " + i );
      Thread.sleep( 200L );

      log.config( "config-msg" );
      log.info( "info-msg" );
      if( i % 2 == 0)
      { log.warning( "warning-msg" ); }

      System.out.println( "sleep" );
      Thread.sleep( 650L );
    }

    System.out.println( "-fin. (CustomMemoryHandler.close() should invoke publish+flush)" );
  }

}
