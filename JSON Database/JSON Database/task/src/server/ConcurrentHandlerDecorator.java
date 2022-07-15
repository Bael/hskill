package server;

import com.google.gson.JsonElement;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentHandlerDecorator implements Memory {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Memory memory;

    public ConcurrentHandlerDecorator(Memory memory) {
        this.memory = memory;
    }

    @Override
    public void put(JsonElement key, JsonElement value) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        memory.put(key, value);
        writeLock.unlock();
    }

    @Override
    public JsonElement get(JsonElement key) {
        Lock readLock = lock.readLock();
        readLock.lock();
        JsonElement value = memory.get(key);
        readLock.unlock();
        return value;
    }

    @Override
    public boolean delete(JsonElement key) {
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        boolean value = memory.delete(key);
        writeLock.unlock();
        return value;
    }

    @Override
    public boolean contains(JsonElement key) {
        Lock readLock = lock.readLock();
        readLock.lock();
        boolean value = memory.contains(key);
        readLock.unlock();
        return value;
    }
}
