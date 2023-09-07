package fi.lipp.regexp.service.finiteAutomaton;
import fi.lipp.regexp.model.InvalidPatternException;
import fi.lipp.regexp.service.regexpParser.Node;
import fi.lipp.regexp.service.regexpParser.RegexpToken;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class FiniteAutomatonBuilder {
  public static FiniteAutomaton generateFromFiniteAutomatonNode(Node node) {

    if (node == null || node.isEmpty()) {
      throw new InvalidPatternException();
    }

    return new FiniteAutomatonBuilder().generateNFA(node);
  }
  public static FiniteAutomaton generateDFAFromNFA(FiniteAutomaton pNDAutomaton) {

    return new FiniteAutomatonBuilder().generateDFA(pNDAutomaton);

  }

  private FiniteAutomaton generateNFA(Node node) {
    Node rootRegexNode = node.getChildren().iterator().next();
    StatePair statePair = faBuildRegexp(rootRegexNode);
    statePair.getFirstState().setInitial(true);
    statePair.getSecondState().setEnd(true);
    FiniteAutomaton nfa = new FiniteAutomaton(statePair.getFirstState());
    return nfa;
  }

  public FiniteAutomaton generateDFA(FiniteAutomaton pNDAutomaton) {

    List<SubSetState> DFAState = new LinkedList<SubSetState>();

    SubSetState S1 = epsilonClosure(pNDAutomaton.getInitialStateFiniteAutomaton());

    S1.setInitial(true);

    DFAState.add(S1);

    SubSetState subSetState = selectState(DFAState);

    while (subSetState != null) {

      for (Transition t : pNDAutomaton.getAlphabetOfFiniteAutomaton()) {

        if (!(t instanceof TransitionEmpty)) {
          SubSetState moveState = epsilonMove(subSetState, t);
          if (!moveState.isEmpty()) {
            SubSetState moCloseState = epsilonClosure(moveState);

            SubSetState euqlState = findState(DFAState, moCloseState);
            if (euqlState == null) {
              DFAState.add(moCloseState);
            } else {
              moCloseState = euqlState;
            }
            Transition afdTransition;
            try {
              afdTransition = (Transition) t.clone();
            } catch (CloneNotSupportedException e) {
              throw new RuntimeException("Fail copying transition");
            }
            subSetState.getState().connect(afdTransition, moCloseState.getState());

          }
        }
      }
      subSetState.setMark(true);
      subSetState = selectState(DFAState);

    }
    FiniteAutomaton dfa = new FiniteAutomaton(S1.getState());
    return dfa;
  }
  private StatePair faBuildRegexp(Node pNode) {

    StatePair headStatePair = null;

    Queue<StatePair> statePairsQueue = new LinkedList<StatePair>();

    for (Node childNode : pNode.getChildren()) {

      if (childNode.getType().equals(RegexpToken.QUANTIFIED_EXPR)) {

        StatePair newExpressionPair = faBuildQuantifiedExpression(childNode);
        statePairsQueue.add(newExpressionPair);

      } else if (childNode.getType().equals(RegexpToken.REGEX)) {

        StatePair regexpPair = faBuildRegex(childNode, statePairsQueue);

        StatePair orStatePair = null;
        headStatePair = statePairsQueue.poll();
        orStatePair = headStatePair;
        while (!statePairsQueue.isEmpty()) {
          StatePair newStatePair = statePairsQueue.poll();
          orStatePair.getSecondState().connect(createEmptyTransition(), newStatePair.getFirstState());
          orStatePair = newStatePair;
        }


        State q0 = new State();
        State q1 = new State();
        q0.connect(createEmptyTransition(), headStatePair.getFirstState());

        q0.connect(createEmptyTransition(), regexpPair.getFirstState());

        regexpPair.getSecondState().connect(createEmptyTransition(), q1);
        orStatePair.getSecondState().connect(createEmptyTransition(), q1);
        StatePair pipeStatePair = new StatePair(q0, q1);
        statePairsQueue.add(pipeStatePair);
      }
    }

    headStatePair = statePairsQueue.poll();
    StatePair tmpPair = headStatePair;

    while (!statePairsQueue.isEmpty()) {

      StatePair pair = statePairsQueue.poll();

      tmpPair.getSecondState().connect(createEmptyTransition(), pair.getFirstState());
      tmpPair = pair;
    }

    headStatePair.setSecondState(tmpPair.getSecondState());

    return headStatePair;
  }

  private StatePair faBuildRegex(Node regexNode, Queue<StatePair> statePairsQueue) {

    Iterator<Node> childNodes = regexNode.getChildren().iterator();

    childNodes.next();
    Node regexpNode = childNodes.next();

    return faBuildRegexp(regexpNode);

  }

  private StatePair faBuildQuantifiedExpression(Node quantifiedExpression) {

    StatePair headStatePair = null;
    StatePair expressionPair = null;

    for (Node child : quantifiedExpression.getChildren()) {

      if (child.getType() == RegexpToken.EXPRESSION) {

        StatePair newExpressionPair = faBuildExpression(child);

        if (expressionPair != null) {
          Transition t = createEmptyTransition();
          expressionPair.getSecondState().connect(t, newExpressionPair.getFirstState());

        }
        expressionPair = newExpressionPair;

      } else {
        if (child.getValue().equals("+")) {

          Transition t = createEmptyTransition();

          expressionPair.getSecondState().connect(t, expressionPair.getFirstState());

        } else if (child.getValue().equals("*")) {

          Transition t = createEmptyTransition();
          expressionPair.getSecondState().connect(t, expressionPair.getFirstState());

          State q0 = new State();
          State q1 = new State();

          q0.connect(createEmptyTransition(), expressionPair.getFirstState());

          expressionPair.getSecondState().connect(createEmptyTransition(), q1);
          q0.connect(createEmptyTransition(), q1);
          expressionPair.setFirstState(q0);
          expressionPair.setSecondState(q1);

        } else {
          expressionPair.getFirstState().connect(createEmptyTransition(), expressionPair.getSecondState());

        }

      }
      if (headStatePair == null) {
        headStatePair = expressionPair;
      }
    }

    return headStatePair;

  }

  private StatePair faBuildExpression(Node node) {

    StatePair lastStates = null;

    Node child = node.getChildren().iterator().next();

    if (child.getType() == RegexpToken.SELECTOR) {

      lastStates = faBuildSelector(child);
    } else if (child.getType() == RegexpToken.GROUP) {
      lastStates = faBuildGroup(child);
    }
    return lastStates;
  }

  private StatePair faBuildGroup(Node node) {
    Node regexNode = node.getChildren().iterator().next();
    return faBuildRegexp(regexNode);
  }

  private StatePair faBuildSelector(Node node) {
    Node child = node.getChildren().iterator().next();
    StatePair statePair = null;

    if (child.getType() == RegexpToken.RANGE) {
      statePair = faBuildRange(child);
    } else {
      statePair = faBuildChar(node);
    }
    return statePair;
  }

  private StatePair faBuildChar(Node pNode) {

    char character = pNode.getValue().charAt(0);
    char initChar;
    char endChar;


    initChar = endChar = character;


    Transition t = new TransitionRange(initChar, endChar);
    State init = new State();
    State end = new State();

    init.connect(t, end);

    return new StatePair(init, end);

  }

  private StatePair faBuildRange(Node node) {

    Node child = node.getChildren().iterator().next();
    return faBuildCharRange(child);

  }

  private StatePair faBuildCharRange(Node charRange) {

    Node[] childNodes = new Node[charRange.getChildrenCount()];
    int i = 0;
    for (Node astNode : charRange.getChildren()) {
      childNodes[i++] = astNode;
    }
    i = 0;

    State firstState = new State();
    State secondState = new State();

    StringBuilder excludeChars = null;

    Node firstChildNode = childNodes[0];
    int nChilds = childNodes.length;

    boolean excludeRange = (firstChildNode.getType() != RegexpToken.ESCAPE && firstChildNode.getValue().equals("^"));

    if (excludeRange) {
      excludeChars = new StringBuilder();
      i = 1;
    }

    while (i < childNodes.length) {

      if (i + 2 < nChilds && childNodes[i + 1].getValue().equals("-")
          && childNodes[i + 2].getType() != RegexpToken.ESCAPE) {

        char initChar = childNodes[i].getValue().charAt(0);
        char endChar = childNodes[i + 2].getValue().charAt(0);

        Transition t = null;

        if (excludeRange) {
          t = new TransitionExcludeRange(initChar, endChar);
        } else {
          if (endChar < initChar) {
            throw new InvalidPatternException();
          }
          t = new TransitionRange(initChar, endChar);
        }
        firstState.connect(t, secondState);
        i += 3;

      } else {
        char rangeChar = childNodes[i].getValue().charAt(0);

        if (excludeRange) {
          excludeChars.append(rangeChar);
        } else {
          TransitionRange transitionRange = new TransitionRange(rangeChar, rangeChar);
          firstState.connect(transitionRange, secondState);
        }
        i++;
      }
    }
    if (excludeRange && excludeChars.length() > 0) {
      TransitionExclude transitionExclude = new TransitionExclude(excludeChars.toString().toCharArray());
      firstState.connect(transitionExclude, secondState);
    }

    return new StatePair(firstState, secondState);
  }

  private SubSetState epsilonClosure(State state) {

    Set<State> closureSet = new HashSet<State>();
    epsilonClosure(state, closureSet, createEmptyTransition());

    SubSetState subSetState = new SubSetState(closureSet);
    subSetState.getState().setEnd(verifyFinalStates(subSetState));

    return subSetState;

  }

  private void epsilonClosure(State state, Set<State> closureSet, Transition sourceTransition) {

    if (state.getTransitions() != null) {
      for (Transition t : state.getTransitions()) {
        if (sourceTransition.equals(t)) {
          epsilonClosure(t.getNextState(), closureSet, sourceTransition);
        }
      }
    }

    closureSet.add(state);

  }

  private SubSetState selectState(List<SubSetState> DFAstates) {

    for (SubSetState subSetState : DFAstates) {

      if (!subSetState.isMark()) {
        return subSetState;
      }
    }
    return null;
  }

  private SubSetState epsilonMove(SubSetState pSubSetState, Transition t) {

    Set<State> moveStates = new HashSet<State>();
    for (State state : pSubSetState.getStates()) {

      searchStatesByTransition(state, t, moveStates);
    }

    SubSetState subSetState = new SubSetState(moveStates);

    return subSetState;

  }

  private void searchStatesByTransition(State state, Transition testTransition, Set<State> pEpsilonMoveStates) {

    if (state == null) {
      return;
    }

    if (state.getTransitions() != null) {
      for (Transition t : state.getTransitions()) {
        if (t.equals(testTransition)) {
          pEpsilonMoveStates.add(t.getNextState());
        } else if (t instanceof TransitionEmpty) {
          searchStatesByTransition(t.getNextState(), testTransition, pEpsilonMoveStates);
        }

      }
    }
  }

  private SubSetState epsilonClosure(SubSetState subSetState) {

    Set<State> states = new HashSet<State>();

    for (State state : subSetState.getStates()) {
      states.addAll(epsilonClosure(state).getStates());
    }

    SubSetState closureSubSet = new SubSetState(states);

    closureSubSet.getState().setEnd(verifyFinalStates(closureSubSet));

    return closureSubSet;
  }

  private boolean verifyFinalStates(SubSetState subSetState) {
    for (State state : subSetState.getStates()) {
      if (state.isEnd()) {
        return true;
      }
    }
    return false;
  }

  private SubSetState findState(List<SubSetState> DFAstates, SubSetState closureState) {
    for (SubSetState subSetState : DFAstates) {
      if (subSetState.equals(closureState)) {
        return subSetState;
      }
    }
    return null;
  }

  private Transition createEmptyTransition() {
    return new TransitionEmpty();
  }
}
