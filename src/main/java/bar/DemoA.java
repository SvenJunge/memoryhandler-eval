package bar;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


public final
class DemoA
{
  
  public static void
  main( final String[] args )
  {

    final var listA =
      List.of( new Visitor( "Detlev", 10 ),
               new Visitor( "Sandra", 20 ),
               new Visitor( "Claudi", 30 ),
               new Visitor( "George", 40 ),
               //
               new Visitor( "Andrea", 50 ),
               new Visitor( "Gustav", 60 ),
               new Visitor( "Klausi", 70 ),
               new Visitor( "Bärbel", 80 ),
               //
               new Visitor( "Thomas", 90 ),
               new Visitor( "Lucius", 100 ) );
    // 2 groups a 4 ppl + 2 rest -> 3 groups, arr.size 4,4,2
    
    
    final var streamA = listA.stream()
                                                .limit( Long.MAX_VALUE ); // max value for some silly stream-op
//    final var grouper = new Grouper( 4, streamA );
//    final var streamB = grouper.getStream()
//                               .filter( Objects::nonNull ); // doing sth on the stream
    final var streamB = Grouper.FX.apply( streamA )
                                                         .filter( Objects::nonNull );
    
    final List<VisitorGroup> result =
      streamB
        .peek( a ->
               { /* just an empty stream-op */ } )
        .toList();
    
//    // another way
//    final List<VisitorGroup> result =
//      Stream.of( listA.stream() )
//        .flatMap( Grouper.FX )
//        .toList();
    
    
    // result-presentation
    final var counter = new AtomicInteger( 0 );
    result.forEach( aGroup ->
                    {
                      System.out.println( "groupCount = " + ( counter.incrementAndGet() ) );
                      Arrays.stream( aGroup.visitors() )
                            .forEach( System.out::println );
                    } );
  
  }
  
  
  record Visitor(String name, int age) {}
  record VisitorGroup(Visitor[] visitors) {}
  
  static final
  class Grouper
    implements BiConsumer<Visitor, Consumer<VisitorGroup>>
  {
    final Supplier<Visitor> callback =
      () ->
      {
        this.sendToConsumer();
        return null;
      };
    
    final int bufferLimit;
    final ArrayList<Visitor> buffer = new ArrayList<>();
    final Stream<Visitor> callbackStream = Stream.generate( callback )
                                                 .limit( 1L )
                                                 .filter( Objects::nonNull );
    final Stream<Visitor> output;
    
    public Grouper( final int limit, final Stream<Visitor> input )
    {
      this.bufferLimit = limit;
      this.output = Stream.concat( input, callbackStream );
    }
    
    public Stream<VisitorGroup>
    getStream()
    {
      return this.output
               .mapMulti( this );
    }
    
    Consumer<VisitorGroup> visitorGroupConsumer = null;
    @Override
    public
    void accept( final Visitor visitor, final Consumer<VisitorGroup> visitorGroupConsumer )
    {
      if( this.visitorGroupConsumer == null )
      {
        this.visitorGroupConsumer = visitorGroupConsumer;
      }
      
      buffer.add( visitor );
      
      if ( buffer.size() == bufferLimit )
      {
        sendToConsumer();
        buffer.clear();
      }
    }
    
    private void
    sendToConsumer()
    {
      this.visitorGroupConsumer
        .accept( new VisitorGroup( buffer.toArray( new Visitor[0] ) ) );
    }
    
    public static final Function<Stream<Visitor>, Stream<VisitorGroup>> FX =
      (aStream) -> new Grouper( 4, aStream ).getStream();
  }
  
  
  
}
