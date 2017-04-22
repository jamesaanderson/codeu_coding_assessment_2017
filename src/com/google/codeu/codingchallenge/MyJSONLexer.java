package com.google.codeu.codingchallenge;

import java.util.*;
import java.util.regex.*;

class MyJSONLexer {
  String in;

  static enum TokenType {
    OBJOPEN("[{]"),
    OBJCLOSE("[}]");

    final String pattern;

    private TokenType(String pattern) {
      this.pattern = pattern;
    }
  }  

  class Token {
    TokenType type;
    String value;

    Token(TokenType type, String value) {
      this.type = type;
      this.value = value;
    } 

    @Override
    public String toString() {
      return this.type + " " + this.value;
    }
  }

  ArrayList<Token> tokens = new ArrayList<Token>();

  MyJSONLexer(String in) {
    this.in = in;
  }

  void scan() {
    StringBuffer patternBuffer = new StringBuffer();
    for (TokenType token: TokenType.values()) {
      patternBuffer.append("(?<" + token.name() + ">" + token.pattern + ")|");
    }

    String pattern = patternBuffer.toString();

    Pattern r = Pattern.compile(pattern);
    Matcher m  = r.matcher(this.in);

    while (m.find()) {
      for (TokenType token: TokenType.values()) {
        if (m.group(token.name()) != null) {
          this.tokens.add(new Token(token, m.group(token.name())));
        } 
      } 
    }
  }
}
