package example.project.gen;

import java.util.Set;
import java.util.HashSet;

/**
 * Static class that has all valid value lists for data types
 * <br /> generated at 23 Jun, 2019 9:47:14 AM
 */ 
public class ValueLists {
	public static final Set<Long> severity = new HashSet<>(4);
	static{
		severity.add(0L);
		severity.add(1L);
		severity.add(2L);
		severity.add(3L);
	}
	public static final Set<Long> color = new HashSet<>(3);
	static{
		color.add(0L);
		color.add(1L);
		color.add(2L);
	}
}
