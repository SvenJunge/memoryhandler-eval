package foo;


import java.util.logging.Formatter;
import java.util.logging.LogRecord;


public final
class CustomFormatter
    extends Formatter
{

  @Override
  public String
  format( final LogRecord record )
  {
    System.out.println( "working" );
    return record.getLevel() + ":\t\t" + record.getMessage() + System.lineSeparator();
  }

}
