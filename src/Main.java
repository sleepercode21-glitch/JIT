import init.Init;
import add.Add;

public class Main {
  public static void main(String[] args) {
    Init init = new Init();
    init.execute();

    Add add = new Add();
    add.execute(new String[] {"LICENSE"});
  }
}
