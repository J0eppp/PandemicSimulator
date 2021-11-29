/*
 * PandemicSimulator
 * Author: Joep van Dijk (J0eppp)
 * Source: https://towardsdatascience.com/simulating-the-pandemic-in-python-2aa8f7383b55
 */

package dev.j0eppp.pandemicsimulator;

import java.util.ArrayList;
import java.util.Random;

public class PandemicSimulator {

    private ArrayList<Person> persons;
    private int amountOfPersons;
    private float startingImmunity;
    private int startingInfectious;
    private int daysContagious;

    public static void main(String[] args) {
        PandemicSimulator simulator = new PandemicSimulator(1000000, 1F, 30, 8);
        simulator.startSimulation();
    }

    public PandemicSimulator(int amountOfPersons, float startingImmunity, int startingInfectious, int daysContagious) {
        this.persons = new ArrayList<>();
        this.amountOfPersons = amountOfPersons;
        this.startingImmunity = startingImmunity;
        this.startingInfectious = startingInfectious;
        this.daysContagious = daysContagious;
        System.out.println("Running PandemicSimulator");
    }

    public void startSimulation() {
        Random random = new Random();
        for (int i = 0; i < amountOfPersons; i++) {
            float immunity = 0F;
            if (random.nextInt(100) < startingImmunity) {
                immunity = 90F;
            }
            float contagiousness = 0;
            boolean mask = false;
            int amountOfFriends = (int) Math.abs(10 + random.nextGaussian() * 5);
            Person person = new Person(i, immunity, contagiousness, mask, amountOfFriends);
            persons.add(person);
        }

        setStartingContagiousness(random);

        float[] immunity = new float[100];
        int[] infections = new int[100];
        int totalInfections = 0;

        for (int i = 0; i < 100; i++) {
            DailyResult result = nextDay(random, i);
            int dailyInfections = (int) result.getInfections();
            infections[i] = dailyInfections;
            float dailyImmunity = (float) result.getImmunity();
            immunity[i] = dailyImmunity;
            totalInfections += dailyInfections;
        }

        System.out.println("-========================-");
        System.out.println("Total infections: " + totalInfections);

        // Graphing things
        SwingWorkerRealTime swingWorkerRealTimeInfections = new SwingWorkerRealTime();
        swingWorkerRealTimeInfections.go(infections);

        SwingWorkerRealTime swingWorkerRealTimeImmunity = new SwingWorkerRealTime();
        swingWorkerRealTimeImmunity.go(immunity);

    }

    public DailyResult nextDay(Random random, int day) {
        System.out.println("It is now day: " + day);
        int contagious = 0;
        float averageImmunity = 0;

        // Simulate the spread of infection
        int infectedToday = 0;
        for (Person p : persons) {
            // Immunity decreases over time
            // Testing with 0.5% a day
            final float immunityDecrease = 0.3F;
            if (p.getImmunity() < immunityDecrease) {
                p.setImmunity(0F);
            } else {
                p.setImmunity(p.getImmunity() - immunityDecrease);
            }
            if (p.getContagiousness() > 0 && p.getAmountOfFriends() > 0) {
                int peopleCouldMeetToday = p.getAmountOfFriends() / 2;
                int peopleMetToday = 0;
                if (peopleCouldMeetToday > 0) {
                    peopleMetToday = random.nextInt(peopleCouldMeetToday);
                }

                for (int i = 0; i < peopleMetToday; i++) {
                    // Get a random person
                    Person friend = persons.get(random.nextInt(persons.size() - 1));
                    if (friend.getContagiousness() == 0 && friend.getImmunity() <= random.nextFloat() * 100 && random.nextInt(100) < p.getContagiousness()) {
                        // Infected a friend
                        setContagiousness(random, friend);
                        infectedToday++;
                    }
                }
            }

            // Get every infected person and increase their infectiousDays by 1
            // If they have been infected longer than daysContagious, set contagiousness to 0 and immunity to true
            if (p.getContagiousness() > 0) {
                p.setContagiousDays(p.getContagiousDays() + 1);
                if (p.getContagiousDays() > daysContagious) {
                    p.setImmunity(70F);
                    p.setContagiousness(0);
                    p.setContagiousDays(0);
                }
            }
            if (p.getContagiousness() > 0) {
                contagious++;
            }
            averageImmunity += p.getImmunity();
        }
        System.out.println("Immunity: " + averageImmunity / persons.size());
        System.out.println("People contagious: " + contagious);
        System.out.println("-====================-");
        return new DailyResult(infectedToday, averageImmunity / persons.size());
    }

    private void setStartingContagiousness(Random random) {
        ArrayList<Integer> personsInfectious = new ArrayList<>();
        for (int i = 0; i < startingInfectious; i++) {
            int index = random.nextInt(persons.size() - 1);
            while (personsInfectious.contains(index)) {
                index = random.nextInt(persons.size() - 1);
            }
            setContagiousness(random, index);
            personsInfectious.add(index);
        }

    }

    private void setContagiousness(Random random, int index) {
        Person person = persons.get(index);
        setContagiousness(random, person);
    }

    private void setContagiousness(Random random, Person person) {
        person.setContagiousness((float) Math.abs(random.nextGaussian() * 10));
    }
}
