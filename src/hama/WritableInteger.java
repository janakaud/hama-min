/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hama;

/**
 *
 * @author Nuran Arachchi
 */
public class WritableInteger implements Writable {

    private int value;

    public WritableInteger(int value) {
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
        int hash = 7;
        hash = 29 * hash + this.value;
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
        final WritableInteger other = (WritableInteger) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }
}
