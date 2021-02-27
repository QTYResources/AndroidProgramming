package com.aptl.jnidemo;

public class NativeSorting {

    static {
        System.loadLibrary("sorting_jni");
    }

    public NativeSorting() {

    }

    public void sortIntegers(int[] ints) {
        nativeSort(ints);
    }

    private native void nativeSort(int[] ints);

    public void sortIntegersWithCallback(int[] ints, Callback callback) {
    nativeSortWithCallback(ints, callback);

	private native void nativeSortWithCallback(int[] ints, Callback callback);

	public interface Callback {
	    void onSorted(int[]sorted);
	}
	
}
