package com.healbe.healbe_example_andorid.connect;

import android.content.Context;

import androidx.fragment.app.Fragment;

//or base routed class
public abstract class ConnectionRoutedFragment extends Fragment implements BackNavigationConsumer {
    private ConnectRouter router;

    public ConnectRouter getRouter() {
        return router;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConnectRouter) {
            router = (ConnectRouter) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ConnectRouter");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        router = null;
    }
}
