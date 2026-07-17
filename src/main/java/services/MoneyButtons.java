package services;

import java.util.ArrayList;

public class MoneyButtons {
    ArrayList<String> coinButtons;
    ArrayList<String> noteButtons;

    public MoneyButtons(ArrayList<String> coinButtons, ArrayList<String> noteButtons) {
        this.coinButtons = coinButtons;
        this.noteButtons = noteButtons;
    }

    public ArrayList<String> getCoinButtons() {
        return coinButtons;
    }

    public ArrayList<String> getNoteButtons() {
        return noteButtons;
    }
}