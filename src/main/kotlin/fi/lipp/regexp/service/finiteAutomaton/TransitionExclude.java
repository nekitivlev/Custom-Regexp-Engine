package fi.lipp.regexp.service.finiteAutomaton;

import java.util.Arrays;

public class TransitionExclude extends TransitionBase implements Cloneable{
  private char[] excluded;

  public TransitionExclude(char[] excluded) {
    super();
    this.excluded = excluded;
    Arrays.sort(excluded);
    String excludedStr = Arrays.toString(excluded);
    setRepresentation("^" + excludedStr.substring(1).substring(0, excludedStr.length() - 2));
  }

  @Override
  public boolean match(char character) {

    if (excluded != null) {

      return !(Arrays.binarySearch(excluded, character) >= 0);
    } else {
      return false;
    }
  }

  @Override
  public Object clone() {
    TransitionExclude t = new TransitionExclude(excluded);
    return t;
  }
}
