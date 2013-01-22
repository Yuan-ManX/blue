/*
 * blue - object composition environment for csound
 * Copyright (C) 2013
 * Steven Yi <stevenyi@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package blue.ui.editor.clojure;

import blue.ui.editor.clojure.antlr.ClojureLexer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author James Reid
 */
public class AntlrTokenReader {

    private HashMap<String, String> tokenTypes = new HashMap<String, String>();
    private ArrayList<ClojureTokenId> tokens = new ArrayList<ClojureTokenId>();

    public AntlrTokenReader() {
        init();
    }

    private void init() {

        tokenTypes.put("AMPERSAND", "keyword");
        tokenTypes.put("APOSTROPHE", "keyword");
        tokenTypes.put("BACKSLASH", "keyword");
        tokenTypes.put("BOOLEAN", "keyword");
        tokenTypes.put("CHARACTER", "character");
        tokenTypes.put("CLOSE_PAREN", "keyword");
        tokenTypes.put("CIRCUMFLEX", "keyword");
        tokenTypes.put("COMMENT", "comment");
        tokenTypes.put("COMMERCIAL_AT", "keyword");
        tokenTypes.put("EscapeSequence", "keyword");
        tokenTypes.put("HEXDIGIT", "number");
        tokenTypes.put("KEYWORD", "keyword");
        tokenTypes.put("LAMBDA_ARG", "keyword");
        tokenTypes.put("LEFT_CURLY_BRACKET", "keyword");
        tokenTypes.put("LEFT_SQUARE_BRACKET", "keyword");
        tokenTypes.put("METADATA_TYPEHINT", "keyword");
        tokenTypes.put("NAME", "keyword");
        tokenTypes.put("NIL", "keyword");
        tokenTypes.put("NUMBER", "number");
        tokenTypes.put("NUMBER_SIGN", "number");
        tokenTypes.put("NUMBER_SIGN", "keyword");
        tokenTypes.put("OPEN_PAREN", "keyword");
        tokenTypes.put("OctalEscape", "keyword");
        tokenTypes.put("REGEX_LITERAL", "keyword");
        tokenTypes.put("RIGHT_CURLY_BRACKET", "keyword");
        tokenTypes.put("RIGHT_SQUARE_BRACKET", "keyword");
        tokenTypes.put("SPACE", "whitespace");
        tokenTypes.put("SPECIAL_FORM", "number");
        tokenTypes.put("STRING", "string");
        tokenTypes.put("SYMBOL", "keyword");
        tokenTypes.put("SYMBOL_HEAD", "keyword");
        tokenTypes.put("SYMBOL_REST", "keyword");
        tokenTypes.put("SYNTAX_QUOTE", "keyword");
        tokenTypes.put("UNQUOTE", "keyword");
        tokenTypes.put("UNQUOTE_SPLICING", "keyword");
        tokenTypes.put("UnicodeEscape", "keyword");
        
    }

    /**
     * Reads the token file from the ANTLR parser and generates
     * appropriate tokens.
     *
     * @return
     */
    public List<ClojureTokenId> readTokenFile() {
        InputStream inp = ClojureLexer.class.getResourceAsStream("Clojure.tokens");
        BufferedReader input = new BufferedReader(new InputStreamReader(inp));
        readTokenFile(input);
        return tokens;
    }

    /**
     * Reads in the token file.
     *
     * @param buff
     */
    private void readTokenFile(BufferedReader buff) {
        String line = null;
        try {
            while ((line = buff.readLine()) != null) {
                String[] splLine = line.split("=");
                String name = splLine[0];
                int tok = Integer.parseInt(splLine[1].trim());
                ClojureTokenId id;
                String tokenCategory = tokenTypes.get(name);
                if (tokenCategory != null) {
                    //if the value exists, put it in the correct category
                    id = new ClojureTokenId(name, tokenCategory, tok);
                } else {
                    //if we don't recognize the token, consider it to a separator
                    id = new ClojureTokenId(name, "keyword", tok);
                }
                //add it into the vector of tokens
                tokens.add(id);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
