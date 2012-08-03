package raceerror.data;


public class Foo {

	public Foo() {
	}
	public Foo(String name) {
		this.name = name;
	}
	public String name;
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Foo[" + getName() + "]"; 
	}
	
}