package p1;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Algos {
    JFrame f1;
    JLabel lb1, lb2, lb3, lb4;
    JTextField txt1, txt2, txt3, txt4;
    JButton add, fcfs, sjf, priority, rr;
    DefaultTableModel model;
    JTable table;
    JScrollPane js;

    public Algos() {
        f1 = new JFrame("My Form");
        lb1 = new JLabel("Process");
        lb2 = new JLabel("CPU Burst");
        lb3 = new JLabel("Arrival Time");
        lb4 = new JLabel("Priority");
        txt1 = new JTextField();
        txt2 = new JTextField();
        txt3 = new JTextField();
        txt4 = new JTextField();
        add = new JButton("ADD");
        fcfs = new JButton("FCFS");
        sjf = new JButton("SJF");
        priority = new JButton("Priority");
        rr = new JButton("Round Robin");

        model = new DefaultTableModel();
        model.addColumn("Process");
        model.addColumn("CPU Burst");
        model.addColumn("Arrival Time");
        model.addColumn("Priority");
        model.addColumn("Waiting Time");
        model.addColumn("Turn Around Time");
        model.addColumn("Response Time");
        table = new JTable(model);
        js = new JScrollPane(table);

        Container c = f1.getContentPane();
        c.setLayout(new GridLayout(8, 2, 10, 10));
        c.add(lb1);
        c.add(txt1);
        c.add(lb2);
        c.add(txt2);
        c.add(lb3);
        c.add(txt3);
        c.add(lb4);
        c.add(txt4);
        c.add(add);
        c.add(fcfs);
        c.add(sjf);
        c.add(priority);
        c.add(rr);
        c.add(js);

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addrow();
            }
        });

        fcfs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runFCFS();
            }
        });

        sjf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runSJF();
            }
        });

        priority.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runPriority();
            }
        });

        rr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runRoundRobin();
            }
        });

        f1.setSize(1200, 800);
        f1.setVisible(true);
    }

    private void addrow() {
        String s1 = txt1.getText();
        String s2 = txt2.getText();
        String s3 = txt3.getText();
        String s4 = txt4.getText();
        model.addRow(new Object[]{s1, s2, s3, s4, "", "", ""});
        txt1.setText("");
        txt2.setText("");
        txt3.setText("");
        txt4.setText("");
    }

    private void runFCFS() {
        int n = model.getRowCount();
        if (n == 0) return;

        String[] name = new String[n];
        int[] burst = new int[n];
        int[] arrival = new int[n];
        int[] prio = new int[n];

        for (int i = 0; i < n; i++) {
            name[i] = model.getValueAt(i, 0).toString();
            burst[i] = Integer.parseInt(model.getValueAt(i, 1).toString());
            arrival[i] = Integer.parseInt(model.getValueAt(i, 2).toString());
            prio[i] = Integer.parseInt(model.getValueAt(i, 3).toString());
        }

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arrival[j] > arrival[j + 1]) {
                    int temp = arrival[j]; arrival[j] = arrival[j+1]; arrival[j+1] = temp;
                    temp = burst[j]; burst[j] = burst[j+1]; burst[j+1] = temp;
                    temp = prio[j]; prio[j] = prio[j+1]; prio[j+1] = temp;
                    String t = name[j]; name[j] = name[j+1]; name[j+1] = t;
                }
            }
        }

        int[] wt = new int[n];
        int[] tat = new int[n];
        int[] rt = new int[n];
        int currentTime = 0;

        for (int i = 0; i < n; i++) {
            if (currentTime < arrival[i]) {
                currentTime = arrival[i];
            }
            rt[i] = currentTime - arrival[i];
            wt[i] = currentTime - arrival[i];
            currentTime = currentTime + burst[i];
            tat[i] = currentTime - arrival[i];
        }

        model.setRowCount(0);
        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{name[i], burst[i], arrival[i], prio[i], wt[i], tat[i], rt[i]});
        }
    }

    private void runSJF() {
        int n = model.getRowCount();
        if (n == 0) return;

        String[] name = new String[n];
        int[] burst = new int[n];
        int[] arrival = new int[n];
        int[] prio = new int[n];

        for (int i = 0; i < n; i++) {
            name[i] = model.getValueAt(i, 0).toString();
            burst[i] = Integer.parseInt(model.getValueAt(i, 1).toString());
            arrival[i] = Integer.parseInt(model.getValueAt(i, 2).toString());
            prio[i] = Integer.parseInt(model.getValueAt(i, 3).toString());
        }

        int[] wt = new int[n];
        int[] tat = new int[n];
        int[] rt = new int[n];
        boolean[] done = new boolean[n];

        int currentTime = 0;
        int completed = 0;

        while (completed < n) {
            int shortest = -1;
            int minBurst = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!done[i] && arrival[i] <= currentTime) {
                    if (burst[i] < minBurst) {
                        minBurst = burst[i];
                        shortest = i;
                    }
                }
            }

            if (shortest == -1) {
                currentTime++;
                continue;
            }

            rt[shortest] = currentTime - arrival[shortest];
            wt[shortest] = currentTime - arrival[shortest];
            currentTime = currentTime + burst[shortest];
            tat[shortest] = currentTime - arrival[shortest];
            done[shortest] = true;
            completed++;
        }

        model.setRowCount(0);
        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{name[i], burst[i], arrival[i], prio[i], wt[i], tat[i], rt[i]});
        }
    }

    private void runPriority() {
        int n = model.getRowCount();
        if (n == 0) return;

        String[] name = new String[n];
        int[] burst = new int[n];
        int[] arrival = new int[n];
        int[] prio = new int[n];

        for (int i = 0; i < n; i++) {
            name[i] = model.getValueAt(i, 0).toString();
            burst[i] = Integer.parseInt(model.getValueAt(i, 1).toString());
            arrival[i] = Integer.parseInt(model.getValueAt(i, 2).toString());
            prio[i] = Integer.parseInt(model.getValueAt(i, 3).toString());
        }

        int[] wt = new int[n];
        int[] tat = new int[n];
        int[] rt = new int[n];
        boolean[] done = new boolean[n];

        int currentTime = 0;
        int completed = 0;

        while (completed < n) {
            int selected = -1;
            int highestPrio = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!done[i] && arrival[i] <= currentTime) {
                    if (prio[i] < highestPrio) {
                        highestPrio = prio[i];
                        selected = i;
                    }
                }
            }

            if (selected == -1) {
                currentTime++;
                continue;
            }

            rt[selected] = currentTime - arrival[selected];
            wt[selected] = currentTime - arrival[selected];
            currentTime = currentTime + burst[selected];
            tat[selected] = currentTime - arrival[selected];
            done[selected] = true;
            completed++;
        }

        model.setRowCount(0);
        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{name[i], burst[i], arrival[i], prio[i], wt[i], tat[i], rt[i]});
        }
    }

    private void runRoundRobin() {
        int n = model.getRowCount();
        if (n == 0) return;

        int quantum = 2;

        String[] name = new String[n];
        int[] burst = new int[n];
        int[] arrival = new int[n];
        int[] prio = new int[n];

        for (int i = 0; i < n; i++) {
            name[i] = model.getValueAt(i, 0).toString();
            burst[i] = Integer.parseInt(model.getValueAt(i, 1).toString());
            arrival[i] = Integer.parseInt(model.getValueAt(i, 2).toString());
            prio[i] = Integer.parseInt(model.getValueAt(i, 3).toString());
        }

        int[] remaining = new int[n];
        int[] wt = new int[n];
        int[] tat = new int[n];
        int[] rt = new int[n];
        boolean[] firstTime = new boolean[n];

        for (int i = 0; i < n; i++) {
            remaining[i] = burst[i];
            firstTime[i] = true;
        }

        int currentTime = 0;
        int completed = 0;

        while (completed < n) {
            boolean anyProcessRan = false;

            for (int i = 0; i < n; i++) {
                if (remaining[i] > 0 && arrival[i] <= currentTime) {
                    anyProcessRan = true;

                    if (firstTime[i]) {
                        rt[i] = currentTime - arrival[i];
                        firstTime[i] = false;
                    }

                    if (remaining[i] <= quantum) {
                        currentTime = currentTime + remaining[i];
                        remaining[i] = 0;
                        tat[i] = currentTime - arrival[i];
                        wt[i] = tat[i] - burst[i];
                        completed++;
                    } else {
                        currentTime = currentTime + quantum;
                        remaining[i] = remaining[i] - quantum;
                    }
                }
            }

            if (!anyProcessRan) {
                currentTime++;
            }
        }

        model.setRowCount(0);
        for (int i = 0; i < n; i++) {
            model.addRow(new Object[]{name[i], burst[i], arrival[i], prio[i], wt[i], tat[i], rt[i]});
        }
    }

    public static void main(String[] args) {
        new Algos();
    }
}