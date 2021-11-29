package dev.j0eppp.pandemicsimulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import dev.j0eppp.pandemicsimulator.Person;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

/**
 * Creates a real-time chart using SwingWorker
 */
public class SwingWorkerRealTime {

    MySwingWorker mySwingWorker;
    SwingWrapper<XYChart> sw;
    XYChart chart;

//    public static void main(String[] args) throws Exception {
//
//        SwingWorkerRealTime swingWorkerRealTime = new SwingWorkerRealTime();
//        swingWorkerRealTime.go();
//    }

    public void go(int[] infections) {
// Create Chart
        double[] array = new double[infections.length];
        double[] yarray = new double[infections.length];
        for (int i = 0; i < infections.length; i++) {
            array[i] = infections[i];
            yarray[i] = i;
        }
        chart = QuickChart.getChart("Infections", "Time", "Infections", "randomWalk", yarray, array);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisTicksVisible(true);

        // Show it
        sw = new SwingWrapper<XYChart>(chart);
        sw.displayChart();

        mySwingWorker = new MySwingWorker(infections);
        mySwingWorker.execute();
    }

    public void go(float[] immunity) {
        // Create Chart
        double[] array = new double[immunity.length];
        double[] yarray = new double[immunity.length];
        for (int i = 0; i < immunity.length; i++) {
            array[i] = immunity[i];
            yarray[i] = i;
        }
        chart = QuickChart.getChart("Immunity", "Time", "Immunity", "randomWalk", yarray, array);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisTicksVisible(true);

        // Show it
        sw = new SwingWrapper<XYChart>(chart);
        sw.displayChart();

        mySwingWorker = new MySwingWorker(immunity);
        mySwingWorker.execute();
    }

    private class MySwingWorker extends SwingWorker<Boolean, double[]> {

//        LinkedList<Float> fifo = new LinkedList<>();
//        ArrayList<Person> persons;
        int[] infections;
        float[] immunity;

        public MySwingWorker(int[] infections) {
//            this.persons = persons;
//            fifo.add(calcImmunity());
//            fifo.add(0.0);
            this.infections = infections;
        }

        public MySwingWorker(float[] immunity) {
            this.immunity = immunity;
        }

//        private float calcImmunity() {
//            float immunity = 0;
//            for (Person p : persons) {
//                immunity += p.getImmunity();
//            }
//
//            return immunity / persons.size();
//        }

        @Override
        protected Boolean doInBackground() throws Exception {
            while (!isCancelled()) {

//                fifo.add((float) (fifo.get(fifo.size() - 1) + Math.random() - .5));
//                fifo.add(calcImmunity());
//                if (fifo.size() > 500) {
//                    fifo.removeFirst();
//                }

                double[] array = new double[infections.length];
                if (infections.length != 0) {
                    for (int i = 0; i < infections.length; i++) {
                        array[i] = infections[i];
                    }
                } else {
                    for (int i = 0; i < immunity.length; i++) {
                        array[i] = immunity[i];
                    }
                }

                publish(array);


                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    // eat it. caught when interrupt is called
                    System.out.println("MySwingWorker shut down.");
                }

            }

            return true;
        }

//        @Override
//        protected void process(List<double[]> chunks) {
//
//            System.out.println("number of chunks: " + chunks.size());
//
//            double[] mostRecentDataSet = chunks.get(chunks.size() - 1);
//
//            chart.updateXYSeries("randomWalk", null, mostRecentDataSet, null);
//            sw.repaintChart();
//
//            long start = System.currentTimeMillis();
//            long duration = System.currentTimeMillis() - start;
//            try {
//                Thread.sleep(40 - duration); // 40 ms ==> 25fps
//                // Thread.sleep(400 - duration); // 40 ms ==> 2.5fps
//            } catch (InterruptedException e) {
//            }
//
//        }
    }
}