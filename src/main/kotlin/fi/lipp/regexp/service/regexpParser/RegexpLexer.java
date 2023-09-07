package fi.lipp.regexp.service.regexpParser;

import fi.lipp.regexp.model.InvalidPatternException;
import java.util.Arrays;

public class RegexpLexer {
  private final String regex;
  private int index;
  private RegexpToken token = null;
  private String character;
  private String tokenValue;
  private final String[] escape_chars = { "(", ")", "*", "+", "-", "?", "[", "\\", "]", "^" };

  private final String[] escape_chars_value = { "(", ")", "*", "+", "-", "?", "[", "\\", "]", "^" };

  public RegexpLexer(String pRegex) {

    Arrays.sort(escape_chars);

    if (pRegex == null) {
      throw new InvalidPatternException();
    }
    regex = pRegex;
    index = 0;
    character = "";
  }

  public boolean readToken() throws InvalidPatternException {
    boolean charRead = readCharacter();
    if (charRead) {
      switch (character) {
        case "[":
          token = RegexpToken.L_BRACKET;
          tokenValue = "[";
          break;
        case "(":
          token = RegexpToken.L_PARENTHESIS;
          tokenValue = "(";
          break;
        case ")":
          token = RegexpToken.R_PARENTHESIS;
          tokenValue = ")";
          break;
        case "+":
        case "*":
        case "?":
          token = RegexpToken.QUANTIFIER;
          tokenValue = character;
          break;
        case "\\":
          verifyEscapeCharacters();
          break;
        default:
          if (isValidChar(character)) {
            token = RegexpToken.CHARACTER;
            tokenValue = String.valueOf(character);
          } else {
            throw new InvalidPatternException();
          }
      }
    }
    return charRead;
  }

  public int getIndex() {
    return index;
  }

  public RegexpToken getToken() {
    return token;
  }

  public String getTokenValue() {
    return tokenValue;
  }

  private boolean isValidChar(String pCharacter) {
    return pCharacter.length() == 1 && pCharacter.charAt(0) >= 32 && pCharacter.charAt(0) <= 126;
  }
  private void verifyEscapeCharacters() {

    if (readCharacter()) {
      int charIndex = findEscapedChar(character);
      if (charIndex >= 0) {
        token = RegexpToken.ESCAPE;
        tokenValue = String.valueOf(escape_chars_value[charIndex]);
      } else {
        throw new InvalidPatternException();
      }
    } else {
      throw new InvalidPatternException();
    }
  }

  private int findEscapedChar(String pCharacter) {
    return Arrays.binarySearch(escape_chars, pCharacter);
  }

  private boolean readCharacter() {

    boolean charRead = false;

    while (!charRead && index < regex.length()) {
      character = String.valueOf(regex.charAt(index++));
      charRead = true;
    }
    return charRead;

  }



}
