package foo;


import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.MemoryHandler;


public final
class CustomMemoryHandler
    extends MemoryHandler
{

  public
  CustomMemoryHandler(
      final Handler target,
      final int size,
      final Level pushlevel )
  {
    super( target, size, pushlevel );
  }
  
  
  
//  @Override
//  public void
//  close()
//      throws SecurityException
//  {
//    super.push();
//    super.flush();
//    super.close();
//  }

}
