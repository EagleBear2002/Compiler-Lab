import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

import java.io.IOException;

public class Main {
	public static final BytePointer error = new BytePointer();
	
	public static SysYLexer lexer(String sourcePath) throws IOException {
		CharStream input = CharStreams.fromFileName(sourcePath);
		return new SysYLexer(input);
	}
	
	public static SysYParser parser(SysYLexer sysYLexer) {
		CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
		return new SysYParser(tokens);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("input path is required");
			return;
		}
		
		String srcPath = args[0];
		String destPath = args[0];
		SysYLexer sysYLexer = lexer(srcPath);
		SysYParser sysYParser = parser(sysYLexer);
		
		ParseTree tree = sysYParser.program();
		Visitor visitor = new Visitor();
		visitor.visit(tree);
		
		
//		if (LLVMPrintModuleToFile(moudule, args[1], error) != 0) {    // moudle是你自定义的LLVMModuleRef对象
//			LLVMDisposeMessage(error);
//		}
	}
}