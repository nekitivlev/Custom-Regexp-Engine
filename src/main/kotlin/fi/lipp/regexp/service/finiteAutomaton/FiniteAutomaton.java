package fi.lipp.regexp.service.finiteAutomaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class FiniteAutomaton {
  private int counter;
  private State initialStateFiniteAutomaton;
  private final Set<Transition> alphabetOfFiniteAutomaton;
  private final List<State> states;


  public State getInitialStateFiniteAutomaton() {
    return initialStateFiniteAutomaton;
  }

  public Iterable<Transition> getAlphabetOfFiniteAutomaton() {
    return Collections.unmodifiableSet(alphabetOfFiniteAutomaton);
  }

  public FiniteAutomaton() {
    alphabetOfFiniteAutomaton = new HashSet<Transition>();
    states = new ArrayList<State>();
    counter = 0;
  }

  public FiniteAutomaton(State firstState) {
    this();
    initialStateFiniteAutomaton = firstState;
    dfs();
  }


  private void navigateAndPrint(StringBuilder builder, State pState, boolean[] visited) {

    if (pState == null) {
      return;
    }
    visited[pState.getId()] = true;
    builder.append(pState);
    Iterable<Transition> transitions = pState.getTransitions();
    if (transitions != null) {
      for (Transition t : pState.getTransitions()) {
        State nextState = t.getNextState();
        if (!visited[nextState.getId()]) {
          navigateAndPrint(builder, nextState, visited);
        }
      }
    }
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    boolean[] visited = new boolean[counter];
    navigateAndPrint(builder, initialStateFiniteAutomaton, visited);

    return builder.toString();
  }
  private void dfs() {
    Queue<State> stateQueue = new LinkedList<State>();

    stateQueue.add(initialStateFiniteAutomaton);
    initialStateFiniteAutomaton.setId(counter++);

    while (!stateQueue.isEmpty()) {

      State state = stateQueue.poll();
      states.add(state);

      if (state.getTransitions() != null) {
        for (Transition t : state.getTransitions()) {
          alphabetOfFiniteAutomaton.add(t);
          State childState = t.getNextState();
          if (childState.getId() == -1) {
            childState.setId(counter++);
            stateQueue.add(childState);
          }
        }
      }
    }
  }
}
