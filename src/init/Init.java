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
      objectDirectory.mkdirs();
      headDirectory.mkdirs();
      head.createNewFile();

      System.out.println("jit repo initialized");
      
    } catch (IOException e) {
      System.err.println("error: failed to initialize git repo");
      e.printStackTrace();
    }
  }
} 
