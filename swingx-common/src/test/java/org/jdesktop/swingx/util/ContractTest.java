package org.jdesktop.swingx.util;

import static org.junit.Assert.fail;

import org.junit.Test;

@SuppressWarnings("nls")
public class ContractTest {
    @Test
    public void testAsNotNull() {
        //expected good cases with Objects
        Contract.asNotNull("1", "Works");
        
        Contract.asNotNull(new String[]{}, "Works");
        Contract.asNotNull(new String[0], "Works");
        Contract.asNotNull(new String[]{"1"}, "Works");
        
        Contract.asNotNull(new String[][]{}, "Works");
        Contract.asNotNull(new String[0][0], "Works");
        Contract.asNotNull(new String[][]{new String[]{}}, "Works");
        Contract.asNotNull(new String[][]{new String[0]}, "Works");
        Contract.asNotNull(new String[][]{new String[]{"1"}}, "Works");
        
        //expected failure cases with Objects
        try {
            Contract.asNotNull(null, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[]{null}, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[1], "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[]{"1", null}, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }

        try {
            Contract.asNotNull(new String[]{"1", null, "2"}, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
        
        try {
            Contract.asNotNull(new String[][]{new String[1]}, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
        
        //expected success with primitives
        Contract.asNotNull(new int[]{}, "Works");
        Contract.asNotNull(new int[0], "Works");
        Contract.asNotNull(new int[]{1}, "Works");
        Contract.asNotNull(new int[1], "Works");
        Contract.asNotNull(new int[1][0], "Works");
        Contract.asNotNull(new int[1][1], "Works");
        
        //expected failure cases with primitives
        int[] badArray = null;
        
        try {
            Contract.asNotNull(badArray, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
        
        int[][] badArray2 = null;
        
        try {
            Contract.asNotNull(badArray2, "Expected failure");
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            //success
        }
    }
}
