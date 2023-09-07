package fi.lipp.regexp.service;
import fi.lipp.regexp.service.finiteAutomaton.FiniteAutomaton;
import fi.lipp.regexp.service.finiteAutomaton.State;
import fi.lipp.regexp.service.finiteAutomaton.Transition;
public class Regex {
  private final FiniteAutomaton finiteAutomaton;

  Regex(FiniteAutomaton pFiniteAutomaton) {

    finiteAutomaton = pFiniteAutomaton;

  }

  public boolean match(char[] buffer, RegexMatchResult regexMatcher) {

    boolean stop = false;

    boolean match = false;

    int matchStartPosition = -1;


    int position = 0;

    if (regexMatcher != null) {
      position = regexMatcher.getPos();
    }

    State currentState = finiteAutomaton.getInitialStateFiniteAutomaton();


    while (currentState.hasTransitions() && (position < buffer.length) && !stop) {

      char c = buffer[position];

      Transition transition = matchTransition((char) c, currentState);

      if (transition == null) {

        if (currentState.isEnd()) {
          match = true;
          stop = true;

          if (matchStartPosition == -1) {
            matchStartPosition = position;
          }

        } else {

          currentState = finiteAutomaton.getInitialStateFiniteAutomaton();
          position++;
          matchStartPosition = -1;
        }

      } else {
        currentState = transition.getNextState();

        match = currentState.isEnd();

        if (matchStartPosition == -1) {
          matchStartPosition = position;
        }
        position++;

      }
    }

    if (match) {
      if (regexMatcher != null) {
        regexMatcher.setMatchStartPos(matchStartPosition);

        regexMatcher.setMatchLen(position - matchStartPosition);
      }
    }

    return match;
  }

  private Transition matchTransition(char c, State pState) {

    Iterable<Transition> transitionList = pState.getTransitions();
    if (transitionList != null) {
      for (Transition t : pState.getTransitions()) {
        if (t.match(c)) {
          return t;
        }
      }
    }

    return null;
  }

  @Override
  public String toString() {
    return "digraph g {\n" + finiteAutomaton + "}\n";
  }
}
