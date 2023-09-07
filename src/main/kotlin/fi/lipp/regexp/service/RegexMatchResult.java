package fi.lipp.regexp.service;

public class RegexMatchResult {
  private int pos = 0;

  private int matchStartPos = -1;
  private int matchLen = 0;

  public int getPos() {
    return pos;
  }
  public void setMatchStartPos(int matchStartPos) {
    this.matchStartPos = matchStartPos;
  }

  public int getMatchLen() {
    return matchLen;
  }

  public void setMatchLen(int matchLen) {
    this.matchLen = matchLen;
  }
  public void setPos(int pos) {
    this.pos = pos;
  }

  public int getMatchStartPos() {
    return matchStartPos;
  }

}
