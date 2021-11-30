/*
 * PandemicSimulator
 * Author: Joep van Dijk (J0eppp)
 * Source: https://towardsdatascience.com/simulating-the-pandemic-in-python-2aa8f7383b55
 */

package dev.j0eppp.pandemicsimulator;

import java.util.ArrayList;
import java.util.Random;

public class PandemicSimulator {

    private final ArrayList<Person> persons;
    private final int amountOfPersons;
    private final float startingImmunity;
    private final int startingInfectious;
    private final int daysContagious;
    private final int amountOfDays;

    private static int parseInt(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.err.println("Argument " + arg + " must be an integer");
            System.exit(1);
        }
        return 0;
    }

    private static float parseFloat(String arg) {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            System.err.println("Argument " + arg + " must be a float");
            System.exit(1);
        }
        return 0F;
    }

    public static void main(String[] args) {
        // Parse args
        if (args.length < 5) {
            System.err.println("Please provide all the arguments required");
            System.exit(1);
        }
        // First argument is the amount of persons
        int amountOfPersons = parseInt(args[0]);
        // Second argument is the starting immunity
        float startingImmunity = parseFloat(args[1]);
        // Third argument is how many people are contagious at the start
        int startingContagious = parseInt(args[2]);
        // Fourth argument is how many days someone is contagious
        int daysContagious = parseInt(args[3]);
        // Fifth argument is how many days the simulator should simulate
        int amountOfDays = parseInt(args[4]);
        PandemicSimulator simulator = new PandemicSimulator(amountOfPersons, startingImmunity, startingContagious, daysContagious, amountOfDays);
        simulator.startSimulation();
    }

    public PandemicSimulator(int amountOfPersons, float startingImmunity, int startingInfectious, int daysContagious, int amountOfDays) {
        this.persons = new ArrayList<>();
        this.amountOfPersons = amountOfPersons;
        this.startingImmunity = startingImmunity;
        this.startingInfectious = startingInfectious;
        this.daysContagious = daysContagious;
        this.amountOfDays = amountOfDays;
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

        ArrayList<Float> immunity = new ArrayList<>();
        ArrayList<Integer> infections = new ArrayList<>();
        int totalInfections = 0;

        for (int i = 0; i < amountOfDays; i++) {
            DailyResult<Integer, Float> result = nextDay(random, i);
            int dailyInfections = result.getInfections();
            infections.add(dailyInfections);
            totalInfections += dailyInfections;
            float dailyImmunity = result.getImmunity();
            immunity.add(dailyImmunity);
        }

        System.out.println("-========================-");
        System.out.println("Total infections: " + totalInfections);

        // Graphing things
        SwingWorkerRealTime swingWorkerRealTimeInfections = new SwingWorkerRealTime();
        swingWorkerRealTimeInfections.go(infections.stream().mapToInt(i -> i).toArray());

        SwingWorkerRealTime swingWorkerRealTimeImmunity = new SwingWorkerRealTime();
        swingWorkerRealTimeImmunity.go(immunity.stream().mapToDouble(i -> i).toArray());
    }

    public DailyResult<Integer, Float> nextDay(Random random, int day) {
        System.out.println("It is now day: " + day);
        int contagious = 0;
        float averageImmunity = 0;

        // Simulate the spread of infection
        int infectedToday = 0;
        ArrayList<Integer> personsInfected = new ArrayList<>();
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
                    // Healed
                    p.setImmunity(100F);
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
        return new DailyResult<>(infectedToday, averageImmunity / persons.size());
    }

    private void setStartingContagiousness(Random random) {
        // This is very inefficient
        // With a lot of contagious people at the start it's really slow to start
        // TODO make this more efficient
        // Maybe just take the [0, startingInfectious] range and make them contagious
        if (persons.size() < startingInfectious) {
            System.err.println("You cannot have more infectious persons than persons in total");
            System.exit(1);
        }
        for (int i = 0; i < startingInfectious; i++) {
            setContagiousness(random, i);
        }
//        ArrayList<Integer> personsInfectious = new ArrayList<>();
//        for (int i = 0; i < startingInfectious; i++) {
//            int index = random.nextInt(persons.size() - 1);
//            while (personsInfectious.contains(index)) {
//                index = random.nextInt(persons.size() - 1);
//            }
//            setContagiousness(random, index);
//            personsInfectious.add(index);
//        }
    }

    private void setContagiousness(Random random, int index) {
        Person person = persons.get(index);
        setContagiousness(random, person);
    }

    private void setContagiousness(Random random, Person person) {
        person.setContagiousness((float) Math.abs(random.nextGaussian() * 10));
    }
}
