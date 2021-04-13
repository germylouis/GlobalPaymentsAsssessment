package com.imobile3.groovypayments.ui.orderhistory;

import android.util.Log;

import com.imobile3.groovypayments.concurrent.GroovyExecutors;
import com.imobile3.groovypayments.data.CartRepository;
import com.imobile3.groovypayments.data.Result;
import com.imobile3.groovypayments.data.model.Cart;
import com.imobile3.groovypayments.data.model.Product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The ViewModel serves as an async bridge between the View (Activity, Fragment)
 * and our backing data repository (Database).
 */
public class OrderHistoryViewModel extends ViewModel {

    private static final String TAG = "OrderHistoryViewModel" ;
    private int mCartClicks;
    private CartRepository mRepository;

    OrderHistoryViewModel(CartRepository repository) {
        mCartClicks = 0;
        mRepository = repository;
    }

    public void addCartClick() {
        mCartClicks++;
    }

    public int getCartClicks() {
        return mCartClicks;
    }

    public LiveData<List<Cart>> getCarts() {
        final MutableLiveData<List<Cart>> observable =
                new MutableLiveData<>(new ArrayList<>());
        //wanted to find carts with a total paid >0 than sort by date
        GroovyExecutors.getInstance().getDiskIo().execute(() -> {
            Result<List<Cart>> result = mRepository.getDataSource().loadCarts();
            if (result instanceof Result.Success) {
                List<Cart> resultSet = ((Result.Success<List<Cart>>) result).getData();
                for (Cart cart : resultSet) {
                    //log to check if anything changed; a way to test my edits
                    Log.d(TAG, "getProducts: " + cart.getTotalPaid());
                }
                observable.postValue(resultSet);
            } else {
                observable.postValue(new ArrayList<>());
            }
        });

        return observable;
    }
}
