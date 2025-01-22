package dev.emassey0135.audionavigation;

import java.util.Arrays;
import java.util.List;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public class EspeakVoice extends Structure {
  public String name;
  public String languages;
  public String identifier;
  public Byte gender;
  public Byte age;
  public Byte variant;
  public Byte xx1;
  public Integer score;
  public Pointer spare;
  protected List getFieldOrder() {
    return Arrays.asList("name", "languages", "identifier", "gender", "age", "variant", "xx1", "score", "spare");
  }
  public EspeakVoice() {
    super();
  }
  public EspeakVoice(Pointer pointer) {
    super(pointer);
    read();
  }
}
