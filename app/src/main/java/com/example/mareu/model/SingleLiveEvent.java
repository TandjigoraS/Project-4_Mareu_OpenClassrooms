package com.example.mareu.model;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mareu.R;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private static final String TAG = String.valueOf(R.string.single_event);

    private final AtomicBoolean pending = new AtomicBoolean(false);

    @MainThread
    @Override
    public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        if (hasActiveObservers()) {
            Log.w(TAG, String.valueOf(R.string.has_active_observe));
        }

        // Observe the internal MutableLiveData
        super.observe(owner, t -> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }

    @MainThread
    @Override
    public void setValue(@Nullable final T t) {
        pending.set(true);
        super.setValue(t);
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
}
