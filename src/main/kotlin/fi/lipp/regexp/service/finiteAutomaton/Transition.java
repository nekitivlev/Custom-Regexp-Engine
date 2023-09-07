package fi.lipp.regexp.service.finiteAutomaton;

public interface Transition {
  boolean match(char character);

  void setNextState(State pState);

  State getNextState();

  Object clone() throws CloneNotSupportedException;
}
