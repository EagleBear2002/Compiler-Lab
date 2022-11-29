parser grammar SysYParser;

options {
    tokenVocab = SysYLexer;
}

program : compUnit;

// 编译单元
compUnit : (funcDef | decl)+ EOF;

// 下面是其他的语法单元定义

// 声明
decl : constDecl | varDecl;

// 常量声明
constDecl : CONST bType constDef (',' constDef)* ';';

// 基本类型
bType : INT;

// 常数定义
constDef : IDENT ('[' constExp ']')+ '=' constInitVal;

// 常量初值
constInitVal : constExp | '{' (constInitVal (',' constInitVal)*)? '}';

// 变量声明
varDecl : bType varDef (',' varDef)* ';';

// 变量定义
varDef : IDENT ('[' constExp ']')* ('=' initVal)?;

// 变量初值
initVal : exp | '{' (initVal (',' initVal)*)? '}';

// 函数定义
funcDef : funcType IDENT '(' (funcFParams)? ')' block;

// 函数类型
funcType : VOID | INT;

// 函数形参表
funcFParams : funcFParam (',' funcFParam)*;

// 函数形参
funcFParam : bType IDENT ('[' ']' ('[' exp ']')*)?;

// 语句块
block : '{' (blockItem)* '}';

// 语句块项
blockItem : decl | stmt;

// 语句
stmt : lVal '=' exp ';'
    | (exp)? ';'
    | block
    | IF '(' cond ')' stmt (ELSE stmt)?
    | WHILE '(' cond ')' stmt
    | BREAK ';'
    | CONTINUE ';'
    | RETURN (exp)? ';'
    ;

// 表达式
exp : '(' exp ')'
   | lVal 
   | number
   | IDENT '(' funcRParams? ')' 
   | unaryOp exp 
   | exp ('*' | '/' | '%') exp
   | exp ('+' | '-') exp
   ;

// 条件表达式
cond : exp 
   | cond ('<' | '>' | '<=' | '>=') cond
   | cond ('==' | '!=') cond 
   | cond AND cond 
   | cond OR cond 
   ;

// 左值表达式
lVal : IDENT ('[' exp ']')*;

// 基本表达式
primaryExp : '(' exp ')' | lVal | number;

// 数值
number : INTEGR_CONST;

// 一元表达式
unaryExp : primaryExp | IDENT '(' (funcRParams)? ')' | unaryOp unaryExp;

// 单目运算符
unaryOp : '+' | '-' | '!';

// 函数实参表， from TA
funcRParams : param (',' param)*;

// from TA
param : exp;

// 乘除模表达式
mulExp : unaryOp | '*'Exp ('*' | '/' | '%') unaryExp;

// 加减表达式
addExp : '*'Exp | addExp ('+' | '-') '*'Exp;

// 关系表达式
relExp : addExp | relExp ('<' | '>' | '<=' | '>=') addExp;

// 相等性表达式
eqExp : relExp | eqExp ('==' | '!=') relExp;

// 逻辑与表达式
lAndExp : eqExp | lAndExp '&&' eqExp;

// 逻辑或表达式
lOrExp : lAndExp | lOrExp '||' lAndExp;

// 常量表达式
constExp : exp;