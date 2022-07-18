package log;


import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.MemoryHandler;
import java.util.logging.SimpleFormatter;


public final
class ForkingMemoryHandler
  extends MemoryHandler
{
  private static final int FILTER_THRESHOLD = Level.FINE.intValue();
  
  public static ForkingMemoryHandler
  createFrom(
    final Handler target,
    final int size,
    final Level pushLevel )
  {
    Objects.requireNonNull( target );
    Objects.requireNonNull( pushLevel );
    if ( size < 1 ) throw new IllegalArgumentException( "Size is not in range: " + size );
    
    final var forkHandler = new ForkHandler( target, size );
    final var forkMemoryHandler = new ForkingMemoryHandler( forkHandler, size, pushLevel );
    //forkMemoryHandler.setFilter( e -> true );
    forkMemoryHandler.setFilter( logRecord ->
                                   ( logRecord.getLevel().intValue() >= FILTER_THRESHOLD ) );
    
    return forkMemoryHandler;
  }
  
  ForkingMemoryHandler(
    final ForkHandler target,
    final int size,
    final Level pushLevel )
  {
    super( target, size, pushLevel );
  }
  
  /* TODO: Frage: bei mehreren Handlern eines Loggers -> wird immer der selbe LogRecord eingereicht?
           oder sind es verschiedene Objekte (-> synergie des init) ?
           Handler-To-MultiHandler -> Wrapper/ Bridge? (Anzahl der inits und formats!) */
  @Override
  public /* synchronized */ void
  publish( final LogRecord logRecord )
  {
    super.publish( logRecord );
  }
  
  @Override
  public /* synchronized */ void
  push()
  {
    super.push();
    super.flush();
  }
  
  
  /* Um via config erstellt werden zu können, müsste es ein MemoryHandler sein (wg. des target-mechanismuses) */
  static final
  class ForkHandler
    extends Handler
  {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // TODO: deamonize (?)
    
    private final Handler target;
    private final ArrayList<LogRecord> records;
    
    public
    ForkHandler(
      final Handler target,
      final int size )
    {
      Objects.requireNonNull( target );
      if ( size < 1 ) throw new IllegalArgumentException( "Size is not in range: " + size );
      
      this.target = target;
      this.records = new ArrayList<>( size );
    }
    
    @Override
    public /* synchronized */ void
    publish( final LogRecord logRecord )
    {
      /* An dieser Stelle muss getSourceClassName() / getSourceMethodName() aufgerufen werden.
       * Hintergrund ist, dass im 'LogRecord' via inferCaller() eine Auflösung erfolgt, die bei
       * der Übergabe an einen Executor sp nicht mehr funktioniert und dann für die Methoden
       * 'NULL' liefert, was Folgefehler provozieren kann (-höchstwahrscheinlich!-). */
      logRecord.getSourceClassName();
      
      synchronized ( this.records )
      {
        /* Hier könnte Performance kommen, wenn direkt das Array aus dem MemHdlr käme...
         * Dazu müsste man seine push-methode evtl. runter massieren und reinhacken. */
        this.records.add( logRecord );
      }
    }
    
    @Override
    public void
    flush()
    {
      final ArrayList<LogRecord> oldRecords;
      synchronized ( this.records )
      {
        oldRecords = new ArrayList<>( this.records );
        this.records.clear();
      }
      
      this.executorService
        .execute( () -> oldRecords.forEach( this.target::publish ) );
    }
    
    @Override
    public void
    close()
    {
      target.close();
    }
    
  }
}
























