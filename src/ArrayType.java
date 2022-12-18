public class ArrayType implements Type{
	Type elementType;
	int elementCount;
	
	public ArrayType(int elementCount, Type elementType) {
		this.elementCount = elementCount;
		this.elementType = elementType;
	}
	
	@Override
	public String toString() {
		StringBuilder typeStr = new StringBuilder();
		if (elementCount == 0) {
			return typeStr.append(elementType).toString();
		}
		return typeStr.append("array(")
				.append(elementCount)
				.append(",")
				.append(elementType)
				.toString();
	}
}
