package ar.edu.unp.madryn.livremarket.monitor.gui;

import javax.swing.*;

public class ControlPanel {
    private JTabbedPane purchases;
    private JTextField purchases_purchase_id;
    private JTextField purchases_state;
    private JTextField purchases_date;
    private JTextField purchases_server_state;
    private JTabbedPane payments;
    private JTabbedPane products;
    private JTabbedPane deliveries;
    private JTabbedPane infractions;
    private JButton purchases_generate_event_button;
    private JButton purchases_restart_button;
    private JButton purchases_shutdown_button;
    private JTextField products_purchase_id;
    private JTextField products_state;
    private JTextField products_date;
    private JTextField products_server_state;
    private JTextField payments_purchase_id;
    private JTextField payments_state;
    private JTextField payments_date;
    private JTextField payments_server_state;
    private JTextField deliveries_purchase_id;
    private JTextField deliveries_state;
    private JTextField deliveries_date;
    private JTextField deliveries_server_state;
    private JTextField infractions_purchase_id;
    private JTextField infractions_state;
    private JTextField infractions_date;
    private JTextField infractions_server_state;
    private JButton products_generate_event_button;
    private JButton products_restart_button;
    private JButton products_shutdown_button;
    private JButton payments_generate_event_button;
    private JButton payments_restart_button;
    private JButton payments_shutdown_button;
    private JButton deliveries_generate_event_button;
    private JButton deliveries_restart_button;
    private JButton deliveries_shutdown_button;
    private JButton infractions_generate_event_button;
    private JButton infractions_restart_button;
    private JButton infractions_shutdown_button;
    private JPanel control_gui;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("ControlPanel");
        frame.setContentPane(new ControlPanel().control_gui);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
