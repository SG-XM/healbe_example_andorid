package com.healbe.healbe_example_andorid.dashboard;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Six<First, Second, Third, Fourth, Fifth, Sixth> {
    private First first;
    private Second second;
    private Third third;
    private Fourth fourth;
    private Fifth fifth;
    private Sixth sixth;

    public Six(First first, Second second, Third third, Fourth fourth, Fifth fifth, Sixth sixth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
        this.sixth = sixth;
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

    public Fifth getFifth() {
        return fifth;
    }

    public Sixth getSixth() {
        return sixth;
    }
}
