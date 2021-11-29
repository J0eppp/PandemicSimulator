package dev.j0eppp.pandemicsimulator;

public class Person {

    private int identifier;
    private float immunity;
    private float contagiousness;
    private boolean mask;
    private int amountOfFriends;
    private int contagiousDays;

    public Person(int identifier, float immunity, float contagiousness, boolean mask, int amountOfFriends) {
        this.identifier = identifier;
        this.immunity = immunity;
        this.contagiousness = contagiousness;
        this.mask = mask;
        this.amountOfFriends = amountOfFriends;
        this.contagiousDays = 0;
    }

    public float getContagiousness() {
        return contagiousness;
    }

    public void setContagiousness(float contagiousness) {
        this.contagiousness = contagiousness;
    }

    public int getAmountOfFriends() {
        return amountOfFriends;
    }

    public float getImmunity() {
        return immunity;
    }

    public void setImmunity(float immunity) {
        this.immunity = immunity;
    }

    public boolean isMask() {
        return mask;
    }

    public void setMask(boolean mask) {
        this.mask = mask;
    }

    public void setAmountOfFriends(int amountOfFriends) {
        this.amountOfFriends = amountOfFriends;
    }

    public int getContagiousDays() {
        return contagiousDays;
    }

    public void setContagiousDays(int contagiousDays) {
        this.contagiousDays = contagiousDays;
    }

    public int getIdentifier() { return identifier; }
}
