package de.sciss.docking;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.*;
import bibliothek.gui.dock.common.intern.CDockController;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ExternalizeDebug implements Runnable {
    public static void main(String[] args) {
        EventQueue.invokeLater(new ExternalizeDebug());
    }

    public void run() {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        CControl control = new CControl(frame);
        frame.setLayout(new GridLayout(1, 1));
        frame.add(control.getContentArea());
        SingleCDockable red = create("Red", Color.RED);
        SingleCDockable green = create("Green", Color.GREEN);
        SingleCDockable blue = create("Blue", Color.BLUE);
        control.addDockable(red);
        control.addDockable(green);
        control.addDockable(blue);
        red.setVisible(true);
        green.setLocation(CLocation.base().normalSouth(0.4));
        green.setVisible(true);
        blue.setLocation(CLocation.base().normalEast(0.3));
        blue.setVisible(true);
        frame.setBounds(20, 20, 400, 400);
        frame.setVisible(true);

        final DockController dc = control.getController();
//        final int i = dc.getStationCount();
//        // System.out.println(i);
//        for (int j = 0; j < i; j++) {
//            // System.out.println(station);
//            // if (station instanceof CentralStation) {
//            final DockStation station = dc.getStation(j);
//            station.addDockStationListener(new StatList(j));
//        }

        dc.getStation(0).addDockStationListener(new DockStationAdapter() {
            @Override
            public void dockableAdded(DockStation station, Dockable dockable) {
                final Component dc = dockable.getComponent();
                if (station instanceof ScreenDockStation) {
                    final ScreenDockStation sds = (ScreenDockStation) station;
                    final ScreenDockWindow sdw = sds.getWindow(dockable);
                    final Component c = sdw.getComponent();
                    if (c instanceof JDialog && false) {
                        final JDialog dlg = (JDialog) c;
                        if (dlg.isUndecorated()) {
                            // System.out.println("Foo");
                            final Rectangle bounds = dlg.getBounds();
                            dlg.pack();
                            dlg.setBounds(bounds);
//                            dlg.invalidate();
//                            dlg.validate();
////                            dlg.repaint();
//                            dlg.repaint();
//                            final Container dlgC = dlg.getContentPane();
//                            if (dc instanceof JComponent) {
//                                final JComponent dcj = (JComponent) dc;
//                                System.out.println("Bar " + dcj.getClass().getSimpleName() + "; " + dlgC.getClass().getSimpleName());
//                                dcj.revalidate();
//                                dcj.repaint();
//                            }
//                            if (dlgC instanceof JComponent) {
//                                final JComponent dcj = (JComponent) dlgC;
//                                final Border border = dcj.getBorder();
//                                System.out.println("Bar " + dcj.getClass().getSimpleName() + "; " + border);
//                                dcj.revalidate();
//                                dcj.repaint();
//                            }
//                            dlg.requestFocus();
                        }
                    }
                }
            }
        });

        // ((CDockController) control.getController()).getStationCount()
    }

    private static class StatList implements DockStationListener {
        private final int id;

        StatList(int id) {
            this.id = id;
        }

        public void dockableAdding(DockStation station, Dockable dockable) {
            System.out.println(id + ": dockableAdding(" + station + ", " + dockable + ")");
        }

        public void dockableRemoving(DockStation station, Dockable dockable) {
            System.out.println(id + ": dockableRemoving(" + station + ", " + dockable + ")");
        }

        public void dockableAdded(DockStation station, Dockable dockable) {
            System.out.println(id + ": dockableAdded(" + station + ", " + dockable + ")");
        }

        public void dockableRemoved(DockStation station, Dockable dockable) {
            System.out.println(id + ": dockableRemoved(" + station + ", " + dockable + ")");
        }

        public void dockableShowingChanged(DockStation station, Dockable dockable, boolean showing) {
            System.out.println(id + ": dockableShowingChanged(" + station + ", " + dockable + ")");
        }

        public void dockableSelected(DockStation station, Dockable oldSelection, Dockable newSelection) {
            System.out.println(id + ": dockableSelected(" + station + ", " + oldSelection + ", " + newSelection + ")");
        }

        public void dockablesRepositioned(DockStation station, Dockable[] dockables) {
            System.out.println(id + ": dockablesRepositioned(" + station + ", " + dockables + ")");
        }
    }

    private static SingleCDockable create(String title, Color color) {
        JPanel background = new JPanel();

        background.setOpaque(true);
        background.setBackground(color);

        return new DefaultSingleCDockable(title, background);
    }
}