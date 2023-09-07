package fi.lipp.regexp.service.finiteAutomaton;

public class TransitionRange extends TransitionBase implements Cloneable{
  private char initChar;
  private char endChar;

  public TransitionRange(char initChar, char endChar) {
    super();
    this.initChar = initChar;
    this.endChar = endChar;
    if (initChar == endChar) {
      setRepresentation("'" + String.valueOf(initChar) + "'");
    } else {
      setRepresentation(String.valueOf(initChar) + "-" + String.valueOf(endChar));
    }
  }

  @Override
  public boolean match(char character) {

    return (character >= initChar && character <= endChar);
  }

  @Override
  public Object clone() throws CloneNotSupportedException {

    TransitionRange t = new TransitionRange(initChar, endChar);

    return t;
  }

}
