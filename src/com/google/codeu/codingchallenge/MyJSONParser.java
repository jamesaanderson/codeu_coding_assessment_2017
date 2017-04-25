// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.codingchallenge;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Stack;

final class MyJSONParser implements JSONParser {
  class ParseException extends IOException {
    ParseException(String msg) {
      super(msg);
    } 
  }

  private ListIterator<MyJSONLexer.Token> iter;
  private Stack<JSON> objStack = new Stack<JSON>();

  @Override
  public JSON parse(String in) throws IOException {
    MyJSONLexer lexer = new MyJSONLexer(in);
    lexer.scan();

    iter = lexer.tokens.listIterator();

    if (iter.next().type == MyJSONLexer.TokenType.OBJOPEN) {
      return parseObjNull();
    } else {
      throw new ParseException("No object defined."); 
    }
  }

  private JSON parseObj() throws IOException {
    if (iter.next().type == MyJSONLexer.TokenType.OBJCLOSE) { // empty object
      return objStack.peek(); 
    }

    iter.previous();
    JSON obj = parseKeyVal();

    MyJSONLexer.Token sym = iter.next();

    if (sym.type == MyJSONLexer.TokenType.OBJCLOSE) {
      return obj; 
    }
    
    if (sym.type == MyJSONLexer.TokenType.COMMA) {
      objStack.pop();

      return parseObj();
    }

    throw new ParseException("Invalid JSON-lite syntax.");
  }

  private JSON parseObjNull() throws IOException {
    objStack.push(new MyJSON());

    return parseObj();
  }

  private JSON parseKeyVal() throws IOException {
    MyJSONLexer.Token key = iter.next();

    if (key.type == MyJSONLexer.TokenType.STRING && iter.next().type == MyJSONLexer.TokenType.COLON) {
      return parseVal(key);
    }

    throw new ParseException("Missing key or }.");
  }

  private JSON parseVal(MyJSONLexer.Token key) throws IOException {
    MyJSONLexer.Token val = iter.next();

    if (val.type == MyJSONLexer.TokenType.OBJOPEN) {
      return objStack.peek().setObject(key.value, parseObjNull());
    }
    
    if (val.type == MyJSONLexer.TokenType.STRING) {
      objStack.peek().setString(key.value, val.value);

      if (this.iter.next().type == MyJSONLexer.TokenType.COMMA) {
        return parseKeyVal();
      } 

      this.iter.previous();
      return objStack.peek();
    }
    
    throw new ParseException("Missing value."); 
  }
}
