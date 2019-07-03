package com.healbe.healbe_example_andorid.connect;

// small router for fragment navigation
public interface ConnectRouter {
    enum State {
        CONNECT,
        SEARCH,
        ERROR,
        ENTER_PIN,
        SETUP_PIN,
    }

    void goState(State state, boolean back);
    void logout();
    void connected();
}
