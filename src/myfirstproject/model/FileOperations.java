
package myfirstproject.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileOperations {
 private ArrayList<Invoice> invoiceHeader;
      
    public ArrayList<Invoice> read(){
        
        
        JFileChooser fc = new JFileChooser();

        try {
            JOptionPane.showMessageDialog(null, "Select Invoice Header File",
                    "Invoice Header", JOptionPane.INFORMATION_MESSAGE);
            int result = fc.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File headerFile = fc.getSelectedFile();
                Path headerPath = Paths.get(headerFile.getAbsolutePath());
                List<String> headerItems = Files.readAllLines(headerPath);
               
                ArrayList<Invoice> arrayOfInvoices = new ArrayList<>();
                for (String headerItem : headerItems) {
                    try {
                        String[] headerParts = headerItem.split(",");
                        int invoiceNum = Integer.parseInt(headerParts[0]);
                        String invoiceDate = headerParts[1];
                        String customerName = headerParts[2];

                        Invoice invoice = new Invoice(invoiceNum, invoiceDate, customerName);
                        arrayOfInvoices.add(invoice);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                        //Reminder to load only Line CSV file and error popup appear when try to choose fault file type
                    }
                }
                JOptionPane.showMessageDialog(null, "Select Invoice Item File",
                        "Invoice Item", JOptionPane.INFORMATION_MESSAGE);
                result = fc.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File itemFile = fc.getSelectedFile();
                    Path itemPath = Paths.get(itemFile.getAbsolutePath());
                    List<String> ITEMS = Files.readAllLines(itemPath);
                    for (String item : ITEMS) {
                        try {
                            String[] itemParts = item.split(",");
                            int invoiceNum = Integer.parseInt(itemParts[0]);
                            String itemName = itemParts[1];
                            double itemPrice = Double.parseDouble(itemParts[2]);
                            int count = Integer.parseInt(itemParts[3]);
                            Invoice inv = null;
                            for (Invoice invoice : arrayOfInvoices) {
                                if (invoice.getNum() == invoiceNum) {
                                    inv = invoice;
                                    break;
                                }
                            }

                            Item ITEM = new Item(itemName, itemPrice, count, inv);
                            inv.getItems().add(ITEM);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error in line format", "Error", JOptionPane.ERROR_MESSAGE);
                                //Reminder to load only CSV file and error popup appear when try to choose fault file type
                           }
                        }
                                        
                  }
              
                this.invoiceHeader = arrayOfInvoices;  // store invoices array in the class variable
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot read file", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
        return invoiceHeader;
    }
    
    public void write(ArrayList<Invoice> invoices)
    {
        for(Invoice inv : invoices)
      {
          int invId = inv.getNum();
          String date = inv.getDate();
          String customer = inv.getCustomer();
          System.out.println("\n Invoice " + invId + "\n {\n " + date + "," + customer);
          ArrayList<Item> ITEMS = inv.getItems();
          for(Item item : ITEMS)
          {
              System.out.println(item.getItem() + "," + item.getPrice()+ "," + item.getCount());
          }
          
          System.out.println(" } \n");
      }
        
    }
    
     public static void main(String[] args)
   {
       FileOperations fo = new FileOperations();
       ArrayList<Invoice> invoices = fo.read();
       fo.write(invoices);
              
   }
    
     
}
