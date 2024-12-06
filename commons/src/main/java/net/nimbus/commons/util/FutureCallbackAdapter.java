package net.nimbus.commons.util;

import com.google.common.util.concurrent.FutureCallback;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class FutureCallbackAdapter<T> implements FutureCallback<T> {

    private Consumer<T> consumer;

    @Override
    public void onSuccess(T result) {
        consumer.accept(result);
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }
}
