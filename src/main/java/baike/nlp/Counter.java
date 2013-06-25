package baike.nlp;

public class Counter {
	private int count = 0;
	
	public Counter(int initialCount) {
		count = initialCount;
	}
	
	public int get() { return count; }
	public void set(int count) { this.count = count; }
	public void increase() { count ++; }
	
	@Override
	public String toString() { return Integer.toString(count); }
}
