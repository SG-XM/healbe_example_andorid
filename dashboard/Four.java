package com.healbe.healbe_example_andorid.dashboard;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Four<First, Second, Third, Fourth> {
    private First first;
    private Second second;
    private Third third;
    private Fourth fourth;

    public Four(First first, Second second, Third third, Fourth fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }


    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    public Third getThird() {
        return third;
    }

    public Fourth getFourth() {
        return fourth;
    }

}
