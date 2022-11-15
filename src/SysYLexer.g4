grammar SysYLexer;

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

WS: [ \t\r\n]+ -> skip;
IDENT: (LETTER | '_') (LETTER | DIGIT | '_')*;
INTEGR_CONST: OCTAL | HEXADECIMAL | DECIMAL;
DECIMAL: DIGIT+;
OCTAL: '0' [0-7]+;
HEXADECIMAL: ('0x' | '0X') (DIGIT | [a-fA-F])+;

SL_COMMENT: '//' .*? '\n' -> skip;
ML_COMMENT: '/*' .*? '*/' -> skip;

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];