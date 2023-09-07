package fi.lipp.regexp.service.regexpParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
  private String value = "";
  private List<Node> children;
  private RegexpToken type;

  public Node(RegexpToken pType) {

    type = pType;
    children = new ArrayList<Node>();
  }


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Iterable<Node> getChildren() {

    return Collections.unmodifiableList(children);
  }

  public void addChild(Node childNode) {
    children.add(childNode);
  }

  public RegexpToken getType() {
    return type;
  }

  public boolean isEmpty() {
    return children.isEmpty();
  }

  public int getChildrenCount() {
    return children.size();
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();

    buffer.append(hashCode() + " [label=\"" + readValue() + "\"];\n");

    for (Node child : children) {
      buffer.append(String.valueOf(hashCode()) + " -- " + child.hashCode() + ";\n");
      buffer.append(child.toString());
    }

    return buffer.toString();

  }

  private String readValue() {
    return type.toString() + " ('" + value + "')";

  }

}
