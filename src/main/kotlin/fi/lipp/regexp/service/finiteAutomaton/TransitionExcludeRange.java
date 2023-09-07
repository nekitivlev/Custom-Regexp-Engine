package fi.lipp.regexp.service.finiteAutomaton;

public class TransitionExcludeRange extends TransitionBase implements Cloneable{
  private char initChar;
  private char endChar;

  public TransitionExcludeRange(char initChar, char endChar) {
    super();
    this.initChar = initChar;
    this.endChar = endChar;
    setRepresentation("^" + String.valueOf(initChar) + "-" + String.valueOf(endChar));
  }

  @Override
  public boolean match(char character) {

    return (character < initChar || character > endChar);
  }

}
