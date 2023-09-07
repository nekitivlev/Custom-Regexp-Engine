package fi.lipp.regexp.service;

import fi.lipp.regexp.model.Caret;
import fi.lipp.regexp.model.Editor;
import fi.lipp.regexp.model.InvalidPatternException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import java.util.regex.Pattern;
public class RegexpServiceImpl implements RegexpService {
  Integer lowerBound(ArrayList<Integer> positions, Integer value){
    Integer index = Collections.binarySearch(positions, value);
    if(index < 0){
      return -index - 1;
    }else{
      return index;
    }
  }
  @NotNull
  @Override
  public Set<Pair<Integer, Integer>> matchAll(@NotNull Editor editor, @NotNull String pattern)
      throws InvalidPatternException {
    String newText=editor.getText();
    Set<Pair<Integer, Integer>> result = new HashSet<>();
    Pattern tempPattern = Pattern.compile("\\.");
    String newPattern = tempPattern.matcher(pattern).replaceAll("\\\\.");
    tempPattern = Pattern.compile("\\\\c");
    newPattern = tempPattern.matcher(newPattern).replaceAll("\\\\\\\\c");
    if(!newPattern.matches("\\A\\p{ASCII}*\\z") || newPattern.matches("\\A\\p{ASCII}*\\(\\)\\p{ASCII}*\\z")
        || newPattern.matches("\\A\\p{ASCII}*[a-zA-Z]\\+\\p{ASCII}*\\z") || newPattern.matches("\\A\\p{ASCII}*\\\\[a-bd-zA-Z]*\\z")){
      throw new InvalidPatternException();
    }

    Pattern p;
    try {
      p = Pattern.compile(newPattern);
    } catch (Exception e) {
      throw new InvalidPatternException();
    }
    boolean isCaret = false;
    int count = 0;
    for(Caret i : editor.getCarets()){
      isCaret = true;
      newText = newText.substring(0, i.getCaretOffset()+count*2) +"\\c"+ newText.substring(i.getCaretOffset()+count*2, newText.length());
      count++;
    }
    tempPattern = Pattern.compile("\\\\c");
    Matcher subMatcher = tempPattern.matcher(newText);
    ArrayList<Integer> positions = new ArrayList<>();
    while(subMatcher.find()){
      positions.add(subMatcher.start());
    }

    ArrayList<Pair<Integer, String>> substrings = new ArrayList<>();
    for(int i= 0; i < newText.length(); i++){
      for(int j = i + 1; j <= newText.length(); j++){

        substrings.add(new Pair<>(i, newText.substring(i, j)));
      }
    }

    for(int i=0;i<substrings.size();i++) {
      Matcher m = p.matcher(substrings.get(i).getSecond());
      while (m.find()) {
          int leftShift = lowerBound(positions, m.start()+substrings.get(i).getFirst());
          int rightShift = lowerBound(positions, m.end()+substrings.get(i).getFirst());

          result.add(new Pair<>(m.start()+substrings.get(i).getFirst() -2*leftShift, m.end()+substrings.get(i).getFirst()-2*rightShift));

      }
    }

      return result;
  }
}
