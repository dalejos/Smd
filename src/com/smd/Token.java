/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smd;

/**
 *
 * @author David
 */
public enum Token {
    INTERFACE("\tINTERFACE"),
    INTERFACE_LABEL("\tLABEL "),
    CONTROL("\tCONTROL"),
    BUTTON(" AS BUTTON"),
    COMMENT("\tCOMMENT "),
    LABEL("\tLABEL "),
    COMMAND("\tCOMMAND "),
    MENU("\tMENU"),
    MENU_LABEL("\t\""),
    OPTION("\tOPTION"),
    HELP("\tHELP "),
    OPTION_LABEL("\t\""),
    DESCRIPCION_PROGRAMA("DESCRIPCION PROGRAMA "),
    ;
    
    private final String token;
    
    Token(String token){
        this.token = token;
    }

    @Override
    public String toString() {
        return this.token;
    }

}
