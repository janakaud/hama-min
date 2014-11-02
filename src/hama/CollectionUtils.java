/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hama;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author janaka
 */
public class CollectionUtils {

    public static Set intersection(Set one, Set two) {
        Set big, small;
        if (one.size() >= two.size()) {
            big = new HashSet(one);
            small = two;
        } else {
            big = new HashSet(two);
            small = one;
        }
        big.removeAll(small);
        return big;
    }
}
