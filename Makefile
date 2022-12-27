include Makefile.git

export CLASSPATH=/usr/local/lib/antlr-*-complete.jar

DOMAINNAME = 47.122.3.40:3000
ANTLR = java -jar /usr/local/lib/antlr-*-complete.jar -listener -visitor -long-messages
JAVAC = javac -g
JAVA = java


PFILE = $(shell find . -name "SysYParser.g4")
LFILE = $(shell find . -name "SysYLexer.g4")
JAVAFILE = $(shell find . -name "*.java")
ANTLRPATH = $(shell find /usr/local/lib -name "antlr-*-complete.jar")

compile: antlr
	$(call git_commit,"make")
	mkdir -p classes
	$(JAVAC) -classpath $(ANTLRPATH) $(JAVAFILE) -d classes

lexer-test1: compile
	$(call git_commit, "lexer-test1")
	java -classpath ./classes:$(ANTLRPATH) Main tests/lexer-test1.sysy

lexer-test2: compile
	$(call git_commit, "lexer-test2")
	java -classpath ./classes:$(ANTLRPATH) Main tests/lexer-test2.sysy

parser-test1: compile
	$(call git_commit, "parser-test1")
	java -classpath ./classes:$(ANTLRPATH) Main tests/parser-test1.sysy

parser-test2: compile
	$(call git_commit, "parser-test2")
	java -classpath ./classes:$(ANTLRPATH) Main tests/parser-test2.sysy

type-test: compile
	$(call git_commit, "type-test")
	java -classpath ./classes:$(ANTLRPATH) Main tests/type-test.sysy 8 4 d

type-test2: compile
	$(call git_commit, "type-test2")
	java -classpath ./classes:$(ANTLRPATH) Main tests/type-test2.sysy 8 4 d

rename-test: compile
	$(call git_commit, "rename-test")
	java -classpath ./classes:$(ANTLRPATH) Main tests/rename-test.sysy 8 4 d

llvm-ir-test1: compile
	$(call git_commit, "llvm-ir-test1")
	java -classpath ./classes:$(ANTLRPATH) Main tests/llvm-ir-test1.sysy tests/llvm-ir-test1.out

llvm-ir-test2: compile
	$(call git_commit, "llvm-ir-test2")
	java -classpath ./classes:$(ANTLRPATH) Main tests/llvm-ir-test2.sysy tests/llvm-ir-test2.out

antlr: $(LFILE) $(PFILE) 
	$(ANTLR) $(PFILE) $(LFILE)

test: compile
	$(call git_commit, "test")
	nohup java -classpath ./classes:$(ANTLRPATH) Main ./tests/test1.sysy &

clean:
	rm -f src/*.tokens
	rm -f src/*.interp
	rm -f src/SysYLexer.java src/SysYParser.java src/SysYParserBaseListener.java src/SysYParserBaseVisitor.java src/SysYParserListener.java src/SysYParserVisitor.java
	rm -rf classes

submit:
	git gc
	#bash -c "$$(curl -s $(DOMAINNAME)/scripts/submit-v2.sh)"
	bash submit.sh

.PHONY: compile antlr test run clean submit