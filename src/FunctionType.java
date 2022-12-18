import java.util.ArrayList;

public class FunctionType implements Type{
	private Type retType;
	private ArrayList<Type> paramsType;
	public FunctionType(Type retType, ArrayList<Type> paramsType) {
		this.retType = retType;
		this.paramsType = paramsType;
	}

	@Override
	public String toString() {
		return retType + "()";
	}
}
