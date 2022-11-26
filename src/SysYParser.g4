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
constDecl : CONST bType constDef (',' constDef )* ';';

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
funcDef : IDENT L_PAREN (funcFParams)* R_PAREN block;

// 函数类型
funcType : 'void' | 'int';

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
    | exp ';'
    | block
    | 'if' '(' cond ')' stmt ('else' stmt)?
    | 'while' '(' cond')' stmt
    | 'break' ';'
    | 'continue' ';'
    | 'return' (exp)? ';'
    ;

// 表达式
exp : L_PAREN exp R_PAREN
   | lVal 
   | number
   | IDENT L_PAREN funcRParams? R_PAREN 
   | unaryOp exp 
   | exp (MUL | DIV | MOD) exp
   | exp (PLUS | MINUS) exp
   ;

// 条件表达式
cond : exp 
   | cond (LT | GT | LE | GE) cond
   | cond (EQ | NEQ) cond 
   | cond AND cond 
   | cond OR cond 
   ;

// 左值表达式
lVal : IDENT (L_BRACKT exp R_BRACKT)*;
   
// 基本表达式
primaryExp : '(' exp ')' | lVal | number;

// 数值
number : INTEGR_CONST;
   
// 一元表达式
unaryOp : PLUS | MINUS | NOT;
   
// 单目运算符

// 函数实参表

// 乘除模表达式

// 加减表达式

// 关系表达式

// 相等性表达式

// 逻辑与表达式

// 逻辑或表达式

// 常量表达式
constExp : exp;

// from TA
funcRParams : param (COMMA param)*;

// from TA
param : exp;
