package org.example.cruddemo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    Integer cnt = 0, mx = 10;

    void printou() {
        while (cnt < mx) {
            lock.lock();
            try {
                if (cnt % 2 == 1) condition.await();
                else {
                    System.out.print(cnt + " ");
                    cnt++;
                    condition.signal();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finally {
                lock.unlock();
            }
        }
    }

    void printji() {
        while (cnt < mx) {
            lock.lock();
            try {
                if (cnt % 2 == 0) condition.await();
                else {
                    System.out.print(cnt + " ");
                    cnt++;
                    condition.signal();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        ThreadTest threadTest = new ThreadTest();
        new Thread(threadTest::printou).start();
        new Thread(threadTest::printji).start();
    }
}
