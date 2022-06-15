package ru.semin;

import java.util.*;
import java.util.Map.Entry;

public class SlopeOne {

    private class UserId  {

        private String value;

        public UserId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    private class ItemId  {

        private String value;

        public ItemId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

    private class Cell {
        public double value = 0;
        public int freq = 0;
    }

    private Map<String, UserId> users = new HashMap<>();
    private Map<String, ItemId> items = new HashMap<>();
    private Map<UserId, Map<ItemId, Double>> inputData = new HashMap<>();
    private Map<ItemId, Map<ItemId, Cell>> diffMatrix = new HashMap<>();

    private boolean needBuildDiffMatrix = true;

    private void buildDiffMatrix() {

        diffMatrix.clear();

        for (Map<ItemId, Double> userRates : inputData.values()) {
            for (Entry<ItemId, Double> entry : userRates.entrySet()) {

                if (!diffMatrix.containsKey(entry.getKey())) {
                    diffMatrix.put(entry.getKey(), new HashMap<ItemId, Cell>());
                }

                for (Entry<ItemId, Double> entry2 : userRates.entrySet()) {

                    Map<ItemId, Cell> itemRow = diffMatrix.get(entry.getKey());
                    Cell cell = itemRow.get(entry2.getKey());

                    if (cell == null) {
                        cell = new Cell();
                        itemRow.put(entry2.getKey(), cell);
                    }

                    cell.freq = cell.freq + 1;
                    cell.value = cell.value + (entry.getValue() - entry2.getValue());

                }

            }
        }

        for (ItemId i : diffMatrix.keySet()) {
            for (ItemId j : diffMatrix.get(i).keySet()) {
                Cell cell = diffMatrix.get(i).get(j);
                cell.value = cell.value / cell.freq;
            }
        }

    }

    public void addRate(String userId, String itemId, double rate) {

        needBuildDiffMatrix = true;

        UserId uId = users.get(userId);
        if (uId == null) {
            uId = new UserId(userId);
            users.put(userId, uId);
        }

        ItemId iId = items.get(itemId);
        if (iId == null) {
            iId = new ItemId(itemId);
            items.put(itemId, iId);
        }

        Map<ItemId, Double> userRates = inputData.get(uId);
        if (userRates == null) {
            userRates = new HashMap<>();
            inputData.put(uId, userRates);
        }

        userRates.put(iId, rate);

    }

    public Map<String, Double> predict(String userId) {

        if (needBuildDiffMatrix) {
            buildDiffMatrix();
            needBuildDiffMatrix = false;
        }

        UserId uId = users.get(userId);
        Map<ItemId, Double> userRates = inputData.get(uId);
        Map<ItemId, Cell> predMatrix = new HashMap<>();

        for (ItemId i : diffMatrix.keySet()) {
            predMatrix.put(i, new Cell());
        }

        for (ItemId j : userRates.keySet()) {
            for (ItemId k : diffMatrix.keySet()) {

                Cell diffCell = diffMatrix.get(k).get(j);
                if (diffCell == null) continue;

                Cell predCell = predMatrix.get(k);
                double newval = (diffCell.value + userRates.get(j).floatValue()) * diffCell.freq;
                predCell.value = predCell.value + newval;
                predCell.freq = predCell.freq + diffCell.freq;

            }
        }

        Map<String, Double> clean = new HashMap<>();
        for (Entry<ItemId, Cell> j : predMatrix.entrySet()) {
            if (j.getValue().freq > 0) {
                clean.put(j.getKey().getValue(), j.getValue().value / j.getValue().freq);
            }
        }

        for (ItemId j : userRates.keySet()) {
            clean.put(j.getValue(), userRates.get(j));
        }

        return clean;
    }

    public void clear() {
        users.clear();
        items.clear();
        inputData.clear();
        diffMatrix.clear();
    }

}
