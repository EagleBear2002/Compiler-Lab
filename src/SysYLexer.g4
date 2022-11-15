grammar SysYLexer;

// *: 0 or more
prog: stat* EOF;

stat: expr ';' | ID '=' expr ';' | 'if' expr ';';

// | : or () : subrule
expr: expr ('*' | '/') expr | expr ('+' | '-') expr | ID | INT;

ID: (LETTER | '_') (LETTER | DIGIT | '_')*;

// +: 1 or more
INT: '0' | ([1-9] DIGIT*);

WS: [ \t\r\n]+ -> skip;

SL_COMMENT: '//' .*? '\n' -> skip;
ML_COMMENT: '/*' .*? '*/' -> skip;

fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];
