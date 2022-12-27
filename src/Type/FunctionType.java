package Type;

import java.util.ArrayList;

public class FunctionType implements Type {
	Type retType;
	ArrayList<Type> paramsType;
	
	public FunctionType(Type retType, ArrayList<Type> paramsType) {
		this.retType = retType;
		this.paramsType = paramsType;
	}
	
	public Type getRetType() {
		return retType;
	}
	
	public ArrayList<Type> getParamsType() {
		return paramsType;
	}
	
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
