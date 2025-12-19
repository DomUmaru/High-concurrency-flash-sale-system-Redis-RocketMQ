package org.example.cruddemo;

import org.example.cruddemo.service.UserService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccountWithLock {

    private int blance;
    Lock reentrantLock = new ReentrantLock();

    void kouqian(int num) {
        reentrantLock.lock();
        try {
            blance += num;
        } finally {
            reentrantLock.unlock();
        }
    }

    int getBlance() {
        return blance;
    }

    public static void main(String[] args) {
        BankAccountWithLock bankAccountWithLock = new BankAccountWithLock();
        int TrheadCnt = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(TrheadCnt);

        for (int i = 0; i < TrheadCnt; i++) {
            executorService.execute(() -> {
                bankAccountWithLock.kouqian(1);
            });
        }

        executorService.shutdown();

        System.out.println(bankAccountWithLock.getBlance());

    }
}

