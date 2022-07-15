import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
    Represents inclusive integer range.
*/
class Range implements Serializable {

	private static final long serialVersionUID = 1L;

	/** @serial */
	private final int from;
	/** @serial */
	private final int to;

	/**
	 * Creates Range.
	 * 
	 * @param from start 
	 * @param to end
	 * @throws IllegalArgumentException if start is greater than end. 
	 */
	public Range(int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException("Start is greater than end");
		}
		this.from = from;
		this.to = to;
	}

	private void writeObject(ObjectOutputStream oos) throws Exception {
		// write the custom serialization code here
		oos.defaultWriteObject();

	}

	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
		if (from > to) {
			throw new IllegalArgumentException("Start is greater than end");
		}

		// write the custom deserialization code here
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

}