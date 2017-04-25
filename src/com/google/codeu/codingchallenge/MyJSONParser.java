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

final class MyJSONParser implements JSONParser {
  class ParseException extends IOException {
    ParseException(String msg) {
      super(msg);
    } 
  }

  private ListIterator<MyJSONLexer.Token> iter;
  private JSON obj;

  @Override
  public JSON parse(String in) throws IOException {
    MyJSONLexer lexer = new MyJSONLexer(in);
    lexer.scan();

    iter = lexer.tokens.listIterator();

    if (iter.next().type == MyJSONLexer.TokenType.OBJOPEN) {
      return parseObj();
    } else {
      throw new ParseException("No object defined."); 
    }
  }

  private JSON parseObj() throws IOException {
    obj = new MyJSON(); 

    if (iter.next().type == MyJSONLexer.TokenType.OBJCLOSE) { // empty object
      return obj; 
    }

    iter.previous();
    JSON json = parseKeyVal();

    if (iter.next().type == MyJSONLexer.TokenType.OBJCLOSE) {
      return json; 
    }

    throw new ParseException("Missing }.");
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
      return obj.setObject(key.value, parseObj());
    }
    
    if (val.type == MyJSONLexer.TokenType.STRING) {
      obj.setString(key.value, val.value);

      if (this.iter.next().type == MyJSONLexer.TokenType.COMMA) {
        return parseKeyVal();
      } 

      this.iter.previous();
      return obj;
    }
    
    throw new ParseException("Missing value."); 
  }
}
