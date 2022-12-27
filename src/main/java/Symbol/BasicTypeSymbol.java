package Symbol;
import Type.*;

public class BasicTypeSymbol extends BaseSymbol implements Type {
	public BasicTypeSymbol(String name) {
		super(name, null);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
