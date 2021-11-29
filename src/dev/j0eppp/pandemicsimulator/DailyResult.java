package dev.j0eppp.pandemicsimulator;

public class DailyResult<F, S> {
    private F infections;
    private S immunity;

    public DailyResult(F infections, S immunity) {
        this.infections = infections;
        this.immunity = immunity;
    }

    public F getInfections() {
        return infections;
    }

    public S getImmunity() {
        return immunity;
    }
}
