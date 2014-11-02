package hama;

/**
 *
 * @author Nuran Arachchi
 */
public class WritableComparableInteger implements WritableComparable {

    private int value;

    public WritableComparableInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.value;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WritableComparableInteger other = (WritableComparableInteger) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
}
