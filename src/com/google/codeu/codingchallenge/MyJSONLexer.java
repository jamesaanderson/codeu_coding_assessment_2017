package com.google.codeu.codingchallenge;

import java.util.regex.*;
import java.util.ArrayList;
import java.io.IOException;

class MyJSONLexer {
  String in;

  static enum TokenType {
    OBJOPEN("[{]"),
    OBJCLOSE("[}]"),
    STRING("(?:[\"])(.*?)(?:[\"])"), // will not match escaped quotes etc
    COLON("[:]"),
    COMMA("[,]");

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
      return type + " " + value;
    }
  }

  ArrayList<Token> tokens = new ArrayList<Token>();

  MyJSONLexer(String in) {
    this.in = in;
  }

  void scan() throws IOException {
    StringBuffer patternBuffer = new StringBuffer();
    for (TokenType token: TokenType.values()) {
      patternBuffer.append("(?<" + token.name() + ">" + token.pattern + ")|");
    }

    String pattern = patternBuffer.toString();

    Pattern r = Pattern.compile(pattern);
    Matcher m  = r.matcher(in);

    while (m.find()) {
      for (TokenType token: TokenType.values()) {
        String value = m.group(token.name());

        if (value != null) {
          if (token == TokenType.STRING) {
            value = value.substring(1, value.length()-1); // remove quotes at beginning and end
          }

          tokens.add(new Token(token, value));
        }
      } 
    }
  }
}
