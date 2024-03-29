package ar.edu.unp.madryn.livremarket.monitor.gui;

import ar.edu.unp.madryn.livremarket.common.comunication.CommunicationHandler;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationManager;
import ar.edu.unp.madryn.livremarket.common.configuration.ConfigurationSection;
import ar.edu.unp.madryn.livremarket.common.messages.Controls;
import ar.edu.unp.madryn.livremarket.common.messages.MessageHandlerManager;
import ar.edu.unp.madryn.livremarket.common.messages.MessageType;
import ar.edu.unp.madryn.livremarket.common.threads.MessageWorker;
import ar.edu.unp.madryn.livremarket.common.utils.Definitions;
import ar.edu.unp.madryn.livremarket.common.utils.Logging;
import ar.edu.unp.madryn.livremarket.monitor.messages.ResultMessageHandler;
import ar.edu.unp.madryn.livremarket.monitor.process.ProcessManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlPanel {
    private static CommunicationHandler communicationHandler;

    private JPanel control_gui;
    private JTabbedPane purchases;
    private JTextField purchases_purchase_id;
    private JTextField purchases_state;
    private JTextField purchases_date;
    private JTextField purchases_server_state;
    private JButton purchases_generate_event_button;
    private JButton purchases_restart_button;
    private JButton purchases_shutdown_button;
    private JPanel purchases_panel;
    private JTabbedPane products;
    private JTextField products_purchase_id;
    private JTextField products_state;
    private JTextField products_date;
    private JTextField products_server_state;
    private JButton products_generate_event_button;
    private JButton products_restart_button;
    private JButton products_shutdown_button;
    private JPanel products_panel;
    private JPanel purchases_actions;
    private JPanel products_actions;
    private JPanel payments_panel;
    private JTabbedPane payments;
    private JTextField payments_purchase_id;
    private JTextField payments_state;
    private JTextField payments_date;
    private JTextField payments_server_state;
    private JPanel payments_actions;
    private JButton payments_generate_event_button;
    private JButton payments_restart_button;
    private JButton payments_shutdown_button;
    private JPanel common_panel;
    private JPanel deliveries_panel;
    private JPanel infractions_panel;
    private JTabbedPane deliveries;
    private JPanel deliveries_actions;
    private JTextField deliveries_purchase_id;
    private JTextField deliveries_state;
    private JTextField deliveries_date;
    private JTextField deliveries_server_state;
    private JButton deliveries_generate_event_button;
    private JButton deliveries_restart_button;
    private JButton deliveries_shutdown_button;
    private JTabbedPane infractions;
    private JPanel infractions_actions;
    private JTextField infractions_purchase_id;
    private JTextField infractions_state;
    private JTextField infractions_date;
    private JTextField infractions_server_state;
    private JButton infractions_generate_event_button;
    private JButton infractions_restart_button;
    private JButton infractions_shutdown_button;
    private JTable purchases_messages;
    private JTable products_messages;
    private JTable payments_messages;
    private JTable deliveries_messages;
    private JTable infractions_messages;
    private JTable purchases_last_state;
    private JTable products_last_state;
    private JTable payments_last_state;
    private JTable deliveries_last_state;
    private JTable infractions_last_state;
    private JButton purchases_start_button;
    private JButton payments_start_button;
    private JButton deliveries_start_button;
    private JButton infractions_start_button;
    private JButton products_start_button;
    private JButton purchases_step_button;
    private JButton payments_step_button;
    private JButton deliveries_step_button;
    private JButton infractions_step_button;
    private JButton products_step_button;
    private JButton purchases_view_logs_button;
    private JButton payments_view_logs_button;
    private JButton deliveries_view_logs_button;
    private JButton infractions_view_logs_button;
    private JButton products_view_logs_button;
    private JButton quit_button;
    private JButton shutdown_all_button;

    public ControlPanel() {
        deliveries_start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (initServer(Definitions.DELIVERIES_SERVER_NAME)) {
                    deliveries_server_state.setText("Encendido");
                    deliveries_start_button.setEnabled(false);
                    deliveries_shutdown_button.setEnabled(true);
                }
            }
        });
        deliveries_shutdown_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (killServer(Definitions.DELIVERIES_SERVER_NAME)) {
                    deliveries_server_state.setText("Apagado");
                    deliveries_shutdown_button.setEnabled(false);
                    deliveries_start_button.setEnabled(true);
                }
            }
        });
        deliveries_view_logs_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLogs(Definitions.DELIVERIES_SERVER_NAME);
            }
        });
        deliveries_step_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestStep(Definitions.DELIVERIES_SERVER_NAME);
            }
        });

        infractions_start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (initServer(Definitions.INFRACTIONS_SERVER_NAME)) {
                    infractions_server_state.setText("Encendido");
                    infractions_start_button.setEnabled(false);
                    infractions_shutdown_button.setEnabled(true);
                }
            }
        });
        infractions_shutdown_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (killServer(Definitions.INFRACTIONS_SERVER_NAME)) {
                    infractions_server_state.setText("Apagado");
                    infractions_shutdown_button.setEnabled(false);
                    infractions_start_button.setEnabled(true);
                }
            }
        });
        infractions_view_logs_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLogs(Definitions.INFRACTIONS_SERVER_NAME);
            }
        });
        infractions_step_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestStep(Definitions.INFRACTIONS_SERVER_NAME);
            }
        });

        payments_start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (initServer(Definitions.PAYMENTS_SERVER_NAME)) {
                    payments_server_state.setText("Encendido");
                    payments_start_button.setEnabled(false);
                    payments_shutdown_button.setEnabled(true);
                }
            }
        });
        payments_shutdown_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (killServer(Definitions.PAYMENTS_SERVER_NAME)) {
                    payments_server_state.setText("Apagado");
                    payments_shutdown_button.setEnabled(false);
                    payments_start_button.setEnabled(true);
                }
            }
        });
        payments_view_logs_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLogs(Definitions.PAYMENTS_SERVER_NAME);
            }
        });
        payments_step_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestStep(Definitions.PAYMENTS_SERVER_NAME);
            }
        });

        products_start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (initServer(Definitions.PRODUCTS_SERVER_NAME)) {
                    products_server_state.setText("Encendido");
                    products_start_button.setEnabled(false);
                    products_shutdown_button.setEnabled(true);
                }
            }
        });
        products_shutdown_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (killServer(Definitions.PRODUCTS_SERVER_NAME)) {
                    products_server_state.setText("Apagado");
                    products_shutdown_button.setEnabled(false);
                    products_start_button.setEnabled(true);
                }
            }
        });
        products_view_logs_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLogs(Definitions.PRODUCTS_SERVER_NAME);
            }
        });
        products_step_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestStep(Definitions.PRODUCTS_SERVER_NAME);
            }
        });

        purchases_start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (initServer(Definitions.PURCHASES_SERVER_NAME)) {
                    purchases_server_state.setText("Encendido");
                    purchases_start_button.setEnabled(false);
                    purchases_shutdown_button.setEnabled(true);
                }
            }
        });
        purchases_shutdown_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (killServer(Definitions.PURCHASES_SERVER_NAME)) {
                    purchases_server_state.setText("Apagado");
                    purchases_shutdown_button.setEnabled(false);
                    purchases_start_button.setEnabled(true);
                }
            }
        });
        purchases_view_logs_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLogs(Definitions.PURCHASES_SERVER_NAME);
            }
        });
        purchases_step_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestStep(Definitions.PURCHASES_SERVER_NAME);
            }
        });

        shutdown_all_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessManager processManager = ProcessManager.getInstance();

                if(!processManager.hasStartedServers()) {
                    JOptionPane.showMessageDialog(null, "No hay servidores encendidos aun!");
                    return;
                }

                processManager.killAllServers();

                deliveries_start_button.setEnabled(true);
                deliveries_shutdown_button.setEnabled(false);
                deliveries_server_state.setText("Apagado");

                infractions_start_button.setEnabled(true);
                infractions_shutdown_button.setEnabled(false);
                infractions_server_state.setText("Apagado");

                payments_start_button.setEnabled(true);
                payments_shutdown_button.setEnabled(false);
                payments_server_state.setText("Apagado");

                products_start_button.setEnabled(true);
                products_shutdown_button.setEnabled(false);
                products_server_state.setText("Apagado");

                purchases_start_button.setEnabled(true);
                purchases_shutdown_button.setEnabled(false);
                purchases_server_state.setText("Apagado");

                JOptionPane.showMessageDialog(null, "Todos los servidores fueron apagados con exito!");
            }
        });
        quit_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProcessManager processManager = ProcessManager.getInstance();

                processManager.killAllServers();

                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ConfigurationSection serversConfiguration = configurationManager.loadConfiguration(Definitions.SERVERS_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (serversConfiguration == null) {
            Logging.error("Error: La configuracion de los servidores no existe!");
            return;
        }

        ConfigurationSection connectionConfiguration = configurationManager.loadConfiguration(Definitions.CONNECTION_CONFIGURATION_FILE, ConfigurationSection.CONFIGURATION_FOLDER);
        if (connectionConfiguration == null) {
            Logging.error("Error: La configuracion de la conexion a la base de datos no existe!");
            return;
        }

        MessageHandlerManager messageHandlerManager = MessageHandlerManager.getInstance();

        communicationHandler = CommunicationHandler.getInstance();

        MessageWorker.setMessageHandlerManager(messageHandlerManager);
        MessageWorker.setServerID(Definitions.MONITOR_SERVER_NAME);

        ResultMessageHandler resultMessageHandler = new ResultMessageHandler();

        messageHandlerManager.registerHandler(resultMessageHandler, MessageType.RESULT);

        if (!communicationHandler.connect()) {
            Logging.error("No se pudo establecer conexion con el servidor AMQP!");
            return;
        }

        communicationHandler.registerReceiver(Definitions.MONITOR_SERVER_NAME);

        ProcessManager processManager = ProcessManager.getInstance();

        processManager.setConfiguration(serversConfiguration);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("ControlPanel");
        frame.setContentPane(new ControlPanel().control_gui);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void requestStep(String serverID) {
        Map<String, String> data = new HashMap<>();
        data.put(Definitions.CONTROL_REFERENCE_KEY, Controls.MAKE_STEP);

        if (!communicationHandler.sendMessage(MessageType.CONTROL, serverID, data)) {
            JOptionPane.showMessageDialog(null, "Error: No se pudo enviar el mensaje!");
        }
    }

    private static boolean initServer(String serverID) {
        ProcessManager processManager = ProcessManager.getInstance();

        if (processManager.isRunning(serverID)) {
            JOptionPane.showMessageDialog(null, "El servidor ya se encuentra encendido!");
            return false;
        }

        if (!processManager.initServer(serverID)) {
            JOptionPane.showMessageDialog(null, "Error: El servidor no pudo ser iniciado!");
            return false;
        }

        JOptionPane.showMessageDialog(null, "Servidor iniciado con exito!");
        return true;
    }

    private static boolean killServer(String serverID) {
        ProcessManager processManager = ProcessManager.getInstance();

        if (!processManager.isRunning(serverID)) {
            JOptionPane.showMessageDialog(null, "El servidor ya se encuentra apagado!");
            return false;
        }

        if (!processManager.killServer(serverID)) {
            JOptionPane.showMessageDialog(null, "Error: El servidor no pudo ser apagado!");
            return false;
        }

        JOptionPane.showMessageDialog(null, "Servidor apagado con exito!");
        return true;
    }

    private static void showLogs(String serverID) {
        ProcessManager processManager = ProcessManager.getInstance();

        List<String> logs = processManager.getLogs(serverID);
        if (CollectionUtils.isEmpty(logs)) {
            JOptionPane.showMessageDialog(null, "No hay logs para mostrar!");
            return;
        }

        JList<String> list = new JList<>(logs.toArray(ArrayUtils.EMPTY_STRING_ARRAY));

        JScrollPane scrollPane = new JScrollPane(list);

        JPanel panel = new JPanel();
        panel.add(scrollPane);

        scrollPane.getViewport().add(list);
        JOptionPane.showMessageDialog(null, scrollPane, "Logs", JOptionPane.PLAIN_MESSAGE);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        control_gui = new JPanel();
        control_gui.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        control_gui.setMaximumSize(new Dimension(1220, 710));
        control_gui.setMinimumSize(new Dimension(1220, 710));
        control_gui.setPreferredSize(new Dimension(1220, 710));
        control_gui.setRequestFocusEnabled(true);
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(350);
        splitPane1.setOrientation(0);
        control_gui.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(1220, 710), new Dimension(1220, 710), new Dimension(1220, 710), 0, false));
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setContinuousLayout(false);
        splitPane2.setDividerLocation(400);
        splitPane2.setMaximumSize(new Dimension(810, 350));
        splitPane2.setMinimumSize(new Dimension(810, 350));
        splitPane2.setPreferredSize(new Dimension(810, 350));
        splitPane1.setLeftComponent(splitPane2);
        purchases_panel = new JPanel();
        purchases_panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, true, false));
        purchases_panel.setMaximumSize(new Dimension(-1, -1));
        purchases_panel.setMinimumSize(new Dimension(-1, -1));
        purchases_panel.setOpaque(true);
        purchases_panel.setPreferredSize(new Dimension(-1, -1));
        splitPane2.setLeftComponent(purchases_panel);
        purchases_panel.setBorder(BorderFactory.createTitledBorder("Compras"));
        purchases = new JTabbedPane();
        purchases.setDoubleBuffered(false);
        purchases.setMaximumSize(new Dimension(350, 245));
        purchases.setMinimumSize(new Dimension(350, 245));
        purchases.setPreferredSize(new Dimension(350, 245));
        purchases_panel.add(purchases, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setMaximumSize(new Dimension(-1, -1));
        panel1.setMinimumSize(new Dimension(-1, -1));
        panel1.setPreferredSize(new Dimension(-1, -1));
        purchases.addTab("Estado Actual", panel1);
        final JLabel label1 = new JLabel();
        label1.setText("ID Compra");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        purchases_purchase_id = new JTextField();
        panel1.add(purchases_purchase_id, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Estado");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_state = new JTextField();
        panel1.add(purchases_state, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Fecha");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_date = new JTextField();
        panel1.add(purchases_date, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Estado del Servidor");
        panel1.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_server_state = new JTextField();
        purchases_server_state.setEnabled(false);
        purchases_server_state.setText("Apagado");
        panel1.add(purchases_server_state, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel1.add(separator1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setMaximumSize(new Dimension(-1, -1));
        panel2.setMinimumSize(new Dimension(-1, -1));
        panel2.setPreferredSize(new Dimension(-1, -1));
        panel2.setRequestFocusEnabled(true);
        purchases.addTab("Mensajes", panel2);
        purchases_messages = new JTable();
        panel2.add(purchases_messages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setInheritsPopupMenu(true);
        panel3.setMaximumSize(new Dimension(-1, -1));
        panel3.setMinimumSize(new Dimension(-1, -1));
        panel3.setPreferredSize(new Dimension(-1, -1));
        purchases.addTab("Ultimo Estado", panel3);
        purchases_last_state = new JTable();
        panel3.add(purchases_last_state, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        purchases_actions = new JPanel();
        purchases_actions.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        purchases_panel.add(purchases_actions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        purchases_generate_event_button = new JButton();
        purchases_generate_event_button.setEnabled(false);
        purchases_generate_event_button.setText("Generar Evento");
        purchases_actions.add(purchases_generate_event_button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_restart_button = new JButton();
        purchases_restart_button.setEnabled(false);
        purchases_restart_button.setText("Reiniciar");
        purchases_actions.add(purchases_restart_button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_shutdown_button = new JButton();
        purchases_shutdown_button.setEnabled(false);
        purchases_shutdown_button.setText("Apagar");
        purchases_actions.add(purchases_shutdown_button, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_start_button = new JButton();
        purchases_start_button.setText("Iniciar");
        purchases_actions.add(purchases_start_button, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_step_button = new JButton();
        purchases_step_button.setText("Step");
        purchases_actions.add(purchases_step_button, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        purchases_view_logs_button = new JButton();
        purchases_view_logs_button.setText("Ver Logs");
        purchases_actions.add(purchases_view_logs_button, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSplitPane splitPane3 = new JSplitPane();
        splitPane3.setDividerLocation(400);
        splitPane2.setRightComponent(splitPane3);
        payments_panel = new JPanel();
        payments_panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, true, false));
        payments_panel.setMaximumSize(new Dimension(-1, -1));
        payments_panel.setMinimumSize(new Dimension(-1, -1));
        payments_panel.setPreferredSize(new Dimension(-1, -1));
        splitPane3.setLeftComponent(payments_panel);
        payments_panel.setBorder(BorderFactory.createTitledBorder("Pagos"));
        payments = new JTabbedPane();
        payments.setMaximumSize(new Dimension(350, 245));
        payments.setMinimumSize(new Dimension(350, 245));
        payments.setPreferredSize(new Dimension(350, 245));
        payments_panel.add(payments, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.setMaximumSize(new Dimension(-1, -1));
        panel4.setMinimumSize(new Dimension(-1, -1));
        panel4.setPreferredSize(new Dimension(-1, -1));
        payments.addTab("Estado Actual", panel4);
        final JLabel label5 = new JLabel();
        label5.setText("ID Compra");
        panel4.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        payments_purchase_id = new JTextField();
        panel4.add(payments_purchase_id, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Estado");
        panel4.add(label6, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_state = new JTextField();
        panel4.add(payments_state, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Fecha");
        panel4.add(label7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_date = new JTextField();
        panel4.add(payments_date, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Estado del Servidor");
        panel4.add(label8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_server_state = new JTextField();
        payments_server_state.setEnabled(false);
        payments_server_state.setText("Apagado");
        panel4.add(payments_server_state, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator2 = new JSeparator();
        panel4.add(separator2, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setMaximumSize(new Dimension(-1, -1));
        panel5.setMinimumSize(new Dimension(-1, -1));
        panel5.setPreferredSize(new Dimension(-1, -1));
        payments.addTab("Mensajes", panel5);
        payments_messages = new JTable();
        panel5.add(payments_messages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.setMaximumSize(new Dimension(-1, -1));
        panel6.setMinimumSize(new Dimension(-1, -1));
        panel6.setPreferredSize(new Dimension(-1, -1));
        payments.addTab("Ultimo Estado", panel6);
        payments_last_state = new JTable();
        panel6.add(payments_last_state, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        payments_actions = new JPanel();
        payments_actions.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        payments_panel.add(payments_actions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        payments_generate_event_button = new JButton();
        payments_generate_event_button.setEnabled(false);
        payments_generate_event_button.setText("Generar Evento");
        payments_actions.add(payments_generate_event_button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_restart_button = new JButton();
        payments_restart_button.setEnabled(false);
        payments_restart_button.setText("Reiniciar");
        payments_actions.add(payments_restart_button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_shutdown_button = new JButton();
        payments_shutdown_button.setEnabled(false);
        payments_shutdown_button.setText("Apagar");
        payments_actions.add(payments_shutdown_button, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_start_button = new JButton();
        payments_start_button.setText("Iniciar");
        payments_actions.add(payments_start_button, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_step_button = new JButton();
        payments_step_button.setText("Step");
        payments_actions.add(payments_step_button, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        payments_view_logs_button = new JButton();
        payments_view_logs_button.setText("Ver Logs");
        payments_actions.add(payments_view_logs_button, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_panel = new JPanel();
        deliveries_panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1, true, false));
        deliveries_panel.setMaximumSize(new Dimension(-1, -1));
        deliveries_panel.setMinimumSize(new Dimension(-1, -1));
        deliveries_panel.setOpaque(true);
        deliveries_panel.setPreferredSize(new Dimension(-1, -1));
        splitPane3.setRightComponent(deliveries_panel);
        deliveries_panel.setBorder(BorderFactory.createTitledBorder("Envios"));
        deliveries = new JTabbedPane();
        deliveries.setDoubleBuffered(false);
        deliveries.setMaximumSize(new Dimension(350, 245));
        deliveries.setMinimumSize(new Dimension(350, 245));
        deliveries.setPreferredSize(new Dimension(350, 245));
        deliveries_panel.add(deliveries, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel7.setMaximumSize(new Dimension(-1, -1));
        panel7.setMinimumSize(new Dimension(-1, -1));
        panel7.setPreferredSize(new Dimension(-1, -1));
        deliveries.addTab("Estado Actual", panel7);
        final JLabel label9 = new JLabel();
        label9.setText("ID Compra");
        panel7.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel7.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 10), new Dimension(-1, 10), new Dimension(-1, 10), 0, false));
        deliveries_purchase_id = new JTextField();
        panel7.add(deliveries_purchase_id, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Estado");
        panel7.add(label10, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_state = new JTextField();
        panel7.add(deliveries_state, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Fecha");
        panel7.add(label11, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_date = new JTextField();
        panel7.add(deliveries_date, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Estado del Servidor");
        panel7.add(label12, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_server_state = new JTextField();
        deliveries_server_state.setEnabled(false);
        deliveries_server_state.setText("Apagado");
        panel7.add(deliveries_server_state, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator3 = new JSeparator();
        panel7.add(separator3, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.setMaximumSize(new Dimension(-1, -1));
        panel8.setMinimumSize(new Dimension(-1, -1));
        panel8.setPreferredSize(new Dimension(-1, -1));
        panel8.setRequestFocusEnabled(true);
        deliveries.addTab("Mensajes", panel8);
        deliveries_messages = new JTable();
        panel8.add(deliveries_messages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel9.setInheritsPopupMenu(true);
        panel9.setMaximumSize(new Dimension(-1, -1));
        panel9.setMinimumSize(new Dimension(-1, -1));
        panel9.setPreferredSize(new Dimension(-1, -1));
        deliveries.addTab("Ultimo Estado", panel9);
        deliveries_last_state = new JTable();
        panel9.add(deliveries_last_state, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        deliveries_actions = new JPanel();
        deliveries_actions.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        deliveries_panel.add(deliveries_actions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deliveries_generate_event_button = new JButton();
        deliveries_generate_event_button.setEnabled(false);
        deliveries_generate_event_button.setText("Generar Evento");
        deliveries_actions.add(deliveries_generate_event_button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_restart_button = new JButton();
        deliveries_restart_button.setEnabled(false);
        deliveries_restart_button.setText("Reiniciar");
        deliveries_actions.add(deliveries_restart_button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_shutdown_button = new JButton();
        deliveries_shutdown_button.setEnabled(false);
        deliveries_shutdown_button.setText("Apagar");
        deliveries_actions.add(deliveries_shutdown_button, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_start_button = new JButton();
        deliveries_start_button.setText("Iniciar");
        deliveries_actions.add(deliveries_start_button, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_step_button = new JButton();
        deliveries_step_button.setText("Step");
        deliveries_actions.add(deliveries_step_button, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deliveries_view_logs_button = new JButton();
        deliveries_view_logs_button.setText("Ver Logs");
        deliveries_actions.add(deliveries_view_logs_button, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSplitPane splitPane4 = new JSplitPane();
        splitPane4.setDividerLocation(400);
        splitPane4.setMaximumSize(new Dimension(810, 350));
        splitPane4.setMinimumSize(new Dimension(810, 350));
        splitPane4.setPreferredSize(new Dimension(810, 350));
        splitPane4.setRequestFocusEnabled(true);
        splitPane1.setRightComponent(splitPane4);
        products_panel = new JPanel();
        products_panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        products_panel.setMaximumSize(new Dimension(-1, -1));
        products_panel.setMinimumSize(new Dimension(-1, -1));
        products_panel.setPreferredSize(new Dimension(-1, -1));
        splitPane4.setLeftComponent(products_panel);
        products_panel.setBorder(BorderFactory.createTitledBorder("Productos"));
        products = new JTabbedPane();
        products.setMaximumSize(new Dimension(350, 245));
        products.setMinimumSize(new Dimension(350, 245));
        products.setPreferredSize(new Dimension(350, 245));
        products_panel.add(products, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.setMaximumSize(new Dimension(-1, -1));
        panel10.setMinimumSize(new Dimension(-1, -1));
        panel10.setPreferredSize(new Dimension(-1, -1));
        products.addTab("Estado Actual", panel10);
        final JLabel label13 = new JLabel();
        label13.setText("ID Compra");
        panel10.add(label13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel10.add(spacer4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_purchase_id = new JTextField();
        panel10.add(products_purchase_id, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label14 = new JLabel();
        label14.setText("Estado");
        panel10.add(label14, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_state = new JTextField();
        panel10.add(products_state, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setText("Fecha");
        panel10.add(label15, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_date = new JTextField();
        panel10.add(products_date, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label16 = new JLabel();
        label16.setText("Estado del Servidor");
        panel10.add(label16, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_server_state = new JTextField();
        products_server_state.setEnabled(false);
        products_server_state.setText("Apagado");
        panel10.add(products_server_state, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator4 = new JSeparator();
        panel10.add(separator4, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel11.setMaximumSize(new Dimension(-1, -1));
        panel11.setMinimumSize(new Dimension(-1, -1));
        panel11.setOpaque(true);
        panel11.setPreferredSize(new Dimension(-1, -1));
        products.addTab("Mensajes", panel11);
        products_messages = new JTable();
        panel11.add(products_messages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel12.setMaximumSize(new Dimension(-1, -1));
        panel12.setMinimumSize(new Dimension(-1, -1));
        panel12.setPreferredSize(new Dimension(-1, -1));
        products.addTab("Ultimo Estado", panel12);
        products_last_state = new JTable();
        panel12.add(products_last_state, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        products_actions = new JPanel();
        products_actions.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        products_panel.add(products_actions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        products_generate_event_button = new JButton();
        products_generate_event_button.setEnabled(false);
        products_generate_event_button.setText("Generar Evento");
        products_actions.add(products_generate_event_button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_restart_button = new JButton();
        products_restart_button.setEnabled(false);
        products_restart_button.setText("Reiniciar");
        products_actions.add(products_restart_button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_shutdown_button = new JButton();
        products_shutdown_button.setEnabled(false);
        products_shutdown_button.setText("Apagar");
        products_actions.add(products_shutdown_button, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_start_button = new JButton();
        products_start_button.setText("Iniciar");
        products_actions.add(products_start_button, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_step_button = new JButton();
        products_step_button.setText("Step");
        products_actions.add(products_step_button, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        products_view_logs_button = new JButton();
        products_view_logs_button.setText("Ver Logs");
        products_actions.add(products_view_logs_button, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSplitPane splitPane5 = new JSplitPane();
        splitPane5.setDividerLocation(400);
        splitPane4.setRightComponent(splitPane5);
        common_panel = new JPanel();
        common_panel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane5.setLeftComponent(common_panel);
        final Spacer spacer5 = new Spacer();
        common_panel.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        quit_button = new JButton();
        quit_button.setText("Salir");
        common_panel.add(quit_button, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        shutdown_all_button = new JButton();
        shutdown_all_button.setText("Apagar Todo");
        common_panel.add(shutdown_all_button, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_panel = new JPanel();
        infractions_panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        infractions_panel.setMaximumSize(new Dimension(-1, -1));
        infractions_panel.setMinimumSize(new Dimension(-1, -1));
        infractions_panel.setPreferredSize(new Dimension(-1, -1));
        splitPane5.setRightComponent(infractions_panel);
        infractions_panel.setBorder(BorderFactory.createTitledBorder("Infracciones"));
        infractions = new JTabbedPane();
        infractions.setMaximumSize(new Dimension(350, 245));
        infractions.setMinimumSize(new Dimension(350, 245));
        infractions.setPreferredSize(new Dimension(350, 245));
        infractions_panel.add(infractions, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel13.setMaximumSize(new Dimension(-1, -1));
        panel13.setMinimumSize(new Dimension(-1, -1));
        panel13.setPreferredSize(new Dimension(-1, -1));
        infractions.addTab("Estado Actual", panel13);
        final JLabel label17 = new JLabel();
        label17.setText("ID Compra");
        panel13.add(label17, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel13.add(spacer6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_purchase_id = new JTextField();
        panel13.add(infractions_purchase_id, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label18 = new JLabel();
        label18.setText("Estado");
        panel13.add(label18, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_state = new JTextField();
        panel13.add(infractions_state, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label19 = new JLabel();
        label19.setText("Fecha");
        panel13.add(label19, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_date = new JTextField();
        panel13.add(infractions_date, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label20 = new JLabel();
        label20.setText("Estado del Servidor");
        panel13.add(label20, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_server_state = new JTextField();
        infractions_server_state.setEnabled(false);
        infractions_server_state.setText("Apagado");
        panel13.add(infractions_server_state, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator5 = new JSeparator();
        panel13.add(separator5, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel14.setMaximumSize(new Dimension(-1, -1));
        panel14.setMinimumSize(new Dimension(-1, -1));
        panel14.setOpaque(true);
        panel14.setPreferredSize(new Dimension(-1, -1));
        infractions.addTab("Mensajes", panel14);
        infractions_messages = new JTable();
        panel14.add(infractions_messages, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.setMaximumSize(new Dimension(-1, -1));
        panel15.setMinimumSize(new Dimension(-1, -1));
        panel15.setPreferredSize(new Dimension(-1, -1));
        infractions.addTab("Ultimo Estado", panel15);
        infractions_last_state = new JTable();
        panel15.add(infractions_last_state, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        infractions_actions = new JPanel();
        infractions_actions.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1, true, false));
        infractions_panel.add(infractions_actions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        infractions_generate_event_button = new JButton();
        infractions_generate_event_button.setEnabled(false);
        infractions_generate_event_button.setText("Generar Evento");
        infractions_actions.add(infractions_generate_event_button, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_restart_button = new JButton();
        infractions_restart_button.setEnabled(false);
        infractions_restart_button.setText("Reiniciar");
        infractions_actions.add(infractions_restart_button, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_shutdown_button = new JButton();
        infractions_shutdown_button.setEnabled(false);
        infractions_shutdown_button.setText("Apagar");
        infractions_actions.add(infractions_shutdown_button, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_start_button = new JButton();
        infractions_start_button.setText("Iniciar");
        infractions_actions.add(infractions_start_button, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_step_button = new JButton();
        infractions_step_button.setText("Step");
        infractions_actions.add(infractions_step_button, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infractions_view_logs_button = new JButton();
        infractions_view_logs_button.setText("Ver Logs");
        infractions_actions.add(infractions_view_logs_button, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return control_gui;
    }

}
