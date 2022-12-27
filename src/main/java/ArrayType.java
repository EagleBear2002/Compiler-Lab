public class ArrayType implements Type {
	Type elementType;
	int elementCount;
	
	public ArrayType(int elementCount, Type elementType) {
		this.elementCount = elementCount;
		this.elementType = elementType;
	}
	
	@Override
	public String toString() {
		return "array(" + elementType + ")";
	}
}
