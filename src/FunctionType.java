import java.util.ArrayList;

public record FunctionType(Type retType, ArrayList<Type> paramsType) implements Type {
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder(retType + "(");
		for (Type type : paramsType) {
			ret.append(type.toString());
		}
		ret.append(")");
		return ret.toString();
	}
	
	
}
