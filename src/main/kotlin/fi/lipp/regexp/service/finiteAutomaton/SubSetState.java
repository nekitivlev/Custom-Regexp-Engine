package fi.lipp.regexp.service.finiteAutomaton;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SubSetState {
  private boolean mark;

  private Set<State> states = new HashSet<State>();

  private final State state = new State();

  private final String id;

  public SubSetState(Set<State> states) {
    super();
    this.states = states;
    id = buildId();
  }

  private String buildId() {

    int[] ids = new int[states.size()];
    int i = 0;
    for (State s : states) {
      ids[i++] = s.getId();

    }
    Arrays.sort(ids);
    return Arrays.toString(ids);
  }

  public boolean isMark() {
    return mark;
  }

  public void setMark(boolean mark) {
    this.mark = mark;
  }

  public Set<State> getStates() {
    return states;
  }

  public State getState() {
    return state;
  }

  public void setInitial(boolean b) {
    state.setInitial(b);

  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SubSetState other = (SubSetState) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public boolean isEmpty() {

    return states.isEmpty();
  }
}
