package init;
import java.io.*;
import java.io.IOException;

public class Init {
  public Init() {

  }

  public void execute() {
    
    File objectDirectory = new File(".jit/objects");
    File headDirectory = new File(".jit/refs/heads");
    File head = new File(".jit/HEAD");
    

    try {
      boolean objectOk = objectDirectory.mkdirs();
      boolean headsOk = headDirectory.mkdirs();
      boolean headOk = head.createNewFile();

      if (objectOk || headsOk || headOk) {
        System.out.println("jit repo initialized");
      } else {
        System.out.println("failed to initialize jit! repo might already exist!");
      }
    } catch (IOException e) {
      System.err.println("error: failed to initialize git repo");
      e.printStackTrace();
    }
  }
} 
