/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package blue.ui.editor.blueSynthBuilder;

import blue.ui.editor.csound.orc.*;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author stevenyi
 */
public enum BSBTokenId implements TokenId {

    CHAR(null, "character"),
    STRING(null, "string"),
    IDENTIFIER(null, "identifier"),
    COMMENT(null, "comment"),
    KEYWORD(null, "keyword"),
    MYFLOAT(null, "constant"),
    INT(null, "constant"),
    WS(null, "whitespace"),
    OPERATOR(null, "operator"),
    SL_COMMENT(null, "comment"),
    ML_COMMENT(null, "comment"),
    ML_COMMENT_INCOMPLETE(null, "error"),
    LPAREN("(", "separator"),
    RPAREN(")", "separator"),
    ERROR(null, "error"),
    
    OPCODE(null, "opcode"),
    
    INSTR_START("instr", "keyword"),
    INSTR_END("endin", "keyword"),
    OPCODE_START("opcode", "keyword"),
    OPCODE_END("endop", "keyword");
    
    
    private static final Language<BSBTokenId> language =
            new BSBLanguageHierarchy().language();
    private final String fixedText;
    private final String primaryCategory;
//    private final int id;

    private BSBTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
//        this.id = id;
    }
    
    public String getFixedText() {
        return fixedText;
    }

    @Override
    public String primaryCategory() {
        return primaryCategory;
    }

    public static final Language<BSBTokenId> language() {
        return language;
    }
}
