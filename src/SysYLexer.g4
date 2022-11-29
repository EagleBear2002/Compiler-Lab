lexer grammar SysYLexer;

CONST : 'const';

INT : 'int';

VOID : 'void';

IF : 'if';

ELSE : 'else';

WHILE : 'while';

BREAK : 'break';

CONTINUE : 'continue';

RETURN : 'return';

PLUS : '+';

MINUS : '-';

MUL : '*';

DIV : '/';

MOD : '%';

ASSIGN : '=';

EQ : '==';

NEQ : '!=';

LT : '<';

GT : '>';

LE : '<=';

GE : '>=';

NOT : '!';

AND : '&&';

OR : '||';

L_PAREN : '(';

R_PAREN : ')';

L_BRACE : '{';

R_BRACE : '}';

L_BRACKT : '[';

R_BRACKT : ']';

COMMA : ',';

SEMICOLON : ';';

IDENT
   : [_a-zA-Z] [_a-zA-Z0-9]*
   ;

INTEGR_CONST
   : DECIMAL_CONST
   | OCTAL_CONST
   | HEXADECIMAL_CONST
   ;

fragment
DECIMAL_CONST
   : '0'
   | [1-9] [0-9]*
   ;

fragment
OCTAL_CONST:
   '0' [0-7]+
   ;

fragment
HEXADECIMAL_CONST
   : ('0x' | '0X') [a-fA-F0-9]+
   ;

WS
   : [ \r\n\t]+ -> skip
   ;

LINE_COMMENT
   : '//' .*? '\n' -> skip
   ;

MULTILINE_COMMENT
   : '/*' .*? '*/' -> skip
   ;
