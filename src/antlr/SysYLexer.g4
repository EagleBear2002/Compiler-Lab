lexer grammar SysYLexer;

//expr: expr ('*' | '/') expr | expr ('+' | '-') expr | IDENT | INT;
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

IDENT: (LETTER | '_') (LETTER | DIGIT | '_')* ;

INTEGR_CONST: OCTAL | HEXADECIMAL | DECIMAL;
fragment DECIMAL: '0' | [1-9] [0-9]*;
fragment OCTAL: '0' [0-7]+;
fragment HEXADECIMAL: ('0x' | '0X') (DIGIT | [a-fA-F])+ ;
WS: [ \t\r\n]+ -> skip;
SL_COMMENT: '//' .*? '\n' -> skip;
ML_COMMENT: '/*' .*? '*/' -> skip;
fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];