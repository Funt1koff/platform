package com.bonfire.ignite.utils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.val;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.apache.ignite.transactions.TransactionOptimisticException;

import java.util.function.Supplier;

@ApplicationScoped
public class IgniteHelper {

    @Inject
    IgniteClient ignite;

    /**
     * Runs action in transaction with the OPTIMISTIC concurrency and SERIALIZABLE isolation
     *
     * @param actionSupplier action to run in transaction
     */
    public <T> T runInTransaction(Supplier<T> actionSupplier) {
        try (val tx = ignite.transactions().txStart(
                TransactionConcurrency.OPTIMISTIC, TransactionIsolation.SERIALIZABLE)) {
            val result = actionSupplier.get();
            tx.commit();
            return result;
        } catch (TransactionOptimisticException | ClientException e) {
            Log.error(STR."Transaction error \{e.getClass().getName()}");
            throw new IgniteException.TransactionException();
        }
    }

    public void runInTransaction(Runnable action) {
        try (val tx = ignite.transactions().txStart(
                TransactionConcurrency.OPTIMISTIC, TransactionIsolation.SERIALIZABLE)) {
            action.run();
            tx.commit();
        } catch (TransactionOptimisticException | ClientException e) {
            Log.error(STR."Transaction error \{e.getClass().getName()}");
            throw new IgniteException.TransactionException();
        }
    }
}
