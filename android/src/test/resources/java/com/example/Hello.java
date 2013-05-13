package com.example;

public class Hello implements IHello {

    private final World what;

    public Hello(World what) {
        this.what = what;
    }

    @Override
    public void sayHello(String what) {
        System.out.println(what + ", this is a random number " + what);
    }

    public static class World {
        public double giveMeANumber() {
            return Math.random();
        }
    }
}
