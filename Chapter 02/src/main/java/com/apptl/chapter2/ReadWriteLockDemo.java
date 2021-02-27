package com.apptl.chapter2;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Erik Hellman
 */
public class ReadWriteLockDemo {
    private final ReentrantReadWriteLock mLock;
    private String mName;
    private int mAge;
    private String mAddress;

    public ReadWriteLockDemo() {
        mLock = new ReentrantReadWriteLock();
    }

    public void setPersonData(String name, int age, String address) {
        ReentrantReadWriteLock.WriteLock writeLock = mLock.writeLock();
        try {
            writeLock.lock();
            mName = name;
            mAge = age;
            mAddress = address;
        } finally {
            writeLock.unlock();
        }
    }

    public String getName() {
        ReentrantReadWriteLock.ReadLock readLock = mLock.readLock();
        try {
            readLock.lock();
            return mName;
        } finally {
            readLock.unlock();
        }
    }
}
