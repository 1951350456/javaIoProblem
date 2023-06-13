import java.io.*;
import java.util.*;

public class Inventory {
    public static void main(String[] args) {
        //货物信息
        TreeMap<String, InventoryItem> InventoryMap = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Inventory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] m = line.split("\t");
                String itemNumber = m[0];
                int quantity = Integer.parseInt(m[1]);
                String supplier = m[2];
                String description = m[3];
                InventoryMap.put(itemNumber, new InventoryItem(itemNumber, quantity, supplier, description));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //发货
        Vector<Transaction> O = new Vector<>();
        //到货
        Vector<Transaction> R = new Vector<>();
        //添加新货种
        Vector<Transaction> A = new Vector<>();
        //删除货种
        Vector<Transaction> D = new Vector<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null){
                String[] m = line.split("\t");
                switch (m[0]) {
                    case "O" -> {
                        String oder = m[0];
                        String item = m[1];
                        int quantity = Integer.parseInt(m[2]);
                        String customer = m[3];
                        O.add(new Transaction(oder, item, quantity, customer));
                    }
                    case "R" -> {
                        String oder1 = m[0];
                        String item1 = m[1];
                        int quantity1 = Integer.parseInt(m[2]);
                        R.add(new Transaction(oder1, item1, quantity1, ""));
                    }
                    case "A" -> {
                        String oder2 = m[0];
                        String item2 = m[1];
                        int quantity2 = Integer.parseInt(m[2]);
                        String customer2 = m[3];
                        A.add(new Transaction(oder2, item2, quantity2, customer2));
                    }
                    case "D" -> {
                        String oder3 = m[0];
                        String item3 = m[1];
                        D.add(new Transaction(oder3, item3, -1, ""));
                    }
                }
            }
            for (int i = O.size()-1; i > 0; i--) {
                for (int j = 0; j<i; j++) {
                    if(O.get(j).quantity()>O.get(j+1).quantity()) {
                        Collections.swap(O, j, j + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //处理新增货种
        for(var i:A) {
            String itemNumber = i.item();
            int quantity = 0;
            String supplier = Integer.toString(i.quantity());
            String description = i.custom();
            InventoryMap.put(itemNumber, new InventoryItem(itemNumber,quantity,supplier,description));
        }
        //处理到货
        for(var i:R) {
            String itemNumber = i.item();
            int quantity = i.quantity()+InventoryMap.get(itemNumber).quantity();
            String supplier = InventoryMap.get(itemNumber).supplier();
            String description = InventoryMap.get(itemNumber).description();
            InventoryMap.put(itemNumber,new InventoryItem(itemNumber,quantity,supplier,description));
        }
        //处理发货
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("Errors.txt"));
            BufferedWriter writer1 = new BufferedWriter(new FileWriter("Shipping.txt"));
        for(var i:O){
            String itemNumber = i.item();
            if(i.quantity()>InventoryMap.get(itemNumber).quantity()){
                writer.write(i.custom()+'\t'+i.item()+'\t'+i.quantity()+'\n');
            }
            else{
                int quantity = InventoryMap.get(itemNumber).quantity()-i.quantity();
                String supplier = InventoryMap.get(itemNumber).supplier();
                String description = InventoryMap.get(itemNumber).description();
                InventoryMap.put(itemNumber,new InventoryItem(itemNumber,quantity,supplier,description));
                writer1.write(i.custom()+'\t'+i.item()+'\t'+i.quantity()+'\n');
            }
        }
        writer.flush();
        writer1.flush();
        writer.close();
        writer1.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        //处理删除货物
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("Errors.txt",true));
            for(var i:D){
                String itemNumber = i.item();
                if(i.quantity()==0){
                    writer.write("0"+'\t'+i.item()+'\t'+InventoryMap.get(itemNumber).quantity()+'\n');
                }
                else{
                    InventoryMap.remove(itemNumber);
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        //输出最终结果
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("NewInventory.txt"));
            Set<Integer> set = new TreeSet<>();
            for (String key : InventoryMap.keySet()) {
                int intValue = Integer.parseInt(key);
                set.add(intValue);
            }
            Set<Integer> sortedSet = new TreeSet<>(set);
            for(var value : sortedSet){
                String key = value.toString();
                writer.write(key+'\t'+InventoryMap.get(key).quantity()+'\t'+InventoryMap.get(key).supplier()+'\t'+InventoryMap.get(key).description()+'\n');
            }
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

//货物信息类
record InventoryItem(String itemNumber, int quantity, String supplier, String description) {
}

//处理类
record Transaction(String oder, String item, int quantity, String custom) {
}