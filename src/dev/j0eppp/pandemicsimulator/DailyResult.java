package dev.j0eppp.pandemicsimulator;

public class DailyResult<F, S/*, T*/> {
    private F infections;
    private S immunity;
//    private T reproduction;

    public DailyResult(F infections, S immunity/*, T reproduction*/) {
        this.infections = infections;
        this.immunity = immunity;
//        this.reproduction = reproduction;
    }

    public F getInfections() {
        return infections;
    }

    public S getImmunity() {
        return immunity;
    }

//    public T getReproduction() {
//        return reproduction;
//    }
}
