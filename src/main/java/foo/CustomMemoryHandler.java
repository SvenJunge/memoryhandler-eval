package foo;


import java.util.logging.MemoryHandler;


public final
class CustomMemoryHandler
    extends MemoryHandler
{

  @Override
  public void
  close()
      throws SecurityException
  {
    super.push();
    super.flush();
    super.close();
  }

}
