package init;
import java.io.*;

public class Init {
  public Init() {

  }

  public void execute() {
    File directory = new File(".jit/objects");
    if (directory.mkdir()) {
      System.out.println("Jit directory created!");
    } else {
      System.out.println("Failed to create Jit directory");
    }
  }
} 
