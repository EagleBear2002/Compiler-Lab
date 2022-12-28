import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.bytedeco.javacpp.BytePointer;

import static org.bytedeco.llvm.global.LLVM.*;

import java.io.IOException;

public class Main {
	
	public static SysYLexer lexer(String sourcePath) throws IOException {
		CharStream input = CharStreams.fromFileName(sourcePath);
		return new SysYLexer(input);
	}
	
	public static SysYParser parser(SysYLexer sysYLexer) {
		CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
		return new SysYParser(tokens);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("input path is required");
			return;
		}
		
		String srcPath = args[0];
		String destPath = args[1];
		SysYLexer sysYLexer = lexer(srcPath);
		SysYParser sysYParser = parser(sysYLexer);
		ParseTree tree = sysYParser.program();
		
		LLVMIRVisitor llvmirVisitor = new LLVMIRVisitor();
		llvmirVisitor.visit(tree);
		
		final BytePointer error = new BytePointer();
		if (LLVMPrintModuleToFile(llvmirVisitor.getModule(), destPath, error) != 0) {    // moudle 是你自定义的 LLVMModuleRef 对象
			LLVMDisposeMessage(error);
		}
	}
}
