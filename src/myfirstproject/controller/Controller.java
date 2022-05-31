
package myfirstproject.controller;

import myfirstproject.model.Invoice;
import myfirstproject.model.InvoiceTable;
import myfirstproject.model.Item;
import myfirstproject.model.ItemTable;
import myfirstproject.view.InvoiceTableDialog;
import myfirstproject.view.OmarSalesInvoiceFrame;
import myfirstproject.view.ItemTableDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Controller implements ActionListener, ListSelectionListener{
    private OmarSalesInvoiceFrame frame;
    private InvoiceTableDialog invoiceTableDialog;
    private ItemTableDialog itemTableDialog;

    public Controller(OmarSalesInvoiceFrame frame) {
        this.frame = frame;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        System.out.println("Action: " + actionCommand);
        switch (actionCommand) {
            case "Load File":
                loadFile();
                break;
            case "Save File":
                saveFile();
                break;
            case "Create New Invoice":
                createNewInvoice();
                break;
            case "Delete Invoice":
                deleteInvoice();
                break;
            case "Create New Item":
                createNewItem();
                break;
            case "Delete Item":
                deleteItem();
                break;
            case "invoiceOkButton":
                InvoiceOkButton();
                break;
            case "InvoiceCancelButton":
                InvoiceCancelButton();
                break;
            case "ItemOkButton":
                ItemOkButton();
                break;
            case "ItemCancelButton":
                ItemCancelButton();
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int selectedIndex = frame.getInvoiceTable().getSelectedRow();
        if (selectedIndex != -1) {
            Invoice currentInvoice = frame.getInvoices().get(selectedIndex);
            frame.getInvoiceNumberLabel().setText("" + currentInvoice.getNum());
            frame.getInvoiceDateLabel().setText(currentInvoice.getDate());
            frame.getCustomerNameLabel().setText(currentInvoice.getCustomer());
            frame.getInvoiceTotalLabel().setText("" + currentInvoice.getInvoiceTotal());
            ItemTable itemTable = new ItemTable(currentInvoice.getItems());
            frame.getItemTable().setModel(itemTable);
            itemTable.fireTableDataChanged();
        }
    }

    private void loadFile() {
        JFileChooser fc = new JFileChooser();
        try {
            int result = fc.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                Path headerPath = Paths.get(headerFile.getAbsolutePath());
                List<String> headerItems = Files.readAllLines(headerPath);
                ArrayList<Invoice> arrayOfInvoices = new ArrayList<>();
                for (String headerItem : headerItems) {
                    try {
                        String[] headerParts = headerItem.split(",");
                        int invoiceNumber = Integer.parseInt(headerParts[0]);
                        String invoiceDate = headerParts[1];
                        String customerName = headerParts[2];

                        Invoice invoice = new Invoice(invoiceNumber, invoiceDate, customerName);
                        arrayOfInvoices.add(invoice);
                        System.out.println(invoice.toString());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                result = fc.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File itemFile = fc.getSelectedFile();
                    Path itemPath = Paths.get(itemFile.getAbsolutePath());
                    List<String> items = Files.readAllLines(itemPath);
                    for (String item : items) {
                        try {
                            String[] itemParts = item.split(",");
                            int invoiceNumber = Integer.parseInt(itemParts[0]);
                            String itemName = itemParts[1];
                            double itemPrice = Double.parseDouble(itemParts[2]);
                            int count = Integer.parseInt(itemParts[3]);
                            Invoice inv = null;
                            for (Invoice invoice : arrayOfInvoices) {
                                if (invoice.getNum() == invoiceNumber) {
                                    inv = invoice;
                                    break;
                                }
                            }

                            Item ITEM = new Item(itemName, itemPrice, count, inv);
                            inv.getItems().add(ITEM);
                            System.out.println(ITEM.toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                frame.setInvoices(arrayOfInvoices);
                InvoiceTable invoiceTable = new InvoiceTable(arrayOfInvoices);
                frame.setInvoicesTableModel(invoiceTable);
                frame.getInvoiceTable().setModel(invoiceTable);
                frame.getInvoicesTableModel().fireTableDataChanged();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Cannot read file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFile() {
        ArrayList<Invoice> invoices = frame.getInvoices();
        String headers = "";
        String items = "";
        for (Invoice invoice : invoices) {
            String invoiceCSV = invoice.fileCSV();
            headers += invoiceCSV;
            headers += "\n";

            for (Item item : invoice.getItems()) {
                String itemCSV = item.fileCSV();
                items += itemCSV;
                items += "\n";
            }
        }
        try {
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                FileWriter fileWriter1 = new FileWriter(headerFile);
                fileWriter1.write(headers);
                fileWriter1.flush();
                fileWriter1.close();
                result = fc.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File lineFile = fc.getSelectedFile();
                    FileWriter fileWriter2 = new FileWriter(lineFile);
                    fileWriter2.write(items);
                    fileWriter2.flush();
                    fileWriter2.close();
                }
            }
        } catch (Exception ex) {

        }
    }

    private void createNewInvoice() {
        invoiceTableDialog = new InvoiceTableDialog(frame);
        invoiceTableDialog.setVisible(true);
    }

    private void deleteInvoice() {
        int selectedRow = frame.getInvoiceTable().getSelectedRow();
        if (selectedRow != -1) {
            frame.getInvoices().remove(selectedRow);
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void createNewItem() {
        itemTableDialog = new ItemTableDialog(frame);
        itemTableDialog.setVisible(true);
    }

    private void deleteItem() {
        int selectedRow = frame.getItemTable().getSelectedRow();

        if (selectedRow != -1) {
            ItemTable itemTable = (ItemTable) frame.getItemTable().getModel();
            itemTable.getItems().remove(selectedRow);
            itemTable.fireTableDataChanged();
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
    }

    private void InvoiceOkButton() {
        
        String date = invoiceTableDialog.getInvoiceDateField().getText();
        String customer = invoiceTableDialog.getCustomerNameField().getText();
        int num = frame.getNextInvoiceNum();
        try {
            String[] dateParts = date.split("-");
            if (dateParts.length < 3) {
                JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                if (day > 31 || month > 12) {
                    JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    Invoice invoice = new Invoice(num, date, customer);
                    frame.getInvoices().add(invoice);
                    frame.getInvoicesTableModel().fireTableDataChanged();
                    invoiceTableDialog.setVisible(false);
                    invoiceTableDialog.dispose();
                    invoiceTableDialog = null;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Wrong date format", "Error", JOptionPane.ERROR_MESSAGE);
        }



    }
    
        private void InvoiceCancelButton() {
        invoiceTableDialog.setVisible(false);
        invoiceTableDialog.dispose();
        invoiceTableDialog = null;
    }

    private void ItemOkButton() {
        String item = itemTableDialog.getItemNameField().getText();
        String countStr = itemTableDialog.getItemCountField().getText();
        String priceStr = itemTableDialog.getItemPriceField().getText();
        int count = Integer.parseInt(countStr);
        double price = Double.parseDouble(priceStr);
        int selectedInvoice = frame.getInvoiceTable().getSelectedRow();
        if (selectedInvoice != -1) {
            Invoice invoice = frame.getInvoices().get(selectedInvoice);
            Item line = new Item(item, price, count, invoice);
            invoice.getItems().add(line);
            ItemTable itemTable = (ItemTable) frame.getItemTable().getModel();
 //           ItemTable.getItems().add(line);
            itemTable.fireTableDataChanged();
            frame.getInvoicesTableModel().fireTableDataChanged();
        }
        itemTableDialog.setVisible(false);
        itemTableDialog.dispose();
        itemTableDialog = null;
    }

    private void ItemCancelButton() {
        itemTableDialog.setVisible(false);
        itemTableDialog.dispose();
        itemTableDialog = null;
    }


    
}
