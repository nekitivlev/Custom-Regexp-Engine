package fi.lipp.regexp.service;
import fi.lipp.regexp.service.finiteAutomaton.FiniteAutomaton;
import fi.lipp.regexp.service.finiteAutomaton.FiniteAutomatonBuilder;
import fi.lipp.regexp.service.regexpParser.RegexpLexer;
import fi.lipp.regexp.service.regexpParser.RegexpParser;
import fi.lipp.regexp.service.regexpParser.Node;
public class RegexEngine {
  public static Regex compile(String regexExpr) {
    RegexpLexer lexer = new RegexpLexer(regexExpr);
    RegexpParser parser = new RegexpParser(lexer);
    Node syntaxTree = parser.analyze();
    FiniteAutomaton nFA = FiniteAutomatonBuilder.generateFromFiniteAutomatonNode(syntaxTree);
    FiniteAutomaton dFA = FiniteAutomatonBuilder.generateDFAFromNFA(nFA);
    return new Regex(dFA);
  }

}
