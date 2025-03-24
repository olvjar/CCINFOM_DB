package view.dialog;

import model.entity.Device;
import model.entity.Appointment;
import model.service.DeviceService;
import model.service.AppointmentService;
import controller.DeviceController;
import controller.AppointmentController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleRepairDialog extends JDialog {
    // Fields for device section
    private final int customerCode;
    private final List<Device> devices;
    private JComboBox<Device> deviceCombo;
    private JPanel deviceDetailsPanel;
    private JTextField typeField, brandField, modelField, serialField;
    private JTextArea descriptionArea;
    private boolean isNewDevice = false;
    
    // Controllers
    private final DeviceController deviceController;
    private final AppointmentController appointmentController;

    // UI Components
    private JLabel typeValueLabel;
    private JLabel brandValueLabel;
    private JLabel modelValueLabel;
    private JLabel serialValueLabel;
    private JTextArea existingDescriptionArea;

    public ScheduleRepairDialog(JFrame parent, int customerCode, List<Device> devices) {
        super(parent, "Schedule Repair", true);
        this.customerCode = customerCode;
        this.devices = devices;
        
        // Initialize controllers
        this.deviceController = new DeviceController(new DeviceService());
        this.appointmentController = new AppointmentController(new AppointmentService());
        
        setupDialog();
        initializeComponents();
    }

    private void setupDialog() {
        setSize(500, 700);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
    }

    private void initializeComponents() {
        JPanel mainPanel = createMainPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createDeviceSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(createScheduleSection());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton scheduleButton = new JButton("Schedule Repair");
        JButton cancelButton = new JButton("Cancel");
        
        scheduleButton.addActionListener(e -> scheduleRepair());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(scheduleButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private JPanel createDeviceSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Device Information"));
        
        JPanel cardPanel = new JPanel(new CardLayout());
        
        // Only show radio buttons if there are existing devices
        if (!devices.isEmpty()) {
            JRadioButton existingDeviceRadio = new JRadioButton("Select Existing Device");
            JRadioButton newDeviceRadio = new JRadioButton("Add New Device");
            ButtonGroup group = new ButtonGroup();
            group.add(existingDeviceRadio);
            group.add(newDeviceRadio);
            
            JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            radioPanel.add(existingDeviceRadio);
            radioPanel.add(newDeviceRadio);
            panel.add(radioPanel, BorderLayout.NORTH);
            
            // Setup radio button listeners
            existingDeviceRadio.addActionListener(e -> {
                CardLayout cl = (CardLayout) cardPanel.getLayout();
                cl.show(cardPanel, "existing");
                isNewDevice = false;
                descriptionArea = existingDescriptionArea;
            });
            
            newDeviceRadio.addActionListener(e -> {
                CardLayout cl = (CardLayout) cardPanel.getLayout();
                cl.show(cardPanel, "new");
                isNewDevice = true;
                descriptionArea = ((JTextArea) ((JScrollPane) deviceDetailsPanel.getComponent(9)).getViewport().getView());
            });
            
            existingDeviceRadio.setSelected(true);
        } else {
            // If no devices, set isNewDevice to true by default
            isNewDevice = true;
        }

        // Create panels for existing and new device
        JPanel existingDevicePanel = createExistingDevicePanel();
        deviceDetailsPanel = createNewDevicePanel();

        cardPanel.add(existingDevicePanel, "existing");
        cardPanel.add(deviceDetailsPanel, "new");
        
        // If no devices, show new device panel directly
        if (devices.isEmpty()) {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "new");
            descriptionArea = ((JTextArea) ((JScrollPane) deviceDetailsPanel.getComponent(9)).getViewport().getView());
        }
        
        panel.add(cardPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createExistingDevicePanel() {
        JPanel existingDevicePanel = new JPanel(new BorderLayout(5, 5));
        
        deviceCombo = new JComboBox<>(devices.toArray(new Device[0]));
        deviceCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                Device device = (Device) value;
                String text = device.getDeviceType() + " - " + device.getBrand() + " " + device.getModel();
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });

        JPanel existingDetailsPanel = new JPanel(new GridBagLayout());
        existingDetailsPanel.setBorder(BorderFactory.createTitledBorder("Device Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);

        JLabel typeLabel = new JLabel("Type:");
        JLabel brandLabel = new JLabel("Brand:");
        JLabel modelLabel = new JLabel("Model:");
        JLabel serialLabel = new JLabel("Serial Number:");
        
        // Initialize the labels
        typeValueLabel = new JLabel();
        brandValueLabel = new JLabel();
        modelValueLabel = new JLabel();
        serialValueLabel = new JLabel();
        existingDescriptionArea = new JTextArea(3, 20);
        existingDescriptionArea.setLineWrap(true);
        existingDescriptionArea.setWrapStyleWord(true);

        addFormField(existingDetailsPanel, "Type:", typeValueLabel, gbc, 0);
        addFormField(existingDetailsPanel, "Brand:", brandValueLabel, gbc, 1);
        addFormField(existingDetailsPanel, "Model:", modelValueLabel, gbc, 2);
        addFormField(existingDetailsPanel, "Serial Number:", serialValueLabel, gbc, 3);

        JPanel existingProblemPanel = new JPanel(new BorderLayout(5, 5));
        existingProblemPanel.setBorder(BorderFactory.createTitledBorder("Problem Description"));
        existingDescriptionArea = new JTextArea(3, 20);
        existingDescriptionArea.setLineWrap(true);
        existingDescriptionArea.setWrapStyleWord(true);
        existingProblemPanel.add(new JScrollPane(existingDescriptionArea), BorderLayout.CENTER);

        // Initially set the descriptionArea reference to existingDescriptionArea
        descriptionArea = existingDescriptionArea;

        setupDeviceComboListener();

        if (devices.size() > 0) {
            Device initialDevice = (Device) deviceCombo.getSelectedItem();
            if (initialDevice != null) {
                typeValueLabel.setText(initialDevice.getDeviceType());
                brandValueLabel.setText(initialDevice.getBrand());
                modelValueLabel.setText(initialDevice.getModel());
                serialValueLabel.setText(initialDevice.getSerialNumber());
                existingDescriptionArea.setText(initialDevice.getDescription());
            }
        }

        JPanel existingDeviceContent = new JPanel(new BorderLayout(5, 5));
        existingDeviceContent.add(deviceCombo, BorderLayout.NORTH);
        existingDeviceContent.add(existingDetailsPanel, BorderLayout.CENTER);
        existingDeviceContent.add(existingProblemPanel, BorderLayout.SOUTH);
        existingDevicePanel.add(existingDeviceContent, BorderLayout.CENTER);

        return existingDevicePanel;
    }

    private JPanel createNewDevicePanel() {
        JPanel newDevicePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);
        
        typeField = new JTextField(20);
        brandField = new JTextField(20);
        modelField = new JTextField(20);
        serialField = new JTextField(20);
        JTextArea newDescriptionArea = new JTextArea(3, 20);
        newDescriptionArea.setLineWrap(true);
        
        addFormField(newDevicePanel, "Type:", typeField, gbc, 0);
        addFormField(newDevicePanel, "Brand:", brandField, gbc, 1);
        addFormField(newDevicePanel, "Model:", modelField, gbc, 2);
        addFormField(newDevicePanel, "Serial Number:", serialField, gbc, 3);
        addFormField(newDevicePanel, "Problem Description:", new JScrollPane(newDescriptionArea), gbc, 4);

        return newDevicePanel;
    }

    private JPanel createScheduleSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Schedule"));
        
        JPanel schedulePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);
        
        JLabel dateTimeLabel = new JLabel(getCurrentDateTime());
        addFormField(schedulePanel, "Schedule Time:", dateTimeLabel, gbc, 0);
        
        panel.add(schedulePanel, BorderLayout.CENTER);
        return panel;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private void scheduleRepair() {
        try {
            validateInputs();
            Device device = isNewDevice ? createNewDevice() : updateExistingDevice();
            createAppointment(device);
            showSuccess("Repair appointment scheduled successfully!");
            dispose();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private Device updateExistingDevice() throws SQLException {
        Device selectedDevice = (Device) deviceCombo.getSelectedItem();
        Device updatedDevice = new Device(
            selectedDevice.getDeviceId(),
            selectedDevice.getCustomerCode(),
            selectedDevice.getDeviceType(),
            selectedDevice.getBrand(),
            selectedDevice.getModel(),
            selectedDevice.getSerialNumber(),
            existingDescriptionArea.getText().trim()
        );
        
        deviceController.updateDevice(updatedDevice);
        return updatedDevice;
    }

    private void createAppointment(Device device) throws SQLException {
        String dateTime = getCurrentDateTime();
        int invoiceNumber = appointmentController.generateInvoiceNumber();
        
        // Create appointment with first available technician
        Appointment appointment = new Appointment(
            customerCode,
            101,
            "Pending",
            dateTime,
            invoiceNumber,
            "Pending",
            0.0,
            device.getDeviceId()
        );
        
        appointmentController.addAppointment(appointment);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void validateInputs() {
        if (isNewDevice) {
            if (typeField.getText().trim().isEmpty() ||
                brandField.getText().trim().isEmpty() ||
                modelField.getText().trim().isEmpty() ||
                serialField.getText().trim().isEmpty() ||
                descriptionArea.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Please fill in all device fields");
            }
        } else if (deviceCombo.getSelectedIndex() == -1) {
            throw new IllegalArgumentException("Please select a device");
        }
    }

    private Device createNewDevice() throws SQLException {
        int deviceId = deviceController.generateDeviceId();
        Device device = new Device(
            deviceId,
            customerCode,
            typeField.getText().trim(),
            brandField.getText().trim(),
            modelField.getText().trim(),
            serialField.getText().trim(),
            descriptionArea.getText().trim()
        );
        
        deviceController.addDevice(device);
        return device;
    }

    private void addFormField(JPanel panel, String label, JComponent field, 
                            GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    // Update device description when combo box selection changes
    private void setupDeviceComboListener() {
        deviceCombo.addActionListener(e -> {
            Device selectedDevice = (Device) deviceCombo.getSelectedItem();
            if (selectedDevice != null) {
                typeValueLabel.setText(selectedDevice.getDeviceType());
                brandValueLabel.setText(selectedDevice.getBrand());
                modelValueLabel.setText(selectedDevice.getModel());
                serialValueLabel.setText(selectedDevice.getSerialNumber());
                existingDescriptionArea.setText(selectedDevice.getDescription());
            }
        });
    }
} 