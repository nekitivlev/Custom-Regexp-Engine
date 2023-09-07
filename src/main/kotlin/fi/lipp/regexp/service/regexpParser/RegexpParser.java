package fi.lipp.regexp.service.regexpParser;

import fi.lipp.regexp.model.InvalidPatternException;

public class RegexpParser {
  private final RegexpLexer lexer;
  private RegexpToken token;

  public RegexpParser(RegexpLexer pLexicalAnalyzer) {
    lexer = pLexicalAnalyzer;
  }

  public Node analyze() {
    readNextToken();

    if (token == null) {
      throw new InvalidPatternException();
    }
    Node root = createNode(RegexpToken.ROOT);

    Node n = productRegexp();

    addChildNode(root, n);

    if (token != null) {
      throw new InvalidPatternException();
    }

    return root;

  }

  private void readNextToken() {

    if (lexer.readToken()) {
      token = lexer.getToken();
    } else {
      token = null;
    }
  }

  private boolean match(RegexpToken... pToken) {

    for (RegexpToken t : pToken) {
      if (token == t) {
        return true;
      }
    }
    return false;
  }

  private void consume(RegexpToken pToken) {
    if (token == null || !token.equals(pToken)) {
      throw new InvalidPatternException();
    }
    readNextToken();
  }

  private void consume(String c) {
    String tokenValue = lexer.getTokenValue();
    if (!c.equals(tokenValue)) {
      throw new InvalidPatternException();
    }
    readNextToken();
  }

  private Node createNode(RegexpToken pToken) {

    Node n = new Node(pToken);
    n.setValue(lexer.getTokenValue());
    return n;
  }

  private void addChildNode(Node pParent, RegexpToken pToken) {
    Node childNode = createNode(pToken);
    pParent.addChild(childNode);
  }

  private void addChildNode(Node root, Node n) {
    root.addChild(n);
  }
  private Node productRegexp() {
    Node node = createNode(RegexpToken.REGEXP);

    if (match(RegexpToken.CHARACTER, RegexpToken.ESCAPE, RegexpToken.L_BRACKET, RegexpToken.L_PARENTHESIS)) {
      Node n = productQuantifiedExpression();
      addChildNode(node, n);
    } else {
      throw new InvalidPatternException();
    }

    while (match(RegexpToken.CHARACTER, RegexpToken.ESCAPE, RegexpToken.L_BRACKET, RegexpToken.L_PARENTHESIS)) {
      Node n = productQuantifiedExpression();
      addChildNode(node, n);
    }



    return node;
  }

  private Node productQuantifiedExpression() {

    Node node = createNode(RegexpToken.QUANTIFIED_EXPR);

    Node n = productExpression();
    addChildNode(node, n);

    if (match(RegexpToken.QUANTIFIER)) {
      addChildNode(node, RegexpToken.QUANTIFIER);
      consume(RegexpToken.QUANTIFIER);
    }
    return node;
  }

  private Node productExpression() {

    Node node = createNode(RegexpToken.EXPRESSION);
    if (match(RegexpToken.CHARACTER, RegexpToken.ESCAPE, RegexpToken.L_BRACKET)) {

      Node n = productSelector();
      addChildNode(node, n);

    } else if (match(RegexpToken.L_PARENTHESIS)) {

      Node nodeGroup = createNode(RegexpToken.GROUP);

      consume(RegexpToken.L_PARENTHESIS);
      Node regex = productRegexp();
      consume(RegexpToken.R_PARENTHESIS);

      addChildNode(nodeGroup, regex);
      addChildNode(node, nodeGroup);
    }
    return node;

  }

  private Node productSelector() {

    Node node = createNode(RegexpToken.SELECTOR);

    if (match(RegexpToken.CHARACTER, RegexpToken.ESCAPE)) {
      Node n = productSymbol();
      addChildNode(node, n);
    } else if (token == RegexpToken.L_BRACKET) {
      Node n = productRange();
      addChildNode(node, n);

    }
    return node;
  }

  private Node productRange() {

    Node node = createNode(RegexpToken.RANGE);

    consume(RegexpToken.L_BRACKET);

    if (match(RegexpToken.ESCAPE, RegexpToken.CHARACTER)) {

      Node charRange = productCharRange();
      addChildNode(node, charRange);

    } else {
      throw new InvalidPatternException();
    }
    consume("]");

    return node;
  }

  private Node productCharRange() {
    Node node = createNode(RegexpToken.CHAR_RANGE);

    if (lexer.getTokenValue().equals("]")) {
      throw new InvalidPatternException();
    }

    while (!lexer.getTokenValue().equals("]")) {
      addChildNode(node, RegexpToken.CHARACTER);
      consume(token);
    }

    return node;
  }

  private Node productSymbol() {

    Node node = null;

    if (match(RegexpToken.CHARACTER)) {
      node = createNode(token);
      consume(RegexpToken.CHARACTER);
    } else if (match(RegexpToken.ESCAPE)) {
      node = createNode(token);
      consume(RegexpToken.ESCAPE);
    }

    return node;
  }


}
