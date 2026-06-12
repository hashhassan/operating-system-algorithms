package p1;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class page {
    JFrame f1;
    JLabel lb1, lb2, lb3, lb4, lb5, lb6, lb7, lb8;
    JTextField txt1, txt2, txt3, txt4, txt5;
    JTextField frameInput, logicalInput, physicalOutput;
    JButton add, assign, convert;
    DefaultTableModel model;
    JTable table;
    JScrollPane js;

    int totalMemory, frameSize, processSize;
    int totalFrames, totalPages;
    int currentPage = 0;

    public page() {
        f1 = new JFrame("Paging System");

        lb1 = new JLabel("Total Memory (KB):");
        lb2 = new JLabel("Frame Size (KB):");
        lb3 = new JLabel("Process Size (KB):");
        lb4 = new JLabel("Total Frames:");
        lb5 = new JLabel("Total Pages:");
        lb6 = new JLabel("Enter Frame No:");
        lb7 = new JLabel("Logical Address:");
        lb8 = new JLabel("Physical Address:");

        txt1 = new JTextField();
        txt2 = new JTextField();
        txt3 = new JTextField();
        txt4 = new JTextField();
        txt4.setEditable(false);
        txt5 = new JTextField();
        txt5.setEditable(false);

        frameInput   = new JTextField();
        logicalInput  = new JTextField();
        physicalOutput = new JTextField();
        physicalOutput.setEditable(false);

        add= new JButton("Calculate");
        assign= new JButton("Assign Frame");
        convert= new JButton("Convert Address");
        assign.setEnabled(false);
        convert.setEnabled(false);

        model = new DefaultTableModel();
        model.addColumn("Page No");
        model.addColumn("Frame No");

        table = new JTable(model);
        js = new JScrollPane(table);

        Container c = f1.getContentPane();
        c.setLayout(new GridLayout(11, 2, 10, 10));

        c.add(lb1);           c.add(txt1);
        c.add(lb2);           c.add(txt2);
        c.add(lb3);           c.add(txt3);
        c.add(lb4);           c.add(txt4);
        c.add(lb5);           c.add(txt5);
        c.add(lb6);           c.add(frameInput);
        c.add(add);           c.add(assign);
        c.add(lb7);           c.add(logicalInput);
        c.add(lb8);           c.add(physicalOutput);
        c.add(convert);       c.add(new JLabel(""));
        c.add(new JLabel("Page Table:")); c.add(js);

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculate();
                frameInput.setText("");
                assign.setEnabled(true);
                convert.setEnabled(false);
                physicalOutput.setText("");
            }
        });

        assign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignframe();
            }
        });

        convert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                convertAddress();
            }
        });

        f1.setSize(520, 650);
        f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f1.setVisible(true);
    }

    private void calculate() {
        try {
            totalMemory = Integer.parseInt(txt1.getText().trim());
            frameSize   = Integer.parseInt(txt2.getText().trim());
            processSize = Integer.parseInt(txt3.getText().trim());

            if (frameSize <= 0 || totalMemory <= 0 || processSize <= 0) {
                JOptionPane.showMessageDialog(f1, "Please enter values greater than 0.");
                return;
            }

            totalFrames = totalMemory / frameSize;
            totalPages  = (int) Math.ceil((double) processSize / frameSize);

            txt4.setText(String.valueOf(totalFrames));
            txt5.setText(String.valueOf(totalPages));

            model.setRowCount(0);
            currentPage = 0;

            JOptionPane.showMessageDialog(f1,
                "Total Frames: " + totalFrames +
                "\nTotal Pages: "  + totalPages  +
                "\nNow enter frame numbers for each page.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(f1, "Please enter valid numbers!");
        }
    }

    private void assignframe() {
        if (currentPage >= totalPages) {
            JOptionPane.showMessageDialog(f1, "All pages have been assigned already!");
            assign.setEnabled(false);
            return;
        }

        try {
            int frameNo = Integer.parseInt(frameInput.getText().trim());

            if (frameNo < 0 || frameNo >= totalFrames) {
                JOptionPane.showMessageDialog(f1,
                    "Invalid frame number! Enter between 0 and " + (totalFrames - 1));
                return;
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                if (Integer.parseInt(model.getValueAt(i, 1).toString()) == frameNo) {
                    JOptionPane.showMessageDialog(f1, "Frame " + frameNo + " is already assigned!");
                    return;
                }
            }

            model.addRow(new Object[]{currentPage, frameNo});
            currentPage++;
            frameInput.setText("");

            if (currentPage == totalPages) {
                JOptionPane.showMessageDialog(f1, "All pages assigned! You can now convert addresses.");
                assign.setEnabled(false);
                convert.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(f1,
                    "Page " + (currentPage - 1) + " assigned to Frame " + frameNo +
                    "\nEnter frame for Page " + currentPage);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(f1, "Please enter a valid frame number!");
        }
    }

    private void convertAddress() {
        try {
            int logicalAddress = Integer.parseInt(logicalInput.getText().trim());

            if (logicalAddress < 0) {
                JOptionPane.showMessageDialog(f1, "Logical address cannot be negative!");
                return;
            }

            int pageNo = logicalAddress / frameSize;
            int offset = logicalAddress % frameSize;

            if (pageNo >= totalPages) {
                JOptionPane.showMessageDialog(f1,
                    "Invalid logical address!\n" +
                    "Page No " + pageNo + " does not exist.\n" +
                    "Valid range: 0 to " + (processSize - 1));
                return;
            }

            int frameNo = -1;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (Integer.parseInt(model.getValueAt(i, 0).toString()) == pageNo) {
                    frameNo = Integer.parseInt(model.getValueAt(i, 1).toString());
                    break;
                }
            }

            if (frameNo == -1) {
                JOptionPane.showMessageDialog(f1, "Page not found in page table!");
                return;
            }

            int physicalAddress = (frameNo * frameSize) + offset;

            physicalOutput.setText(String.valueOf(physicalAddress));

            JOptionPane.showMessageDialog(f1,
                "--- Address Translation ---\n" +
                "Logical Address  : " + logicalAddress + "\n" +
                "Page Number      : " + pageNo        + "\n" +
                "Offset           : " + offset        + "\n" +
                "Frame Number     : " + frameNo       + "\n" +
                "Physical Address : " + physicalAddress);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(f1, "Please enter a valid logical address!");
        }
    }

    public static void main(String[] args) {
        new page();
    }
}