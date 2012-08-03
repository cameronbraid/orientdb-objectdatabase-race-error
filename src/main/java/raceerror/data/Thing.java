package raceerror.data;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.OneToMany;

import com.google.common.collect.Lists;

public class Thing {

	public Thing() {}
	public Thing(String name) {
		this.name = name;
	}
	public String name;
	public String getName() {
		return name;
	}	
	public void setName(String name) {
		this.name = name;
	}
	
	@Embedded
	List<Thing> things = Lists.newArrayList(); 
	public List<Thing> getThings() {
		return things;
	}
	
	List<Foo> foos = Lists.newArrayList();
	public List<Foo> getFoos() {
		return foos;
	}

	@Override
	public String toString() {
		return "thing[" + name + "]";
	}
}